package com.qianjh.ryzen.core;

import com.qianjh.ryzen.browser.BrowserSession;
import com.qianjh.ryzen.browser.BrowserSessionFactory;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.model.ExecutorMessage;
import com.qianjh.ryzen.model.ExecutorSnapshot;
import com.qianjh.ryzen.model.ExecutorTaskRequest;
import com.qianjh.ryzen.model.ExecutorTaskResponse;
import com.qianjh.ryzen.transport.ExecutorTransport;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    private final ExecutorTransport transport;
    private final ExecutorProperties properties;
    private final BrowserSessionFactory browserSessionFactory;

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
     * 接受任务
     *
     * @param task 任务
     * @return 是否接受
     */
    public boolean accept(ExecutorTaskRequest task) {
        if (task == null || task.taskId() == null) {
            throw new IllegalArgumentException("taskId must not be blank");
        }
        // 忽略其他执行器任务
        if (!properties.getExecutorCode().equals(task.executorCode())
                || !properties.getAccountCode().equals(task.accountCode())) {
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
     * 快照
     *
     * @return 快照
     */
    public ExecutorSnapshot snapshot() {
        return ExecutorSnapshot.builder()
                .imType(ImType.MEI_TUAN)
                .executorCode(properties.getExecutorCode())
                .accountCode(properties.getAccountCode())
                .status(status)
                .currentOperation(currentOperation)
                .currentTaskId(currentTaskId)
                .errorMessage(errorMessage)
                .time(System.currentTimeMillis())
                .build();
    }

    /**
     * 在线程中执行
     */
    private void run() {
        while (running) {
            // 打开浏览器
            try (BrowserSession session = browserSessionFactory.open()) {
                // 等待登录
                waitForLogin(session);
                if (!running) {
                    break;
                }

                // 状态就绪
                status = ExecutorStatus.READY;
                // 注册到调度器
                transport.register(snapshot());
                Instant lastCollectedAt = Instant.EPOCH;

                while (running) {
                    // 获取待发送消息
                    QueuedSend queued = sendQueue.poll();

                    // 如果有待发送消息，先发消息
                    if (queued != null) {
                        executeSend(session, queued.task());
                        continue;
                    }

                    // 到了采集间隔，执行采集
                    if (Instant.now().isAfter(lastCollectedAt.plus(properties.getCollectInterval()))) {
                        executeCollect(session);
                        lastCollectedAt = Instant.now();
                    } else {
                        // Polling keeps send latency low without creating a separate collect task.
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
                if (running) {
                    try {
                        Thread.sleep(3_000);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } finally {
                currentOperation = ExecutorOperationType.NONE;
                currentTaskId = null;
            }
        }

        // 离线
        status = ExecutorStatus.OFFLINE;
    }

    /**
     * 等待登录
     *
     * @param session
     * @throws InterruptedException
     */
    private void waitForLogin(BrowserSession session) throws InterruptedException {
        status = ExecutorStatus.WAIT_LOGIN;
        log.info("Waiting for manual login. Complete login in the opened browser window.");
        while (running) {
            try {
                if (session.isLoggedIn()) {
                    log.info("Login detected for account {}", properties.getAccountCode());
                    errorMessage = null;
                    return;
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                log.debug("Login is not ready yet: {}", e.getMessage());
            }
            // 等待
            Thread.sleep(properties.getLoginCheckInterval());
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
                    .accountCode(properties.getAccountCode())
                    .taskId(task.taskId())
                    .status("SUCCESS")
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
                    .accountCode(properties.getAccountCode())
                    .taskId(task.taskId())
                    .status("UNKNOWN")
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
