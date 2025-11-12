package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class Main {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions()
                            .setHeadless(false)
                    );
            Page page = browser.newPage();
            page.navigate("https://www.google.com/search?q=depaul+bookstore");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("DePaul University, Chicago Depaul University https://depaul.bncollege.com/"));
            page.locator ("#homeSplashImg").click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("+ Undergraduate Majors")).click();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Academic Calendar")).click();
            page.getByText("To download events to your").dblclick();
        }
    }
}

