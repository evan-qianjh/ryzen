package com.qianjh.ryzen.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.qianjh.ryzen.config.ExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Playwright实现的浏览器会话工厂
 *
 * @author QianJH
 */
@Component
@RequiredArgsConstructor
public class PlaywrightBrowserSessionFactory implements BrowserSessionFactory {
    private final ExecutorProperties properties;

    @Override
    public BrowserSession open() {
        try {
            Path userDataDir = properties.getUserDataDir();
            // 创建目录
            Files.createDirectories(userDataDir);

            Playwright playwright = Playwright.create();
            // 设置是否有图形界面
            var options = new BrowserType.LaunchPersistentContextOptions()
                    .setHeadless(properties.isHeadless())
                    .setArgs(List.of("--start-maximized"));
            // 指定浏览器
            if (properties.getBrowserChannel() != null && !properties.getBrowserChannel().isBlank()) {
                options.setChannel(properties.getBrowserChannel());
            }
            // 打开页面
            BrowserContext context = playwright.chromium()
                    .launchPersistentContext(userDataDir, options);
            Page page = context.pages().isEmpty() ? context.newPage() : context.pages().getFirst();

            // 导航至配置的页面
            page.navigate(properties.getPlatformUrl());
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            return new PlaywrightBrowserSession(playwright, context, page, properties);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start Playwright browser session", e);
        }
    }
}
