package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qianjh.ryzen.framework.Oem;
import com.qianjh.ryzen.framework.OemDomain;
import com.qianjh.ryzen.framework.gateway.util.DomainUtils;
import com.qianjh.ryzen.mapper.OemDomainMapper;
import com.qianjh.ryzen.mapper.OemMapper;
import com.qianjh.ryzen.service.DomainService;
import com.qianjh.ryzen.service.dto.DomainOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainServiceImpl implements DomainService {

    private final OemDomainMapper oemDomainMapper;

    private static final Map<Long, Oem> OEM_MAP = new ConcurrentHashMap<>();
    private static final Map<String, OemDomain> DOMAIN_MAP = new ConcurrentHashMap<>();

    private final OemMapper oemMapper;

    // TODO 不优雅，应该启动时加载一次，然后通过事件驱动更新
    @Scheduled(fixedRate = 10_000L)
    public void job() {
        try {
            refreshAll();
            log.debug("刷新Cache");
        } catch (Exception e) {
            log.error("刷新Cache失败", e);
        }
    }

    private void refreshAll() {
        // oem
        for (Oem entity : oemMapper.selectList(Wrappers.emptyWrapper())) {
            OEM_MAP.put(entity.getId(), entity);
        }
        // oem domain
        for (OemDomain oemDomain : oemDomainMapper.selectList(Wrappers.emptyWrapper())) {
            Oem oem = OEM_MAP.get(oemDomain.getOemId());
            if(oem == null) {
                log.error("没有Oem的OemDomain ::: oemDomain={}", oemDomain);
                continue;
            }
            DOMAIN_MAP.put(oemDomain.getDomain(), oemDomain);
        }
    }

    @Override
    public DomainOwner resolve(String domain) {
        // 解析出top
        String top = DomainUtils.parseTop(domain);

        //
        OemDomain oemDomain = DOMAIN_MAP.get(top);
        Oem oem = OEM_MAP.get(oemDomain.getOemId());

        return new DomainOwner(oem, oemDomain);
    }
}
