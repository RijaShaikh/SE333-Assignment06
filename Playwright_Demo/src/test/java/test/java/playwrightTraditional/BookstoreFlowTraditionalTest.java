package test.java.playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class BookstoreFlowTraditionalTest {

    @Test
    void fullPurchasePathwayTraditional() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setRecordVideoDir(Paths.get("videos/"))
                            .setRecordVideoSize(1280, 720)
            );

            Page page = context.newPage();
            page.setDefaultTimeout(60000);

            page.navigate("https://depaul.bncollege.com/");
            Locator searchBox = page.locator("input[type='search'], input[aria-label='Search'], input[name='search']");
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
                    "Product name not visible"
            );

            assertTrue(
                    page.getByText("149.98").first().isVisible(),
                    "Product price 149.98 not visible"
            );

            assertTrue(page.getByText("SKU").first().isVisible(), "SKU label not visible");
            assertTrue(page.locator("div, p").filter(new Locator.FilterOptions().setHasText("Noise Cancelling")).first().isVisible(),
                    "Product description not found / not visible");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Add to Cart", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            page.waitForTimeout(3000);

            boolean oneItemInCart =
                    page.getByText("1 Item").first().isVisible() ||
                    page.getByText("1 Items").first().isVisible() ||
                    page.getByText("(1)").first().isVisible();
            assertTrue(oneItemInCart, "Cart does not show 1 item");

            // Click “Cart” icon / link.
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Cart", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(
                    page.getByText("Your Shopping Cart").first().isVisible(),
                    "Not on 'Your Shopping Cart' page"
            );

            assertTrue(
                    page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black")
                            .first()
                            .isVisible(),
                    "Product name not visible in cart"
            );
            assertTrue(page.getByText("Qty 1").first().isVisible() ||
                       page.getByText("Quantity: 1").first().isVisible(),
                       "Quantity 1 not visible in cart");
            assertTrue(page.getByText("$149.98").first().isVisible(),
                       "Cart price $149.98 not visible");

            page.getByText("FAST In-Store Pickup").first().click();

            assertTrue(page.getByText("149.98").first().isVisible(), "Subtotal 149.98 not visible");
            assertTrue(page.getByText("2.00").first().isVisible(), "Handling 2.00 not visible");
            assertTrue(page.getByText("TBD").first().isVisible(), "Tax TBD not visible");
            assertTrue(page.getByText("151.98").first().isVisible(), "Estimated total 151.98 not visible");

            Locator promoBox = page.locator("input[name='promo'], input[aria-label='Promo Code'], input[id*='promo']");
            promoBox.first().fill("TEST");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Apply", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(
                    page.getByText("promo code", new Page.GetByTextOptions().setExact(false)).first().isVisible()
                    || page.getByText("invalid", new Page.GetByTextOptions().setExact(false)).first().isVisible(),
                    "Promo code rejection message not visible"
            );

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Proceed to Checkout", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(
                    page.getByText("Create Account").first().isVisible(),
                    "Create Account label not visible"
            );

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Proceed as Guest", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(
                    page.getByText("Contact Information").first().isVisible(),
                    "Contact Information page label not visible"
            );

            page.locator("input[name*='firstName'], input[id*='firstName']").first().fill("TestFirst");
            page.locator("input[name*='lastName'], input[id*='lastName']").first().fill("TestLast");
            page.locator("input[type='email'], input[name*='email']").first().fill("test@example.com");
            page.locator("input[type='tel'], input[name*='phone']").first().fill("3125550000");

            assertTrue(page.getByText("149.98").first().isVisible(), "Contact page subtotal 149.98 not visible");
            assertTrue(page.getByText("2.00").first().isVisible(), "Contact page handling 2.00 not visible");
            assertTrue(page.getByText("TBD").first().isVisible(), "Contact page tax TBD not visible");
            assertTrue(page.getByText("151.98").first().isVisible(), "Contact page estimated total 151.98 not visible");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("TestFirst TestLast").first().isVisible(),
                       "Contact name not carried over to Pickup Information");
            assertTrue(page.getByText("test@example.com").first().isVisible(),
                       "Contact email not carried over to Pickup Information");
            assertTrue(page.getByText("3125550000").first().isVisible(),
                       "Contact phone not carried over to Pickup Information");

            // Assert pickup location & selection
            assertTrue(page.getByText("DePaul University Loop Campus & SAIC").first().isVisible(),
                       "Pickup location not visible");
            assertTrue(page.getByText("I'll pick them up").first().isVisible(),
                       "Pickup person selection not visible");

            // Sidebar totals
            assertTrue(page.getByText("149.98").first().isVisible(),
                       "Pickup page subtotal 149.98 not visible");
            assertTrue(page.getByText("2.00").first().isVisible(),
                       "Pickup page handling 2.00 not visible");
            assertTrue(page.getByText("TBD").first().isVisible(),
                       "Pickup page tax TBD not visible");
            assertTrue(page.getByText("151.98").first().isVisible(),
                       "Pickup page estimated total 151.98 not visible");

            assertTrue(page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black")
                            .first().isVisible(),
                       "Pickup item not visible");
            assertTrue(page.getByText("$149.98").first().isVisible(),
                       "Pickup item price 149.98 not visible");

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)))
                .first()
                .click();

            assertTrue(page.getByText("149.98").first().isVisible(),
                       "Payment page subtotal 149.98 not visible");
            assertTrue(page.getByText("2.00").first().isVisible(),
                       "Payment page handling 2.00 not visible");
            assertTrue(page.getByText("15.58").first().isVisible(),
                       "Payment page taxes 15.58 not visible");
            assertTrue(page.getByText("167.56").first().isVisible(),
                       "Payment page total 167.56 not visible");

            assertTrue(page.getByText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black")
                            .first().isVisible(),
                       "Payment page item not visible");
            assertTrue(page.getByText("$149.98").first().isVisible(),
                       "Payment page item price 149.98 not visible");

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
            assertTrue(cartEmpty, "Cart does not appear to be empty after deleting product");

            context.close();
            browser.close();
        }
    }
}
