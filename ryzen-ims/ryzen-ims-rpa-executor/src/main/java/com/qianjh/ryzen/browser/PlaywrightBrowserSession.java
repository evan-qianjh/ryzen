package com.qianjh.ryzen.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.enums.MessageDirection;
import com.qianjh.ryzen.model.Assignment;
import com.qianjh.ryzen.model.ExecutorMessage;
import com.qianjh.ryzen.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Playwright实现的浏览器会话
 * <p>
 * 最小化页面适配器，具体的选择器及消息DOM解析逻辑与平台相关，特意保留在此类中，以避免将Playwright引入调度器。
 * <p>
 * Minimal page adapter. The actual selectors and message DOM parsing are platform-specific
 * and intentionally kept in this class rather than leaking Playwright into the scheduler.
 *
 * @author QianJH
 */
@Slf4j
@RequiredArgsConstructor
public class PlaywrightBrowserSession implements BrowserSession {
    private final Playwright playwright;
    private final BrowserContext context;
    private final Page page;
    private final ExecutorProperties properties;
    private final Assignment assignment;

    // 对话最后一条消息ID
    private final Map<String, Long> CHAT_LAST_MSG = new HashMap<>();

    @Override
    public boolean isLoggedIn() {
        String selector = assignment.imType().getLoginSuccessSelector();
        if (selector == null || selector.isBlank()) {
            throw new IllegalStateException("Configure ryzen.executor.login-success-selector before starting the executor");
        }
        return page.locator(selector).first().isVisible();
    }

    @Override
    public List<ExecutorMessage> collectMessages() {
        // 采集的消息
        List<ExecutorMessage> result = new ArrayList<>();

        // 左侧会话面板
        Locator sessionsPanel = page.locator("#session-list-panel");
        sessionsPanel.waitFor();

        /* *************************************** */
        log.debug("点击“全部接待”");
        Locator tabAll = sessionsPanel.getByText("全部接待");
        tabAll.waitFor();
        tabAll.click();

        /* *************************************** */
        // 获取二级tab
        page.waitForTimeout(200);
        Locator subTabs = sessionsPanel.locator("span[class*='style-module_filterChip_']");

        /* *************************************** */
        log.debug("点击“全部”");
        Locator subTabUnread = subTabs.getByText("全部");
        subTabUnread.waitFor();
        subTabUnread.click();

        /* *************************************** */
        // 判断当前是否有选中对话框，有则解析
        Locator currentChat = page.locator("div[class*='style-module_sessionItemActive_']");
        if (currentChat.count() > 0) {
            Locator chat = currentChat.locator("xpath=..");
            String chatCode = getChatCode(chat);
            String sender = getChatSender(chat);

            log.info("存在进行中对话框 ::: chatCode={}, sender={}", chatCode, sender);
            result.addAll(
                    parseMessageArea(chatCode, sender)
            );
        }

        // 判断是否有未读对话
        Locator unreadFlags = page.locator("span[class*='style-module_unreadBadge_']");
        log.atLevel(unreadFlags.count() > 0 ? Level.INFO : Level.DEBUG)
                .log("发现未读对话框 ::: count={}", unreadFlags.count());
        for (int i = 0; i < unreadFlags.count(); i++) {
            Locator unreadFlag = unreadFlags.nth(i);
            Locator chat = unreadFlag.locator("xpath=ancestor::div[@data-conversation-id][1]");
            String chatCode = getChatCode(chat);
            String sender = getChatSender(chat);

            // 点击对话框
            chat.click();
            page.waitForTimeout(1000);

            // 解析
            result.addAll(
                    parseMessageArea(chatCode, sender)
            );
        }

        log.info("采集消息 ::: size={}", result.size());

        return result;
    }

    // TODO 编辑昵称？

    private String getChatSender(Locator chat) {
        return chat.locator("span[class*='style-module_sessionNickname_']").textContent();
    }

    private String getChatCode(Locator chat) {
        return chat.getAttribute("data-conversation-id");
    }

