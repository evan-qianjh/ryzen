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
    private static final String USER_OBJECT_KEY = "oem/{oemId}/tenant/{tenantId}/{userType}/{userId}/{category}/{date}/{objectName}";

    @Override
    public AliyunOssUploadToken createUserToken(Long oemId, Long tenantId, String userType, Long userId, String category, String fileName) {
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
                .replace("{userType}", userType)
                .replace("{userId}", String.valueOf(userId))
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

}
