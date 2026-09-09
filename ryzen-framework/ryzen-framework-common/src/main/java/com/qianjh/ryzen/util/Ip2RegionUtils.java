package com.qianjh.ryzen.util;

import org.apache.commons.lang3.StringUtils;

public final class Ip2RegionUtils {

    public static String[] splitRegion(String region) {
        if (StringUtils.isBlank(region)) {
            return new String[0];
        }
        return region.split("\\|");
    }

    public static String splitRegionCity(String region) {
        String[] regions = splitRegion(region);
        if (regions.length == 0) {
            return null;
        }
        // 中国|安徽省|合肥市|电信|CN
        // 合肥市
        String city = regions[Math.min(regions.length - 1, 2)];
        if (city.equals("0")) {
            return "未知";
        }
        return city;
    }

    public static String splitRegionProvince(String region) {
        String[] regions = splitRegion(region);
        if (regions.length == 0) {
            return null;
        }
        // 中国|安徽省|合肥市|电信|CN
        // 安徽省
        String province = regions[Math.min(regions.length - 1, 1)];
        if (province.equals("0")) {
            return "未知";
        }
        return province;
    }
}
