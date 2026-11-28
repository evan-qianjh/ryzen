package com.qianjh.ryzen.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.ims.dto.Assignment;
import com.qianjh.ryzen.ims.dto.ExecutorMessage;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MessageDirection;
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
            String chatTitle = getChatTitle(chat);
            String chatAvatar = getChatAvatar(chat);

            log.info("存在进行中对话框 ::: chatCode={}, sender={}", chatCode, chatTitle);
            result.addAll(
                    parseMessageArea(chatCode, chatTitle, chatAvatar)
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
            String chatTitle = getChatTitle(chat);
            String chatAvatar = getChatAvatar(chat);

            // 点击对话框
            chat.click();
            page.waitForTimeout(1000);

            // 解析
            result.addAll(
                    parseMessageArea(chatCode, chatTitle, chatAvatar)
            );
        }

        log.info("采集消息 ::: size={}", result.size());

        return result;
    }

    // TODO 编辑昵称？

    private String getChatTitle(Locator chat) {
        return chat.locator("span[class*='style-module_sessionNickname_']").textContent();
    }

    private String getChatAvatar(Locator chat) {
        return chat.locator("img[class*='style-module_sessionAvatar_']").getAttribute("src");
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
    private List<ExecutorMessage> parseMessageArea(String chatCode, String chatName, String chatAvatar) {
        log.info("处理对话框 ::: chatId={}, chatName={}", chatCode, chatName);

        // 客户名 标题
        Locator customerNameNode = page.locator("span[title='点击编辑昵称']");
        String conversationTitle = customerNameNode.count() > 0 ? customerNameNode.textContent() : chatName;

        // 门店 副标题
        Locator shopNameNode = page.locator("span[class*='style-module_shopName_']");
        String conversationSubTitle = shopNameNode.count() > 0 ? shopNameNode.textContent() : null;

        // 获取发来的消息列表
        Locator messageHits = page.locator("div[class*='message-wrapper']");
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
            if (messageId.startsWith("tmp_")) {
                log.warn("异常消息 ::: {}", messageId);
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
            String content = parseContent(contentNode);

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
                            .conversationTitle(conversationTitle)
                            .conversationSubTitle(conversationSubTitle)
                            .conversationAvatar(chatAvatar)
                            .senderCode(null)
                            .senderName(senderName)
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

        // 消息重排序
        result.sort(Comparator.comparing(ExecutorMessage::messageCode));

        return result;
    }

    private String parseContent(Locator contentNode) {
        Object evaluate = contentNode.evaluate("""
                element => {
                    function extract(node) {
                        if (node.nodeType === Node.TEXT_NODE) {
                            return node.textContent;
                        }
                
                        if (node.nodeType === Node.ELEMENT_NODE) {
                            if (node.classList.contains('im-card_emoji-icon')) {
                                const emojiMap = {
                                    'im-card_emoji-icon_smile': '[微笑]',
                                    'im-card_emoji-icon_pout': '[撇嘴]',
                                    'im-card_emoji-icon_flushed': '[色]',
                                    'im-card_emoji-icon_daze': '[发呆]',
                                    'im-card_emoji-icon_smirk': '[得意]',
                                    'im-card_emoji-icon_tear': '[流泪]',
                                    'im-card_emoji-icon_shy': '[害羞]',
                                    'im-card_emoji-icon_shut': '[闭嘴]',
                                    'im-card_emoji-icon_sleep': '[睡]',
                                    'im-card_emoji-icon_cry': '[大哭]',
                                    'im-card_emoji-icon_awkward': '[尴尬]',
                                    'im-card_emoji-icon_angry': '[发怒]',
                                    'im-card_emoji-icon_naughty': '[调皮]',
                                    'im-card_emoji-icon_grin': '[呲牙]',
                                    'im-card_emoji-icon_surprise': '[惊讶]',
                                    'im-card_emoji-icon_sad': '[难过]',
                                    'im-card_emoji-icon_cool': '[酷]',
                                    'im-card_emoji-icon_embarrassed': '[囧]',
                                    'im-card_emoji-icon_crazy': '[抓狂]',
                                    'im-card_emoji-icon_vomit': '[吐]',
                                    'im-card_emoji-icon_giggle': '[偷笑]',
                                    'im-card_emoji-icon_joy': '[愉快]',
                                    'im-card_emoji-icon_whiteeye': '[白眼]',
                                    'im-card_emoji-icon_arrogant': '[傲慢]',
                                    'im-card_emoji-icon_hunger': '[饥饿]',
                                    'im-card_emoji-icon_sleepy': '[困]',
                                    'im-card_emoji-icon_panic': '[惊恐]',
                                    'im-card_emoji-icon_silly': '[流汗]',
                                    'im-card_emoji-icon_leisurely': '[憨笑]',
                                    'im-card_emoji-icon_curse': '[咒骂]',
                                    'im-card_emoji-icon_doubt': '[疑问]',
                                    'im-card_emoji-icon_shush': '[嘘]',
                                    'im-card_emoji-icon_dizzy': '[晕]',
                                    'im-card_emoji-icon_fengle': '[疯了]',
                                    'im-card_emoji-icon_unlucky': '[衰]',
                                    'im-card_emoji-icon_skull': '[骷髅]',
                                    'im-card_emoji-icon_knock': '[敲打]',
                                    'im-card_emoji-icon_goodbye': '[再见]',
                                    'im-card_emoji-icon_wipesweat': '[擦汗]',
                                    'im-card_emoji-icon_picknose': '[抠鼻]',
                                    'im-card_emoji-icon_applause': '[鼓掌]',
                                    'im-card_emoji-icon_qiudale': '[糗大了]',
                                    'im-card_emoji-icon_sinistersmile': '[坏笑]',
                                    'im-card_emoji-icon_zhh': '[左哼哼]',
                                    'im-card_emoji-icon_hum': '[右哼哼]',
                                    'im-card_emoji-icon_haqian': '[哈欠]',
                                    'im-card_emoji-icon_despise': '[鄙视]',
                                    'im-card_emoji-icon_wronged': '[委屈]',
                                    'im-card_emoji-icon_teary': '[快哭了]',
                                    'im-card_emoji-icon_scheming': '[阴险]',
                                    'im-card_emoji-icon_kiss': '[亲亲]',
                                    'im-card_emoji-icon_pitiful': '[可怜]',
                                    'im-card_emoji-icon_smilingface': '[笑脸]',
                                    'im-card_emoji-icon_sick': '[生病]',
                                    'im-card_emoji-icon_blush': '[脸红]',
                                    'im-card_emoji-icon_laughcry': '[破涕为笑]',
                                    'im-card_emoji-icon_fear': '[恐惧]',
                                    'im-card_emoji-icon_disappointed': '[失望]',
                                    'im-card_emoji-icon_astonished': '[吃惊]',
                                    'im-card_emoji-icon_heha': '[嘿哈]',
                                    'im-card_emoji-icon_facepalm': '[捂脸]',
                                    'im-card_emoji-icon_sly': '[奸笑]',
                                    'im-card_emoji-icon_clever': '[机智]',
                                    'im-card_emoji-icon_frown': '[皱眉]',
                                    'im-card_emoji-icon_yeah': '[耶]',
                                    'im-card_emoji-icon_melon': '[吃瓜]',
                                    'im-card_emoji-icon_fighting': '[加油]',
                                    'im-card_emoji-icon_sweat': '[汗]',
                                    'im-card_emoji-icon_omg': '[OMG]',
                                    'im-card_emoji-icon_emm': '[嗯嗯]',
                                    'im-card_emoji-icon_society': '[社会]',
                                    'im-card_emoji-icon_wangchai': '[旺财]',
                                    'im-card_emoji-icon_approved': '[好的]',
                                    'im-card_emoji-icon_slapface': '[打脸]',
                                    'im-card_emoji-icon_wow': '[哇]',
                                    'im-card_emoji-icon_rolleyes': '[翻白眼]',
                                    'im-card_emoji-icon_666': '[666]',
                                    'im-card_emoji-icon_letmesee': '[让我看看]',
                                    'im-card_emoji-icon_sigh': '[叹气]',
                                    'im-card_emoji-icon_bitter': '[苦涩]',
                                    'im-card_emoji-icon_caidao': '[菜刀]',
                                    'im-card_emoji-icon_watermelon': '[西瓜]',
                                    'im-card_emoji-icon_beer': '[啤酒]',
                                    'im-card_emoji-icon_basketball': '[篮球]',
                                    'im-card_emoji-icon_pingpang': '[乒乓]',
                                    'im-card_emoji-icon_coffee': '[咖啡]',
                                    'im-card_emoji-icon_rice': '[饭]',
                                    'im-card_emoji-icon_pig': '[猪头]',
                                    'im-card_emoji-icon_rose': '[玫瑰]',
                                    'im-card_emoji-icon_diaoxie': '[凋谢]',
                                    'im-card_emoji-icon_lips': '[嘴唇]',
                                    'im-card_emoji-icon_heart': '[爱心]',
                                    'im-card_emoji-icon_brokenheart': '[心碎]',
                                    'im-card_emoji-icon_cake': '[蛋糕]',
                                    'im-card_emoji-icon_shandian': '[闪电]',
                                    'im-card_emoji-icon_zhadan': '[炸弹]',
                                    'im-card_emoji-icon_dao': '[刀]',
                                    'im-card_emoji-icon_football': '[足球]',
                                    'im-card_emoji-icon_piaocong': '[瓢虫]',
                                    'im-card_emoji-icon_bianbian': '[便便]',
                                    'im-card_emoji-icon_moon': '[月亮]',
                                    'im-card_emoji-icon_sun': '[太阳]',
                                    'im-card_emoji-icon_gift': '[礼物]',
                                    'im-card_emoji-icon_hug': '[拥抱]',
                                    'im-card_emoji-icon_strong': '[强]',
                                    'im-card_emoji-icon_weak': '[弱]',
                                    'im-card_emoji-icon_handshake': '[握手]',
                                    'im-card_emoji-icon_victory': '[胜利]',
                                    'im-card_emoji-icon_fistpalm': '[抱拳]',
                                    'im-card_emoji-icon_hook': '[勾引]',
                                    'im-card_emoji-icon_fist': '[拳头]',
                                    'im-card_emoji-icon_chajin': '[差劲]',
                                    'im-card_emoji-icon_aini': '[爱你]',
                                    'im-card_emoji-icon_NO': '[NO]',
                                    'im-card_emoji-icon_ok': '[OK]',
                                    'im-card_emoji-icon_pray': '[祈祷]'
                                };
                
                                for (const cls of node.classList) {
                                    if (emojiMap[cls]) {
                                        return emojiMap[cls];
                                    }
                                }
                
                                return '[表情]';
                            }
                
                            return Array.from(node.childNodes)
                                .map(extract)
                                .join('');
                        }
                
                        return '';
                    }
                
                    return extract(element);
                }
                """);
        return evaluate == null ? null : evaluate.toString();
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
