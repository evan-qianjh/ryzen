package com.qianjh.ryzen.netty.handler;

import com.google.gson.Gson;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.exception.BusinessException;
import com.qianjh.ryzen.exception.UnauthorizedException;
import com.qianjh.ryzen.handler.ActionHandler;
import com.qianjh.ryzen.handler.ProcessHandler;
import com.qianjh.ryzen.message.Message;
import com.qianjh.ryzen.message.RequestMessage;
import com.qianjh.ryzen.message.ResponseMessage;
import com.qianjh.ryzen.netty.ClientManager;
import com.qianjh.ryzen.netty.MessageSender;
import com.qianjh.ryzen.util.SpringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author QianJH
 */
@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger WEBSOCKET_CONNECT_LOGGER = LoggerFactory.getLogger("WEBSOCKET_CONNECT_LOGGER");
    private static final Logger WEBSOCKET_RECEIVE_LOGGER = LoggerFactory.getLogger("WEBSOCKET_RECEIVE_LOGGER");
    private static final Logger WEBSOCKET_ERROR_LOGGER = LoggerFactory.getLogger("WEBSOCKET_ERROR_LOGGER");


    private static final String PING = "ping";
    public static final String PONG = "pong";
    private static final String INFO = "info";
    private static final String SUBSCRIBED = "subscribed";

    private static final String MESSAGE_START_WITH = "{";
    private static final Gson GSON = new Gson();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            ClientManager.add(ctx.channel()); // TODO 取不到IP
            WEBSOCKET_CONNECT_LOGGER.debug("[+][{}]->[{}], tenant={}, ip={}", ctx.channel().id().asShortText(), ClientManager.getOnline(), getTenantId(ctx), getClientIp(ctx));
        } finally {
            super.channelActive(ctx);
        }
    }

    // 断开结果通知
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            ClientManager.onClosed(ctx.channel());
            WEBSOCKET_CONNECT_LOGGER.debug("[-][{}]->[{}], tenant={}, ip={}", ctx.channel().id().asShortText(), ClientManager.getOnline(), getTenantId(ctx), getClientIp(ctx));
        } finally {
            super.channelInactive(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        WEBSOCKET_ERROR_LOGGER.error("[error] [{}], tenant={}, ip={}", ctx.channel().id().asShortText(), getTenantId(ctx), getClientIp(ctx), cause);
        ClientManager.close(ctx.channel());

        // 异常不在往上层抛
//        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping
        if (frame instanceof PingWebSocketFrame) {
            log.debug("receive ping");
            ctx.write(new PongWebSocketFrame(frame.content().retain()));
        }
        // pong
        if (frame instanceof PongWebSocketFrame) {
            log.debug("receive pong");
        }
        // close
        if (frame instanceof CloseWebSocketFrame) {
            ClientManager.close(ctx.channel());
        }
        //
        if (frame instanceof TextWebSocketFrame) {
            // 会话标识
            ChannelId clientId = ctx.channel().id();
            String clientIp = getClientIp(ctx);

            // 接收到的消息
            String message = ((TextWebSocketFrame) frame).text().trim();

            // 处理消息
            handleMessage(ctx, clientId, message);
        }
    }

    /**
     * 获取真实IP
     *
     * @param ctx 上下文
     * @return ip
     */
    public static String getClientIp(ChannelHandlerContext ctx) {
        Attribute<Object> attr = ctx.channel().attr(AttributeKey.valueOf(HeaderHandler.CLIENT_IP));
        Object obj = attr.get();
        return obj == null ? null : String.valueOf(obj);
    }

    /**
     * 获取租户ID
     *
     * @param ctx 上下文
     * @return 租户ID
     */
    public static Long getTenantId(ChannelHandlerContext ctx) {
        Attribute<Object> attr = ctx.channel().attr(AttributeKey.valueOf(HeaderHandler.TENANT_ID));
        Object obj = attr.get();
        if (Objects.isNull(obj)) {
            return null;
        }
        String objStr = String.valueOf(obj);
        if (StringUtils.isBlank(objStr)) {
            return null;
        }
        return Long.valueOf(objStr);
    }

    /**
     * 处理消息
     *
     * @param ctx      ctx
     * @param clientId 会话
     * @param message  消息
     * @throws Exception 异常
     */
    private void handleMessage(ChannelHandlerContext ctx, ChannelId clientId, String message) throws Exception {
        //
        Long tenantId = getTenantId(ctx);

        // ping
        if (PING.equalsIgnoreCase(message)) {
            MessageSender.send(clientId, Message.build(PONG), PONG);
        }
        // 信息
        else if (INFO.equalsIgnoreCase(message)) {
            WEBSOCKET_RECEIVE_LOGGER.debug("tenantId={}, clientId={}, message={}", tenantId, clientId, message);
            respInfo(clientId);
        }
        // 已订阅
        else if (SUBSCRIBED.equalsIgnoreCase(message)) {
            WEBSOCKET_RECEIVE_LOGGER.debug("tenantId={}, clientId={}, message={}", tenantId, clientId, message);
//            SubscribeService subscribeService = SpringUtils.getApplicationContext().getBean(SubscribeService.class);
//            List<?> subscribes = subscribeService.getSubscribed(clientId);
//            MessageSender.send(clientId, GSON.toJson(subscribes), "resp subscribed");
        }
        // 业务
        else if (message.startsWith(MESSAGE_START_WITH)) {
            WEBSOCKET_RECEIVE_LOGGER.debug("[↓][{}] ip={} ::: {}", clientId.asShortText(), getClientIp(ctx), message);

            // 转换请求报文
            RequestMessage request = GSON.fromJson(message, RequestMessage.class);

            try {
                // 处理动作
                if (request.getAction() != null) {
                    ActionHandler actionHandler = SpringUtils.getApplicationContext().getBean(ActionHandler.class);
                    actionHandler.handle(ctx.channel(), tenantId, request);
                }

                // 处理操作
                if (StringUtils.isNotBlank(request.getProcess())) {
                    ProcessHandler processHandler = SpringUtils.getApplicationContext().getBean(ProcessHandler.class);
                    processHandler.handle(clientId, tenantId, request);
                }
            } catch (UnauthorizedException ue) {
                String content = GSON.toJson(buildUnauthorizedResp(request));
                String summary = String.format("UnauthorizedException id=%s", request.getId());
                MessageSender.send(clientId, Message.build(content), summary);
            } catch (BusinessException be) {
                String content = GSON.toJson(buildBadRequestResp(request, be.getMc()));
                String summary = String.format("BusinessException id=%s", request.getId());
                MessageSender.send(clientId, Message.build(content), summary);
            } catch (IllegalArgumentException iae) {
                String content = GSON.toJson(buildBadRequestResp(request, iae.getMessage()));
                String summary = String.format("IllegalArgumentException id=%s", request.getId());
                MessageSender.send(clientId, Message.build(content), summary);
            } catch (Exception e) {
                log.error(e.getMessage(), e);

                //
                ResponseMessage<?> resp = ResponseMessage.builder()
                        .id(request.getId()).action(request.getAction()).process(request.getProcess())
                        .code(Resp.failure().getRc()).msg(e.getMessage()).build();
                String content = GSON.toJson(resp);
                String summary = String.format("Exception id=%s", request.getId());
                MessageSender.send(clientId, Message.build(content), summary);

                //
                ClientManager.close(ctx.channel());
            }
        } else {
            ResponseMessage<?> resp = ResponseMessage.builder().code(Resp.failure().getRc()).msg("bad request").build();
            String content = GSON.toJson(resp);
            MessageSender.send(clientId, Message.build(content), "checkMessage");
            ClientManager.close(ctx.channel());
        }

    }

    /**
     * 构建未鉴权响应
     *
     * @param request 请求
     * @return 响应
     */
    public static ResponseMessage<?> buildUnauthorizedResp(RequestMessage request) {
        return ResponseMessage.builder()
                .id(request.getId()).action(request.getAction()).process(request.getProcess())
                .code(401).msg("token expire")
                .build();
    }

    /**
     * 构建业务异常响应
     *
     * @param request 请求
     * @param message 异常消息
     * @return 响应
     */
    public static ResponseMessage<?> buildBadRequestResp(RequestMessage request, String message) {
        return ResponseMessage.builder()
                .id(request.getId()).action(request.getAction()).process(request.getProcess())
                .code(400).msg(message)

                .build();
    }

    /**
     * 响应成功
     *
     * @param clientId 会话
     */
    public static void respSuccess(ChannelId clientId, RequestMessage request, Object data) {
        Resp<Object> success = Resp.success();
        resp(clientId, request, success.getRc(), success.getMc(), data);
    }

    /**
     * 响应
     *
     * @param clientId 客户端ID
     * @param request  请求
     * @param rc       return code
     * @param mc       message code
     * @param data     data
     */
    public static void resp(ChannelId clientId, RequestMessage request, Integer rc, String mc, Object data) {
        //
        ResponseMessage<?> resp = ResponseMessage.builder()
                .id(request.getId()).action(request.getAction()).process(request.getProcess())
                .code(rc).msg(mc)
                .data(data)
                .build();
        String content = GSON.toJson(resp);
        String summary = String.format("resp ::: rc=%s, mc=%s, id=%s", rc, mc, request.getId());
        MessageSender.send(clientId, Message.build(content), summary);
    }

    /**
     * 响应信息
     *
     * @param clientId 会话
     */
    private void respInfo(ChannelId clientId) {
        // 服务器
        String server = getServer();

        // 当前节点在线人数
        int online = ClientManager.getOnline();


        Long accountId = ClientManager.getLongAttrVal(clientId, ClientManager.ATTR_ACCOUNT_ID);

        // 响应信息
        Map<String, String> infoMap = new HashMap<>(16);
        infoMap.put("server", server);
        infoMap.put("clientId", clientId.asShortText());
        infoMap.put("clientIds", String.valueOf(online));
        infoMap.put("accountId", Objects.isNull(accountId) ? null : accountId.toString());


        String content = GSON.toJson(infoMap);
        MessageSender.send(clientId, Message.build(content), "resp info");
    }


    /**
     * 获取服务实例
     *
     * @return 服务实例
     */
    private String getServer() {
        String server = System.getenv("POD_NAME");
        if (StringUtils.isNotBlank(server)) {
            server = server.replace("ryzen-biz-push-", "");
        }
        return server;
    }

}
