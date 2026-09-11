package com.qianjh.ryzen.cache.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qianjh.ryzen.cache.OemDomainCache;
import com.qianjh.ryzen.entity.OemDomain;
import com.qianjh.ryzen.mapper.OemDomainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OemDomainCacheImpl implements OemDomainCache {

    private final Map<Long, OemDomain> ID_MAP = new ConcurrentHashMap<>(16);

    private final OemDomainMapper oemDomainMapper;

    @Scheduled(fixedRate = 10_000L)
    public void job() {
        try {
            refreshAll();
            log.debug("初始化TenantDomain ::: {}", ID_MAP.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshAll() {
        List<OemDomain> entities = oemDomainMapper.selectList(Wrappers.emptyWrapper());
        Map<Long, OemDomain> id2Entity = entities.stream()
                .collect(Collectors.toMap(OemDomain::getId, Function.identity(), (key1, key2) -> key2));

        // 移除失效的
        for (Map.Entry<Long, OemDomain> entry : ID_MAP.entrySet()) {
            Long id = entry.getKey();
            if (!id2Entity.containsKey(id)) {
                ID_MAP.remove(id);
            }
        }

        // 缓存新的
        for (OemDomain entity : entities) {
            cache(entity);
        }
    }

    private void cache(OemDomain entity) {
        ID_MAP.put(entity.getId(), entity);
    }

    @Override
    public OemDomain getByDomain(String domain) {
        if (!StringUtils.hasLength(domain)) {
            return null;
        }
        return ID_MAP.values().stream()
                .filter(entity -> entity.getDomain().equalsIgnoreCase(domain))
                .findFirst().orElse(null);
    }
}
