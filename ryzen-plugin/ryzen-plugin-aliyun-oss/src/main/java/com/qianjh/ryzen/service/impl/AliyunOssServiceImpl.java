package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.dict.ImageStyle;
import com.qianjh.ryzen.dict.VideoStyle;
import com.qianjh.ryzen.entity.Aliyun;
import com.qianjh.ryzen.entity.AliyunOss;
import com.qianjh.ryzen.mapper.AliyunOssMapper;
import com.qianjh.ryzen.service.AliyunOssService;
import com.qianjh.ryzen.service.AliyunService;
import com.qianjh.ryzen.service.RyzenStorageService;
import com.qianjh.ryzen.service.dto.AliyunOssUploadToken;
import com.qianjh.ryzen.util.AliyunOssUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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

    private static final String ASSETS_TEMPLATE = "tenant/{tenantId}/assets/{service}/uploads/{fileName}";
    private static final String ACCOUNT_TEMPLATE = "tenant/{tenantId}/{accountType}_account/{accountId}/uploads/{fileName}";
    private static final String IMAGE_STYLE_FORMAT = "?x-oss-process=style/";
    private static final String VIDEO_STYLE_FORMAT = "?x-oss-process=video/";

    private static final Map<Long, AliyunOss> TENANT_ID__PLUGIN = new HashMap<>();
    private static final Map<Long, Aliyun> TENANT_ID__PARTNER = new HashMap<>();

    private final AliyunService aliyunService;
    private final RyzenStorageService ryzenStorageCryptoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //
        List<AliyunOss> plugins = list(Wrappers.emptyWrapper());
        for (AliyunOss plugin : plugins) {
            TENANT_ID__PLUGIN.put(plugin.getOemId(), plugin);
        }
        //
        List<Aliyun> partners = aliyunService.list(Wrappers.lambdaQuery());
        for (Aliyun partner : partners) {
            TENANT_ID__PARTNER.put(partner.getOemId(), partner);
        }
    }

    /**
     * 获取OSS
     *
     * @param tenantId 租户
     * @return OSS
     */
    private AliyunOss getOss(Long tenantId) {
        return TENANT_ID__PLUGIN.get(tenantId);
    }

    /**
     * 获取Aliyun
     *
     * @param tenantId 租户
     * @return Aliyun
     */
    private Aliyun getAliyun(Long tenantId) {
        return TENANT_ID__PARTNER.get(tenantId);
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


    @Override
    public AliyunOssUploadToken createAssetToken(Long tenantId, String service, String filePrefix, String fileSuffix) {
        // 文件名
        String fileName = generateFileName(filePrefix, fileSuffix);

        String objectName = ASSETS_TEMPLATE
                .replace("{tenantId}", String.valueOf(tenantId))
                .replace("{service}", service)
                .replace("{fileName}", fileName);

        return createToken(tenantId, objectName);
    }

    @Override
    public AliyunOssUploadToken createAccountToken(Long tenantId, String accountType, Long accountId, String filePrefix, String fileSuffix) {
        // 文件名
        String fileName = generateFileName(filePrefix, fileSuffix);

        String objectName = ACCOUNT_TEMPLATE
                .replace("{tenantId}", String.valueOf(tenantId))
                .replace("{accountType}", accountType)
                .replace("{accountId}", String.valueOf(accountId))
                .replace("{fileName}", fileName);

        return createToken(tenantId, objectName);
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

}