    /**
     * 解析消息区域
     *
     * @param chatCode 对话
     * @return 消息
     */
    private List<ExecutorMessage> parseMessageArea(String chatCode, String chatName) {
        log.info("处理对话框 ::: chatId={}, chatName={}", chatCode, chatName);

        // 获取发来的消息列表
        Locator messageHits = page.locator("div[class*=message-wrapper]");
        int hitsCount = messageHits.count();
        if (hitsCount == 0) {
            return Collections.emptyList();
        }

        List<ExecutorMessage> result = new ArrayList<>();

        // 遍历消息
        Long lastMessageSeq = CHAT_LAST_MSG.get(chatCode);
        Long maxMessageSeq = null;
        for (int k = 0; k < Math.min(hitsCount, 10); k++) {
            Locator messageHit = messageHits.nth(k);
            Locator message = messageHit.locator("xpath=ancestor::div[@data-msg-id][1]");

            String messageId = message.getAttribute("data-msg-id");
            if (messageId == null) {
                continue;
            }

            // 去重
            long messageSeq = Long.parseLong(messageId);
            if (lastMessageSeq != null && messageSeq <= lastMessageSeq) {
                continue;
            }
            maxMessageSeq = maxMessageSeq == null ? messageSeq : Math.max(maxMessageSeq, messageSeq);

            // 方向
            String clazz = messageHit.getAttribute("class");
            if (!StringUtils.hasText(clazz)) {
                log.error("获取消息方向失败 ::: messageHit={}, index={}", messageHit, k);
                continue;
            }
            MessageDirection direction = null;
            if (clazz.contains("left-message")) {
                direction = MessageDirection.INBOUND;
            } else if (clazz.contains("right-message")) {
                direction = MessageDirection.OUTBOUND;
            }

            // 获取头像
            Locator avatarNode = message.locator(".sender-avatar >> img");
            String avatar = avatarNode.count() > 0 ? avatarNode.getAttribute("src") : null;

            // 获取昵称
            Locator senderNode = message.locator(".sender-nickname");
            String senderName = (String) senderNode.evaluate(
                    "el => [...el.childNodes]" +
                            ".filter(node => node.nodeType === Node.TEXT_NODE)" +
                            ".map(node => node.textContent)" +
                            ".join('').trim()"
            );

            // 发送时间
            Locator timeNode = message.locator(".message-formattime");
            String time = timeNode.textContent();
            Long sentTime = MessageUtils.toTimestamp(time);


            Locator contentNode = message.locator(".im-card_text-message");
            String content = contentNode.textContent();
            log.info("获取消息 ::: chatId={}, sender={}, messageCode={}, content={}, sentTime={}",
                    chatCode, senderName, messageId, content, sentTime);

            result.add(
                    ExecutorMessage.builder()
                            .imType(ImType.MEI_TUAN)
                            .direction(direction)
                            .executorCode(properties.getCode())
                            .accountCode(assignment.accountCode())
                            .messageCode(messageId)
                            .conversationCode(chatCode)
                            .senderCode(senderName)
                            .senderAvatar(avatar)
                            .content(content)
                            .sentTime(sentTime)
                            .build()
            );
        }

        // 更新最新消息
        if (maxMessageSeq != null) {
            CHAT_LAST_MSG.put(chatCode, maxMessageSeq);
        }

        return result;
    }

    @Override
    public String sendMessage(String conversationCode, String content) {
        // 左侧会话面板
        Locator sessionsPanel = page.locator("#session-list-panel");
        sessionsPanel.waitFor();

        log.debug("点击“全部接待”");
        Locator tabAll = sessionsPanel.getByText("全部接待");
        tabAll.waitFor();
        tabAll.click();

        // 获取二级tab
        page.waitForTimeout(200);
        Locator subTabs = sessionsPanel.locator("span[class*='style-module_filterChip_']");

        log.debug("点击“全部”");
        Locator subTabUnread = subTabs.getByText("全部");
        subTabUnread.waitFor();
        subTabUnread.click();

        /* *************************************** */
        // 获取对话列表
        page.waitForTimeout(1000);
        Locator chat = page.locator("div[data-conversation-id='" + conversationCode + "']");
        if (chat.count() == 0) {
            log.error("未定位到聊天对话 ::: chatCode={}", conversationCode);
            throw new RuntimeException("未定位到聊天对话");
        }
        chat.click();

        // 等待消息列表加载完毕
        page.locator("#imMessageList").waitFor();


        // 输入消息
        page.waitForTimeout(1000);
        Locator input = page.locator(".roo-input-textarea");
//        input.pressSequentially("还在吗？？");
        input.fill(content);

        // 发送消息
        page.waitForTimeout(1000);
        page.locator(".roo-btn").click();

        log.info("send {}", content);

        page.waitForTimeout(1000);

        // 获取回执(发出消息的消息ID)

        // 已发出消息
        Locator outboundMessages = page.locator("div[class*=right-message]");
        if (outboundMessages.count() == 0) {
            log.error("获取回执失败 ::: 获取发出消息列表失败 ::: conversationCode={}, content={}", conversationCode, content);
            return null;
        }
        Locator outboundMessage = outboundMessages.nth(0);
        Locator message = outboundMessage.locator("xpath=ancestor::div[@data-msg-id][1]");
        if (message.count() == 0) {
            log.error("获取回执失败 ::: 获取最新发出消息失败 ::: conversationCode={}, content={}", conversationCode, content);
            return null;
        }
        String messageId = message.getAttribute("data-msg-id");
        if (messageId == null) {
            log.error("获取回执失败 ::: 获取最新发出消息id失败 ::: conversationCode={}, content={}", conversationCode, content);
            return null;
        }
        return messageId;
    }

    @Override
    public void close() {
        try {
            context.close();
        } finally {
            playwright.close();
        }
    }
}
