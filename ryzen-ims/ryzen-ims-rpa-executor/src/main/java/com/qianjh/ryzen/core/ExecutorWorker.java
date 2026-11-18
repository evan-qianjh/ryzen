package com.qianjh.ryzen.core;

import com.qianjh.ryzen.browser.BrowserSession;
import com.qianjh.ryzen.browser.BrowserSessionFactory;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.model.*;
import com.qianjh.ryzen.transport.ExecutorTransport;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 执行器Worker
 *
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorWorker {

    private volatile BrowserSession session;
    private volatile Assignment assignment;

    private final ExecutorTransport transport;
    private final ExecutorProperties properties;
    private final BrowserSessionFactory browserSessionFactory;

    /**
     * 指令队列
     */
    private final BlockingQueue<ExecutorCommand> commandQueue = new LinkedBlockingQueue<>();
    /**
     * 发送队列
     */
    private final PriorityBlockingQueue<QueuedSend> sendQueue = new PriorityBlockingQueue<>();

    /**
     * 已知任务ID集合
     */
    private final Set<Long> knownTaskIds = ConcurrentHashMap.newKeySet();
    /**
     * 原子计数，任务排序用
     */
    private final AtomicLong sequence = new AtomicLong();

    private volatile boolean running = true;
    private volatile ExecutorStatus status = ExecutorStatus.WAIT_LOGIN;
    private volatile ExecutorOperationType currentOperation = ExecutorOperationType.NONE;
    private volatile Long currentTaskId;
    private volatile String errorMessage;
    private Thread workerThread;


    @PostConstruct
    public void start() {
        workerThread = new Thread(this::run, "browser-worker");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /**
     * 接受指令
     *
     * @param command 指令
     */
    public void acceptCommand(ExecutorCommand command) {
        commandQueue.offer(command);
    }

    /**
     * 接受任务
     *
     * @param task 任务
     * @return 是否接受
     */
    public boolean accept(ExecutorTaskRequest task) {
        if (task == null || task.taskId() == null) {
            throw new IllegalArgumentException("taskId must not be blank");
        }

        Assignment current = assignment;
        if (current == null || session == null || status != ExecutorStatus.READY) {
            return false;
        }

        // 忽略其他执行器任务
        if (!properties.getExecutorCode().equals(task.executorCode())
                || !current.accountCode().equals(task.accountCode())
                || !current.code().equals(task.assignmentCode())
                || !current.fencingToken().equals(task.fencingToken())
        ) {
            log.debug("Ignore task targeted at another executor/account: {}", task.taskId());
            return false;
        }
        // 忽略重复
        if (!knownTaskIds.add(task.taskId())) {
            log.info("Ignore duplicated task: {}", task.taskId());
            return false;
        }
        // 任务受理
        transport.taskAccepted(task);
        // 压入队列
        sendQueue.offer(new QueuedSend(task, sequence.incrementAndGet()));
        return true;
    }


    /**
     * 在线程中执行
     */
    private void run() {
        // Executor 启动后立即注册
        status = ExecutorStatus.IDLE;
        transport.register(snapshot());

        // 上次采集时间
        Instant lastCollectedAt = Instant.EPOCH;

        while (running) {
            try {
                // ==================================================
                // 1. 未分配账号：阻塞等待 Scheduler 命令
                // ==================================================
                if (assignment == null) {
                    status = ExecutorStatus.IDLE;

                    // 阻塞在此
                    ExecutorCommand command = commandQueue.take();
                    handleCommand(command);

                    continue;
                }

                // ==================================================
                // 2. 已分配账号，但尚未创建浏览器会话
                // ==================================================
                if (session == null) {
                    status = ExecutorStatus.INITIALIZING;

                    session = browserSessionFactory.open(assignment);

                    // 新 Session 创建后，立即触发一次采集
                    lastCollectedAt = Instant.EPOCH;

                    status = ExecutorStatus.WAIT_LOGIN;

                    log.info("Waiting for login ::: accountCode={}",
                            assignment.accountCode());
                }

                // ==================================================
                // 3. 等待登录
                // ==================================================
                if (status == ExecutorStatus.WAIT_LOGIN) {
                    // 优先处理控制命令，例如 RELEASE_ACCOUNT
                    handlePendingCommand();

                    // 命令可能已经释放账号或关闭 Session
                    if (assignment == null || session == null) {
                        continue;
                    }

                    // 已登录
                    if (session.isLoggedIn()) {
                        status = ExecutorStatus.READY;
                        errorMessage = null;

                        log.info("Account login success ::: accountCode={}", assignment.accountCode());
                    }
                    // 未登录
                    else {
                        Thread.sleep(properties.getLoginCheckInterval().toMillis());
                        continue;
                    }
                }

                // ==================================================
                // 4. 已登录：优先发送任务，按间隔执行采集
                // ==================================================
                if (status == ExecutorStatus.READY || status == ExecutorStatus.BUSY) {
                    // 优先处理控制命令
                    handlePendingCommand();

                    // 控制命令可能已经释放账号或关闭 Session
                    if (assignment == null || session == null) {
                        continue;
                    }

                    // 如果命令改变了状态，例如重新进入 WAIT_LOGIN
                    if (status != ExecutorStatus.READY && status != ExecutorStatus.BUSY) {
                        continue;
                    }

                    // ------------------------------------------
                    // 4.1 优先处理发送任务
                    // ------------------------------------------
                    QueuedSend queued = sendQueue.poll();
                    if (queued != null) {
                        executeSend(session, queued.task());
                        continue;
                    }

                    // ------------------------------------------
                    // 4.2 到达采集间隔才执行采集
                    // ------------------------------------------
                    Instant now = Instant.now();

                    if (!now.isBefore(lastCollectedAt.plus(properties.getCollectInterval()))) {

                        executeCollect(session);

                        lastCollectedAt = Instant.now();
                    } else {
                        // 高频轮询发送任务，避免发送延迟
                        Thread.sleep(250);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                status = ExecutorStatus.ERROR;
                errorMessage = e.getMessage();

                log.error("Executor browser loop failed", e);

                // 关闭异常的浏览器会话
                closeSession();

                // 注意：保留 assignment。
                // 账号租约由 Scheduler 管理，不能因为 Session 异常就认为账号已释放。
                if (running) {
                    try {
                        Thread.sleep(3_000);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // ==================================================
        // 5. Executor 停止
        // ==================================================
        closeSession();

        status = ExecutorStatus.OFFLINE;
    }


    /**
     * 快照
     *
     * @return 快照
     */
    public ExecutorSnapshot snapshot() {
        Assignment current = assignment;

        return ExecutorSnapshot.builder()
                .imType(ImType.MEI_TUAN)
                .executorCode(properties.getExecutorCode())
                .accountCode(current == null ? null : current.accountCode())
                .assignmentCode(current == null ? null : current.code())
                .fencingToken(current == null ? null : current.fencingToken())
                .status(status)
                .currentOperation(currentOperation)
                .currentTaskId(currentTaskId)
                .errorMessage(errorMessage)
                .time(System.currentTimeMillis())
                .build();
    }

    /**
     * 处理待执行的指令
     */
    private void handlePendingCommand() {
        ExecutorCommand command;

        while ((command = commandQueue.poll()) != null) {
            handleCommand(command);
            // 可能在指令中销毁会话
            if (session == null) {
                return;
            }
        }
    }

    /**
     * 处理指令
     *
     * @param command 指令
     */
    private void handleCommand(ExecutorCommand command) {
        // 非当前执行器
        if (!properties.getExecutorCode().equals(command.executorCode())) {
            return;
        }

        switch (command.type()) {
            case ASSIGN -> assign(command.assignment());
            case RELEASE -> release();
            case RESTART -> restart();
            case STOP -> running = false;
        }
    }

    /**
     * 分配账号
     *
     * @param newAssignment 新的指派
     */
    private void assign(Assignment newAssignment) {
        if (assignment != null) {
            log.warn("Executor already has an account assigned");
            return;
        }
        if (newAssignment == null) {
            throw new IllegalArgumentException("assignment is required");
        }

        assignment = newAssignment;

        status = ExecutorStatus.INITIALIZING;

        log.info("Account assigned ::: accountCode={}", assignment.accountCode());
    }

    /**
     * 释放账号
     */
    private void release() {
        Assignment current = this.assignment;

        if (current == null) {
            this.status = ExecutorStatus.IDLE;
            return;
        }

        log.info("释放账号, accountCode={}, code={}", current.accountCode(), current.code());

        // 停止接收新任务
        this.status = ExecutorStatus.INITIALIZING;

        // 取消未执行任务
        cancelPendingTasks("ACCOUNT_RELEASED");

        // 关闭浏览器
        closeSession();

        // 清理账号上下文
        this.assignment = null;

        // 恢复空闲状态
        this.status = ExecutorStatus.IDLE;

        log.info("账号释放完成, executorKey={}, accountCode={}", properties.getExecutorCode(), current.accountCode());
    }

    /**
     * 销毁浏览器会话
     */
    private void closeSession() {
        BrowserSession current = session;
        session = null;

        if (current != null) {
            try {
                current.close();
            } catch (Exception e) {
                log.error("Failed to close browser session", e);
            }
        }
    }

    /**
     * 执行采集
     *
     * @param session 会话
     */
    private void executeCollect(BrowserSession session) {
        status = ExecutorStatus.BUSY;
        currentOperation = ExecutorOperationType.COLLECT;
        currentTaskId = null;
        try {
            for (ExecutorMessage message : session.collectMessages()) {
                transport.collectedMessage(message);
            }
        } catch (Exception e) {
            errorMessage = "COLLECT: " + e.getMessage();
            log.error("Message collection failed", e);
        } finally {
            currentOperation = ExecutorOperationType.NONE;
            status = ExecutorStatus.READY;
        }
    }

    /**
     * 执行发消息
     *
     * @param session 会话
     * @param task    任务
     */
    private void executeSend(BrowserSession session, ExecutorTaskRequest task) {
        status = ExecutorStatus.BUSY;
        currentOperation = ExecutorOperationType.SEND;
        currentTaskId = task.taskId();
        transport.taskRunning(task);
        try {
            // 执行并拿到回执(消息ID)
            String receiptCode = session.sendMessage(task.conversationCode(), task.content());

            ExecutorTaskResponse result = ExecutorTaskResponse.builder()
                    .imType(ImType.MEI_TUAN)
                    .executorCode(properties.getExecutorCode())
                    .accountCode(assignment.accountCode())
                    .taskId(task.taskId())
                    .status(ExecutorTaskStatus.SUCCESS)
                    .errorMessage(null)
                    .finishedAt(System.currentTimeMillis())
                    .receiptCode(receiptCode)
                    .build();
            transport.taskFinished(result);
        } catch (Exception e) {
            // A browser exception can happen after the platform accepted the message.
            // UNKNOWN prevents the scheduler from blindly retrying and sending duplicates.
            errorMessage = "SEND: " + e.getMessage();
            log.error("Send task outcome is unknown: {}", task.taskId(), e);
            ExecutorTaskResponse result = ExecutorTaskResponse.builder()
                    .imType(ImType.MEI_TUAN)
                    .executorCode(properties.getExecutorCode())
                    .accountCode(assignment.accountCode())
                    .taskId(task.taskId())
                    .status(ExecutorTaskStatus.UNKNOWN)
                    .errorMessage(e.getMessage())
                    .finishedAt(System.currentTimeMillis())
                    .build();
            transport.taskFinished(result);
        } finally {
            currentTaskId = null;
            currentOperation = ExecutorOperationType.NONE;
            status = ExecutorStatus.READY;
        }
    }

    /**
     * 取消待执行的任务
     *
     * @param reason 原因
     */
    private void cancelPendingTasks(String reason) {
        Assignment current = this.assignment;

        if (current == null) {
            return;
        }

        List<QueuedSend> cancelledTasks = new ArrayList<>();

        sendQueue.drainTo(cancelledTasks);

        for (QueuedSend queue : cancelledTasks) {
            ExecutorTaskRequest task = queue.task;
            // 只取消当前账号分配下的任务
            if (!Objects.equals(task.assignmentCode(), current.code())) {
                // 理论上不应该出现其他分配的任务，避免误取消
                sendQueue.offer(queue);
                continue;
            }

            try {
                transport.taskFinished(
                        ExecutorTaskResponse.builder()
                                .imType(task.imType())
                                .executorCode(properties.getExecutorCode())
                                .accountCode(current.accountCode())
                                .taskId(task.taskId())
                                .status(ExecutorTaskStatus.CANCELED)
                                .errorMessage(reason)
                                .finishedAt(System.currentTimeMillis())
                                .build()
                );
            } catch (Exception e) {
                log.error("上报任务取消结果失败, taskId={}", task.taskId(), e);
            }
        }

        log.info("取消待执行任务, executorKey={}, accountKey={}, count={}",
                properties.getExecutorCode(),
                current.accountCode(),
                cancelledTasks.size());
    }

    /**
     * 重启会话
     */
    private void restart() {
        Assignment current = this.assignment;

        if (current == null) {
            log.warn("当前没有账号分配，无法重启 Session");
            return;
        }

        log.info("准备重启浏览器会话, executorKey={}, accountKey={}",
                properties.getExecutorCode(),
                current.accountCode());

        // 停止接收新任务
        this.status = ExecutorStatus.INITIALIZING;

        // 取消尚未执行的任务
        cancelPendingTasks("SESSION_RESTART");

        // 关闭旧会话
        closeSession();

        try {
            // 创建新会话
            this.session = browserSessionFactory.open(current);

            // 进入登录等待状态
            this.status = ExecutorStatus.WAIT_LOGIN;

            log.info("浏览器会话重启完成，等待登录, accountKey={}",
                    current.accountCode());

        } catch (Exception e) {
            this.status = ExecutorStatus.ERROR;

            log.error("浏览器会话重启失败, accountKey={}",
                    current.accountCode(), e);

            // TODO reportExecutorError(e);
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
            try {
                workerThread.join(5_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private record QueuedSend(ExecutorTaskRequest task, long sequence) implements Comparable<QueuedSend> {
        @Override
        public int compareTo(QueuedSend other) {
            return Long.compare(sequence, other.sequence);
        }
    }
}
