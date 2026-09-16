package com.qianjh.ryzen.service;

import com.qianjh.ryzen.dict.ImageStyle;
import com.qianjh.ryzen.dict.VideoStyle;

import java.util.List;
import java.util.UUID;

/**
 * @author QianJH
 */
public interface OssService {

    /**
     * 获取下载域名
     * e.g.: https://cdn.xxx.com
     *
     * @param oemId OEM ID
     * @return 下载域名
     */
    String getDownloadHost(Long oemId);

    /**
     * 获取对象地址
     * e.g.: https://cdn.xxx.com/tenant/1/assets/thing/static/abc.jpg
     *
     * @param oemId      OEM ID
     * @param objectPath 路径
     * @return 绝对地址
     */
    String getObjectUrl(Long oemId, String objectPath);

    /**
     * 获取对象地址
     *
     * @param oemId      OEM ID
     * @param objectPath 路径
     * @param imageStyle 图片样式
     * @return 绝对地址
     */
    String getObjectUrl(Long oemId, String objectPath, ImageStyle imageStyle);

    /**
     * 获取对象地址
     *
     * @param oemId      OEM ID
     * @param objectPath 路径
     * @param videoStyle 视频样式
     * @return 绝对地址
     */
    String getObjectUrl(Long oemId, String objectPath, VideoStyle videoStyle);


    /**
     * 获取对象地址
     *
     * @param oemId             OEM ID
     * @param objectPath        路径
     * @param defaultObjectPath 默认路径
     * @return 绝对地址
     */
    String getOrDefaultObjectUrl(Long oemId, String objectPath, String defaultObjectPath);

    /**
     * 获取对象地址集合
     * e.g.: https://cdn.xxx.com/oem/1/assets/thing/static/abc.jpg
     *
     * @param oemId       OEM ID
     * @param objectPaths 路径集合
     * @return 绝对地址集合
     */
    List<String> getObjectUrls(Long oemId, List<String> objectPaths);

    /**
     * 生成文件名
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 文件名
     */
    default String generateFileName(String prefix, String suffix) {
        String fileName = UUID.randomUUID().toString();

        StringBuilder sb = new StringBuilder();

        // prefix
        if (prefix != null && !prefix.isBlank()) {
            sb.append(prefix);
        }

        // filename
        sb.append(fileName);

        //  suffix
        if (suffix != null && !suffix.isBlank()) {
            sb.append(suffix);
        }
        return sb.toString();
    }
}
