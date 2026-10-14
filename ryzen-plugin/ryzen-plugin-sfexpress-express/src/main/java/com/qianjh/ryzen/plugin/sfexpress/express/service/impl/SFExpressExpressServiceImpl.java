package com.qianjh.ryzen.plugin.sfexpress.express.service.impl;

import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.framework.security.service.RyzenStorageService;
import com.qianjh.ryzen.plugin.sfexpress.entity.SFExpress;
import com.qianjh.ryzen.plugin.sfexpress.express.enums.Language;
import com.qianjh.ryzen.plugin.sfexpress.express.enums.SearchType;
import com.qianjh.ryzen.plugin.sfexpress.express.service.SFExpressExpressService;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.OrderSearchRespDto;
import com.qianjh.ryzen.plugin.sfexpress.express.service.dto.SfResp;
import com.qianjh.ryzen.plugin.sfexpress.service.SFExpressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SFExpressExpressServiceImpl implements SFExpressExpressService {

    private static final Language DEFAULT_LANGUAGE = Language.ZH_CN;

    private final WebClient webClient;
    private final SFExpressService sfExpressService;
    private final RyzenStorageService ryzenStorageService;

    @Override
    public Mono<SfResp<OrderSearchRespDto>> orderSearch(Long oemId, SearchType searchType, String orderId, String mainWaybillNo, Language language) {
        SFExpress sfExpress = sfExpressService.mustGet(oemId);
        String apiHost = sfExpress.getApiHost();
        String customerCode = sfExpress.getCustomerCode();
        String checkWord = ryzenStorageService.decrypt(sfExpress.getCheckWord());

        if (orderId == null || orderId.isBlank()) {
            return Mono.error(new IllegalArgumentException("顺丰客户订单号不能为空"));
        }

        if (searchType == null) {
            return Mono.error(new IllegalArgumentException("查询类型不能为空"));
        }

        Map<String, Object> requestData = new LinkedHashMap<>();
        requestData.put("orderId", orderId);
        requestData.put("searchType", String.valueOf(searchType.getNumber()));
        requestData.put("language", language.getCode());

        return Mono.fromCallable(
                        () -> GsonUtils.toJson(requestData))
                .flatMap(msgData -> orderSearch(apiHost, customerCode, checkWord, msgData));
    }

    @Override
    public Mono<SfResp<OrderSearchRespDto>> orderSearch(Long oemId, SearchType searchType, String orderId, String mainWaybillNo) {
        return orderSearch(oemId, searchType, orderId, mainWaybillNo, DEFAULT_LANGUAGE);
    }

    private Mono<SfResp<OrderSearchRespDto>> orderSearch(String apiHost, String customerCode, String checkWord, String msgData) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String requestId = UUID.randomUUID().toString();
        String msgDigest = generateMsgDigest(msgData, timestamp, checkWord);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("partnerID", customerCode);
        form.add("requestID", requestId);
        form.add("serviceCode", "EXP_RECE_SEARCH_ORDER_RESP");
        form.add("timestamp", timestamp);
        form.add("msgDigest", msgDigest);
        form.add("msgData", msgData);

        return webClient.post()
                .uri(apiHost + "/std/service")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(SfApiResponse.class)
                .flatMap(this::parseResponse);
    }

    /**
     * 解析响应报文
     *
     * @param response 响应实体
     * @return 响应报文
     */
    private Mono<SfResp<OrderSearchRespDto>> parseResponse(SfApiResponse response) {
        if (response == null) {
            return Mono.error(new IllegalStateException("顺丰接口返回为空"));
        }

        OrderSearchRespDto msgData = null;
        if (response.success && "A1000".equals(response.errorCode) && StringUtils.hasText(response.msgData)) {
            msgData = GsonUtils.GSON.fromJson(response.msgData, OrderSearchRespDto.class);
        }

        SfResp<OrderSearchRespDto> sfResp = SfResp.<OrderSearchRespDto>builder()
                .success(response.success)
                .errorCode(response.errorCode)
                .errorMsg(response.errorMsg)
                .msgData(msgData)
                .build();

        return Mono.just(sfResp);
    }

    private String generateMsgDigest(String msgData, String timestamp, String checkWord) {

        try {
            String source = msgData + timestamp + checkWord;

            byte[] digest = MessageDigest.getInstance("MD5").digest(source.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前环境不支持 MD5", e);
        }
    }

    private record SfApiResponse(
            Boolean success, // true 请求成功，false 请求失败
            String errorCode, // 错误编码，S0000成功
            String errorMsg, // 错误描述
            String msgData) {
    }
}