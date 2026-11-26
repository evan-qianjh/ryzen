package com.qianjh.ryzen.browser;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.dto.Assignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    public BrowserSession open(Assignment assignment) {
        Playwright playwright = null;
        BrowserContext context = null;

        try {
            ImType imType = assignment.imType();
            String accountCode = assignment.accountCode();
            Path root = properties.getProfileRoot()
                    .toAbsolutePath().normalize();

            // 初始化用户目录
            Path userDataDir = root.resolve(imType + "/" + accountCode)
                    .normalize();

            // 防止 accountCode 被构造为路径穿越
            if (!userDataDir.startsWith(root)) {
                throw new IllegalArgumentException("Invalid accountCode");
            }

            // 创建目录
            Files.createDirectories(userDataDir);

            // 初始化playwright
            playwright = Playwright.create();

            // 设置是否有图形界面
            var options = new BrowserType.LaunchPersistentContextOptions()
                    .setHeadless(properties.isHeadless())
                    .setArgs(List.of("--start-maximized"));
            // 指定浏览器
            if (StringUtils.hasText(properties.getBrowserChannel())) {
                options.setChannel(properties.getBrowserChannel());
            }
            // 打开页面
            context = playwright.chromium()
                    .launchPersistentContext(userDataDir, options);

            // 初始化页面
            Page page = context.pages().isEmpty()
                    ? context.newPage()
                    : context.pages().getFirst();

            // 导航至配置的页面
            page.navigate(imType.getPlatformUrl());
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            return new PlaywrightBrowserSession(playwright, context, page, properties, assignment);
        } catch (Exception e) {
            if (context != null) {
                context.close();
            }
            if (playwright != null) {
                playwright.close();
            }
            throw new IllegalStateException(
                    String.format("Failed to start browser session for imType:%s, accountCount: %s",
                            assignment.imType(), assignment.accountCode()
                    ), e
            );
        }
    }
}
