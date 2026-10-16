package com.qianjh.ryzen.plugin.aliyun.oss.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.framework.common.util.DateUtils;
import com.qianjh.ryzen.framework.security.service.RyzenStorageService;
import com.qianjh.ryzen.plugin.aliyun.entity.Aliyun;
import com.qianjh.ryzen.plugin.aliyun.oss.entity.AliyunOss;
import com.qianjh.ryzen.plugin.aliyun.oss.mapper.AliyunOssMapper;
import com.qianjh.ryzen.plugin.aliyun.oss.service.AliyunOssService;
import com.qianjh.ryzen.plugin.aliyun.oss.service.dto.AliyunOssUploadToken;
import com.qianjh.ryzen.plugin.aliyun.oss.util.AliyunOssUtils;
import com.qianjh.ryzen.plugin.aliyun.service.AliyunService;
import com.qianjh.ryzen.plugin.oss.dict.ImageStyle;
import com.qianjh.ryzen.plugin.oss.dict.VideoStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class AliyunOssServiceImpl extends ServiceImpl<AliyunOssMapper, AliyunOss> implements AliyunOssService, ApplicationRunner {


    private static final String IMAGE_STYLE_FORMAT = "?x-oss-process=style/";
    private static final String VIDEO_STYLE_FORMAT = "?x-oss-process=video/";

    private static final Map<Long, AliyunOss> OEM_ID__PLUGIN = new HashMap<>();
    private static final Map<Long, Aliyun> OEM_ID__PARTNER = new HashMap<>();

    private final AliyunService aliyunService;
    private final RyzenStorageService ryzenStorageCryptoService;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //
        List<AliyunOss> plugins = list(Wrappers.emptyWrapper());
        for (AliyunOss plugin : plugins) {
            OEM_ID__PLUGIN.put(plugin.getOemId(), plugin);
        }
        //
        List<Aliyun> partners = aliyunService.list(Wrappers.lambdaQuery());
        for (Aliyun partner : partners) {
            OEM_ID__PARTNER.put(partner.getOemId(), partner);
        }
    }

    /**
     * 获取OSS
     *
     * @param oemId OEM
     * @return OSS
     */
    private AliyunOss getOss(Long oemId) {
        return OEM_ID__PLUGIN.get(oemId);
    }

    /**
     * 获取Aliyun
     *
     * @param oemId OEM
     * @return Aliyun
     */
    private Aliyun getAliyun(Long oemId) {
        return OEM_ID__PARTNER.get(oemId);
    }

    @Override
    public String getDownloadHost(Long oemId) {
        AliyunOss oss = getOss(oemId);
        return oss == null ? null : oss.getDownloadHost();
    }

    @Override
    public String getObjectUrl(Long oemId, String objectPath) {
        //
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        //
        if (objectPath.startsWith("http")) {
            return objectPath;
        }
        //
        return getDownloadHost(oemId) + (objectPath.startsWith("/") ? objectPath : "/" + objectPath);
    }

    @Override
    public String getObjectUrl(Long oemId, String objectPath, ImageStyle imageStyle) {
        String objectUrl = getObjectUrl(oemId, objectPath);
        //
        return appendStyle(objectUrl, imageStyle);
    }

    @Override
    public String getObjectUrl(Long oemId, String objectPath, VideoStyle videoStyle) {
        String objectUrl = getObjectUrl(oemId, objectPath);
        //
        return appendStyle(objectUrl, videoStyle);
    }

    @Override
    public String getOrDefaultObjectUrl(Long oemId, String objectPath, String defaultObjectPath) {
        //
        String objectUrl = getObjectUrl(oemId, objectPath);
        //
        if (StringUtils.isNotBlank(objectUrl)) {
            return objectUrl;
        }
        return getObjectUrl(oemId, defaultObjectPath);
    }

    @Override
    public List<String> getObjectUrls(Long oemId, List<String> objectPaths) {
        if (Objects.isNull(objectPaths) || objectPaths.isEmpty()) {
            return Collections.emptyList();
        }
        return objectPaths.stream().map(objectPath -> getObjectUrl(oemId, objectPath)).toList();
    }

    /**
     * 追加样式
     *
     * @param objectUrl  对象URL
     * @param imageStyle 图片样式
     * @return 带样式修饰的对象URL
     */
    private String appendStyle(String objectUrl, ImageStyle imageStyle) {
        if (imageStyle == null) {
            return objectUrl;
        }
        if (StringUtils.isBlank(objectUrl)) {
            return objectUrl;
        }
        if (objectUrl.contains(IMAGE_STYLE_FORMAT)) {
            return objectUrl;
        }
        return objectUrl + IMAGE_STYLE_FORMAT + imageStyle.getCode();
    }

    /**
     * 追加样式
     *
     * @param objectUrl  对象URL
     * @param videoStyle 视频样式
     * @return 带样式修饰的对象URL
     */
    private String appendStyle(String objectUrl, VideoStyle videoStyle) {
        if (videoStyle == null) {
            return objectUrl;
        }
        if (StringUtils.isBlank(objectUrl)) {
            return objectUrl;
        }
        if (objectUrl.contains(VIDEO_STYLE_FORMAT)) {
            return objectUrl;
        }
        return objectUrl + VIDEO_STYLE_FORMAT + videoStyle.getCode();
    }

    /**
     * 用户模板
     * oem/1/tenant/1/user/1/avatar/20261007/abcdefg123456.png
     */
    private static final String USER_OBJECT_KEY = "oem/{oemId}/tenant/{tenantId}/{domain}/{domainId}/{category}/{date}/{objectName}";

    @Override
    public AliyunOssUploadToken createToken(Long oemId, Long tenantId, String domain, Long domainId, String category, String fileName) {
        Assert.isTrue(StringUtils.isNotBlank(category), IllegalArgumentException.class.getSimpleName());
        Assert.isTrue(StringUtils.isNotBlank(fileName), IllegalArgumentException.class.getSimpleName());

        // 获取suffix
        String[] split = fileName.split("\\.");
        Assert.isTrue(split.length > 1, IllegalArgumentException.class.getSimpleName());
        String suffix = split[split.length - 1];

        // 文件名
        String objectName = generateFileName(suffix);
        // date
        String date = DateUtils.format(LocalDate.now(), DateUtils.CLEAR_FORMATTER);
        // replace
        String objectKey = USER_OBJECT_KEY
                .replace("{oemId}", String.valueOf(oemId))
                .replace("{tenantId}", String.valueOf(tenantId))
                .replace("{domain}", domain)
                .replace("{domainId}", String.valueOf(domainId))
                .replace("{category}", category)
                .replace("{date}", date)
                .replace("{objectName}", objectName);

        return createToken(oemId, objectKey);
    }

    /**
     * 创建token
     *
     * @param tenantId   租户ID
     * @param objectName 对象名称
     * @return token
     */
    private AliyunOssUploadToken createToken(Long tenantId, String objectName) {
        AliyunOss aliyunOss = getOss(tenantId);
        Aliyun aliyun = getAliyun(tenantId);

        String policy = AliyunOssUtils.getPolicy();
        String signature = AliyunOssUtils.getSignature(ryzenStorageCryptoService.decrypt(aliyun.getAccessSecret()), policy);

        return AliyunOssUploadToken.builder()
                .bucket(aliyunOss.getBucketName())
                .fileKey(objectName)
                .policy(policy)
                .accessKeyId(aliyun.getAccessKey())
                .signature(signature)
                .downloadHost(getDownloadHost(tenantId))
                .uploadHost(aliyunOss.getUploadPublicHost())
                .build();
    }


    @Override
    public boolean upload(AliyunOssUploadToken token, byte[] content, String contentType, String fileName) throws IOException, InterruptedException {
        if (token == null || content == null || content.length == 0) {
            throw new IllegalArgumentException("token 和文件内容不能为空");
        }

        // multipart/form-data 请求中的分隔符
        String boundary = "----RyzenOSS" + UUID.randomUUID().toString().replace("-", "");

        ByteArrayOutputStream body = new ByteArrayOutputStream();

        addField(body, boundary, "key", token.getFileKey());
        addField(body, boundary, "policy", token.getPolicy());
        addField(body, boundary, "OSSAccessKeyId", token.getAccessKeyId());
        addField(body, boundary, "Signature", token.getSignature());

        // 让 OSS 返回成功状态码 200，而不是默认的 204。
        addField(body, boundary, "success_action_status", "200");

        addFile(body, boundary, "file", fileName, contentType, content);

        write(body, "--" + boundary + "--\r\n");

        String uploadHost = token.getUploadHost();
        if (uploadHost == null || uploadHost.isBlank()) {
            log.error("uploadHost 不能为空 ::: {}", token);
            return false;
        }

        // uploadHost 应是 Bucket 的 OSS 上传域名，不应拼接 fileKey。
        String uploadUrl = uploadHost.endsWith("/")
                ? uploadHost.substring(0, uploadHost.length() - 1)
                : uploadHost;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .timeout(java.time.Duration.ofSeconds(60))
                .header(
                        "Content-Type",
                        "multipart/form-data; boundary=" + boundary
                )
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        // 失败
        if (response.statusCode() != 200) {
            log.error("OSS 上传失败 ::: token={}, 状态码={}, 响应={}", token, response.statusCode(), response.body());
            return false;
        }

        return true;
    }

    private static void addField(ByteArrayOutputStream out, String boundary, String name, String value) throws IOException {
        write(out, "--" + boundary + "\r\n");
        write(out, "Content-Disposition: form-data; name=\"" + name + "\"\r\n");
        write(out, "\r\n");
        write(out, value == null ? "" : value);
        write(out, "\r\n");
    }

    private static void addFile(ByteArrayOutputStream out, String boundary, String fieldName, String fileName, String contentType, byte[] content) throws IOException {
        write(out, "--" + boundary + "\r\n");
        write(out, "Content-Disposition: form-data; name=\"" + fieldName
                + "\"; filename=\"" + fileName + "\"\r\n");
        write(out, "Content-Type: "
                + (contentType == null ? "application/octet-stream" : contentType)
                + "\r\n");
        write(out, "\r\n");
        out.write(content);
        write(out, "\r\n");
    }

    private static void write(ByteArrayOutputStream out, String value) throws IOException {
        out.write(value.getBytes(StandardCharsets.UTF_8));
    }
}
