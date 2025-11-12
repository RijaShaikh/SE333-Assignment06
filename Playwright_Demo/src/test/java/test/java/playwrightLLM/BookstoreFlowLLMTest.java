package test.java.playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class BookstoreFlowLLMTest {

    @Test
    void fullPurchasePathwayLLMStyle() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setRecordVideoDir(Paths.get("videos_llm/"))
                            .setRecordVideoSize(1280, 720)
            );

            Page page = context.newPage();
            page.setDefaultTimeout(100000);
            page.setDefaultNavigationTimeout(100000);

            page.navigate("https://depaul.bncollege.com/");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            Locator searchBox = page.locator("input[type='search'], input[placeholder*='Search'], input[aria-label*='Search'], input[name*='search']");
            searchBox.first().waitFor();
            searchBox.first().click();
            searchBox.first().fill("earbuds");
            searchBox.first().press("Enter");

            page.getByText("Brand").first().click();
            page.getByText("JBL").first().click();

            page.getByText("Color").first().click();
            page.getByText("Black").first().click();

            page.getByText("Price").first().click();
            page.getByText("Over $50").first().click();

            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("JBL Quantum True Wireless Noise Cancelling Gaming", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(
                    page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black")
                            .first()
                            .isVisible(),
                    "LLM test: Product title missing"
            );

            assertTrue(page.getByText("149.98").first().isVisible(),
                       "LLM test: Product price 149.98 missing");

            assertTrue(page.getByText("SKU").first().isVisible(),
                       "LLM test: SKU label missing");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Add to Cart", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            page.waitForTimeout(3000);

            boolean oneItemInCart =
                    page.getByText("1 Item").first().isVisible() ||
                    page.getByText("1 Items").first().isVisible() ||
                    page.getByText("(1)").first().isVisible();
            assertTrue(oneItemInCart, "LLM test: Cart does not show 1 item");

            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Cart", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("Your Shopping Cart").first().isVisible(),
                       "LLM test: Not on Shopping Cart page");

            assertTrue(
                    page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black")
                            .first()
                            .isVisible(),
                    "LLM test: Cart item name missing"
            );
            assertTrue(page.getByText("$149.98").first().isVisible(),
                       "LLM test: Cart item price missing");

            page.getByText("FAST In-Store Pickup").first().click();

            assertTrue(page.getByText("149.98").first().isVisible(),
                       "LLM test: Subtotal 149.98 missing");
            assertTrue(page.getByText("2.00").first().isVisible(),
                       "LLM test: Handling 2.00 missing");
            assertTrue(page.getByText("TBD").first().isVisible(),
                       "LLM test: Tax TBD missing");
            assertTrue(page.getByText("151.98").first().isVisible(),
                       "LLM test: Estimated total 151.98 missing");

            Locator promoBox = page.locator("input[name='promo'], input[aria-label='Promo Code'], input[id*='promo']");
            promoBox.first().fill("TEST");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Apply", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(
                    page.getByText("promo code", new Page.GetByTextOptions().setExact(false)).first().isVisible()
                    || page.getByText("invalid", new Page.GetByTextOptions().setExact(false)).first().isVisible(),
                    "LLM test: Promo rejection message missing"
            );

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Proceed to Checkout", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("Create Account").first().isVisible(),
                       "LLM test: Create Account heading missing");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Proceed as Guest", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("Contact Information").first().isVisible(),
                       "LLM test: Contact Information heading missing");

            page.locator("input[name*='firstName'], input[id*='firstName']").first().fill("AIUserFirst");
            page.locator("input[name*='lastName'], input[id*='lastName']").first().fill("AIUserLast");
            page.locator("input[type='email'], input[name*='email']").first().fill("aiuser@example.com");
            page.locator("input[type='tel'], input[name*='phone']").first().fill("7735550000");

            assertTrue(page.getByText("149.98").first().isVisible(),
                       "LLM test: Contact page subtotal missing");
            assertTrue(page.getByText("2.00").first().isVisible(),
                       "LLM test: Contact page handling missing");
            assertTrue(page.getByText("TBD").first().isVisible(),
                       "LLM test: Contact page tax TBD missing");
            assertTrue(page.getByText("151.98").first().isVisible(),
                       "LLM test: Contact page estimated total missing");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("AIUserFirst AIUserLast").first().isVisible(),
                       "LLM test: Pickup page contact name missing");
            assertTrue(page.getByText("aiuser@example.com").first().isVisible(),
                       "LLM test: Pickup page email missing");
            assertTrue(page.getByText("7735550000").first().isVisible(),
                       "LLM test: Pickup page phone missing");

            assertTrue(page.getByText("DePaul University Loop Campus & SAIC").first().isVisible(),
                       "LLM test: Pickup location missing");
            assertTrue(page.getByText("I'll pick them up").first().isVisible(),
                       "LLM test: Pickup person choice missing");

            assertTrue(page.getByText("149.98").first().isVisible(),
                       "LLM test: Pickup page subtotal missing");
            assertTrue(page.getByText("2.00").first().isVisible(),
                       "LLM test: Pickup page handling missing");
            assertTrue(page.getByText("TBD").first().isVisible(),
                       "LLM test: Pickup page tax TBD missing");
            assertTrue(page.getByText("151.98").first().isVisible(),
                       "LLM test: Pickup page estimated total missing");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("149.98").first().isVisible(),
                       "LLM test: Payment page subtotal missing");
            assertTrue(page.getByText("2.00").first().isVisible(),
                       "LLM test: Payment page handling missing");
            assertTrue(page.getByText("15.58").first().isVisible(),
                       "LLM test: Payment page taxes missing");
            assertTrue(page.getByText("167.56").first().isVisible(),
                       "LLM test: Payment page total missing");

            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Back to Cart", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Remove|Delete", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            page.waitForTimeout(2000);

            boolean cartEmpty =
                    page.getByText("Your cart is empty").first().isVisible() ||
                    page.getByText("0 Items").first().isVisible();
            assertTrue(cartEmpty, "LLM test: Cart not empty after delete");

            context.close();
            browser.close();
        }
    }
}
