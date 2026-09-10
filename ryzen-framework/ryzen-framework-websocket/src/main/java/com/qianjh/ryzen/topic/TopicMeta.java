package com.qianjh.ryzen.topic;

import com.qianjh.ryzen.util.ListUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author QianJH 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TopicMeta {
    private String key;
    private String name;
    private List<String> params;

    /**
     * 解析
     *
     * @param key 唯一标识
     * @return 主题信息
     */
    public static TopicMeta ofKey(String key) {
        String[] topicArgs = key.split("@");
        String name = topicArgs[0];
        List<String> params = topicArgs.length > 1 ? Arrays.asList(topicArgs[1].split(",")) : Collections.emptyList();
        return TopicMeta.builder()
                .name(name)
                .params(params)
                .key(key)
                .build();
    }

    /**
     * 构建
     *
     * @param name 名称
     * @return 主题信息
     */
    public static TopicMeta ofName(String name) {
        return ofNameParams(name, Collections.emptyList());
    }

    /**
     * 构建
     *
     * @param name   名称
     * @param params 参数
     * @return 主题信息
     */
    public static TopicMeta ofNameParams(String name, List<String> params) {
        String paramsStr = ListUtils.stringList2String(params);
        String key = StringUtils.isBlank(paramsStr) ? name : name + "@" + paramsStr;

        return TopicMeta.builder()
                .name(name)
                .params(params)
                .key(key)
                .build();
    }
}
