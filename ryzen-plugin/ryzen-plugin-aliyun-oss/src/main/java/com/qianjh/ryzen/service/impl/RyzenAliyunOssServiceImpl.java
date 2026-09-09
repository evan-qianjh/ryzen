package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qianjh.ryzen.dict.ImageStyle;
import com.qianjh.ryzen.dict.VideoStyle;
import com.qianjh.ryzen.entity.PartnerAliyun;
import com.qianjh.ryzen.entity.PluginAliyunOss;
import com.qianjh.ryzen.service.PartnerAliyunService;
import com.qianjh.ryzen.service.PluginAliyunOssService;
import com.qianjh.ryzen.service.RyzenAliyunOssService;
import com.qianjh.ryzen.service.RyzenStorageCryptoService;
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

@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class RyzenAliyunOssServiceImpl implements RyzenAliyunOssService, ApplicationRunner {

    private static final String ASSETS_TEMPLATE = "tenant/{tenantId}/assets/{service}/uploads/{fileName}";
    private static final String ACCOUNT_TEMPLATE = "tenant/{tenantId}/{accountType}_account/{accountId}/uploads/{fileName}";
    private static final String IMAGE_STYLE_FORMAT = "?x-oss-process=style/";
    private static final String VIDEO_STYLE_FORMAT = "?x-oss-process=video/";

    private static final Map<Long, PluginAliyunOss> TENANT_ID__PLUGIN = new HashMap<>();
    private static final Map<Long, PartnerAliyun> TENANT_ID__PARTNER = new HashMap<>();

    private final PartnerAliyunService partnerAliyunService;
    private final PluginAliyunOssService pluginAliyunOssService;
    private final RyzenStorageCryptoService ryzenStorageCryptoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //
        List<PluginAliyunOss> plugins = pluginAliyunOssService.list(Wrappers.emptyWrapper());
        for (PluginAliyunOss plugin : plugins) {
            TENANT_ID__PLUGIN.put(plugin.getTenantId(), plugin);
        }
        //
        List<PartnerAliyun> partners = partnerAliyunService.list(Wrappers.lambdaQuery());
        for (PartnerAliyun partner : partners) {
            TENANT_ID__PARTNER.put(partner.getTenantId(), partner);
        }
    }

    /**
     * 获取OSS
     *
     * @param tenantId 租户
     * @return OSS
     */
    private PluginAliyunOss getOss(Long tenantId) {
        return TENANT_ID__PLUGIN.get(tenantId);
    }

    /**
     * 获取Aliyun
     *
     * @param tenantId 租户
     * @return Aliyun
     */
    private PartnerAliyun getAliyun(Long tenantId) {
        return TENANT_ID__PARTNER.get(tenantId);
    }

    @Override
    public String getDownloadHost(Long tenantId) {
        PluginAliyunOss oss = getOss(tenantId);
        return oss == null ? null : oss.getDownloadHost();
    }

    @Override
    public String getObjectUrl(Long tenantId, String objectPath) {
        //
        if (StringUtils.isBlank(objectPath)) {
            return null;
        }
        //
        if (objectPath.startsWith("http")) {
            return objectPath;
        }
        //
        return getDownloadHost(tenantId) + (objectPath.startsWith("/") ? objectPath : "/" + objectPath);
    }

    @Override
    public String getObjectUrl(Long tenantId, String objectPath, ImageStyle imageStyle) {
        String objectUrl = getObjectUrl(tenantId, objectPath);
        //
        return appendStyle(objectUrl, imageStyle);
    }

    @Override
    public String getObjectUrl(Long tenantId, String objectPath, VideoStyle videoStyle) {
        String objectUrl = getObjectUrl(tenantId, objectPath);
        //
        return appendStyle(objectUrl, videoStyle);
    }

    @Override
    public String getOrDefaultObjectUrl(Long tenantId, String objectPath, String defaultObjectPath) {
        //
        String objectUrl = getObjectUrl(tenantId, objectPath);
        //
        if (StringUtils.isNotBlank(objectUrl)) {
            return objectUrl;
        }
        return getObjectUrl(tenantId, defaultObjectPath);
    }

    @Override
    public List<String> getObjectUrls(Long tenantId, List<String> objectPaths) {
        if (Objects.isNull(objectPaths) || objectPaths.isEmpty()) {
            return Collections.emptyList();
        }
        return objectPaths.stream().map(objectPath -> getObjectUrl(tenantId, objectPath)).toList();
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
        PluginAliyunOss pluginAliyunOss = getOss(tenantId);
        PartnerAliyun partnerAliyun = getAliyun(tenantId);

        String policy = AliyunOssUtils.getPolicy();
        String signature = AliyunOssUtils.getSignature(ryzenStorageCryptoService.decrypt(partnerAliyun.getAccessSecret()), policy);

        return AliyunOssUploadToken.builder()
                .bucket(pluginAliyunOss.getBucketName())
                .fileKey(objectName)
                .policy(policy)
                .accessKeyId(partnerAliyun.getAccessKey())
                .signature(signature)
                .downloadHost(getDownloadHost(tenantId))
                .uploadHost(pluginAliyunOss.getUploadPublicHost())
                .build();
    }

}
