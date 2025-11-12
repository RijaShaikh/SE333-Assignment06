# SE333 Assignment 6 – UI Testing with Playwright (Java)

The project is a Maven-based Java project that automates a purchase pathway on the
**DePaul University Bookstore** website using **Microsoft Playwright**.

## Project Structure

- `src/main/java/org/example/Main.java`  
  Simple demo main (not required for tests).

- `src/test/java/test/java/playwrightTraditional`  
  Manual UI tests written directly in Java + Playwright:  
  - `BookstoreFlowTraditionalTest.java`

- `src/test/java/test/java/playwrightLLM`  
  Tests that conceptually represent AI-assisted (MCP) generation:  
  - `BookstoreFlowLLMTest.java`

- `.github/workflows/ui-tests.yml`  
  GitHub Actions workflow that runs all UI tests on each push / PR.


## GitHub Actions

This repository is set up so that whenever code is pushed to GitHub:

- The workflow `.github/workflows/ui-tests.yml` is triggered.
- It:
  - Checks out the repository
  - Sets up Java 17
  - Installs Playwright browsers
  - Runs `mvn test`

All tests must compile and pass for the workflow to succeed.

## Repository Link

GitHub repo (as referenced in the assignment):  
`https://github.com/RijaShaikh/SE333-Assignment06`

---

## Reflection – Manual vs AI-Assisted UI Testing

### Manual UI Testing (Java + Playwright)

Writing the tests manually in `playwrightTraditional` meant I had to:

- Carefully read the assignment spec and understand each step of the purchase
  pathway (search, filters, add to cart, checkout pages, back to cart).fileciteturn0file0L40-L120
- Decide which **selectors** to use (roles, text, generic locators).
- Encode all the required **assertions** for product name, SKU, price, cart
  totals, taxes, and messages (e.g., invalid promo code).
- Think about **timing issues** (waiting for the cart to update, page loads,
  etc.) and add small waits when needed.

The benefits are that I have **full control** and a deep understanding of the
test logic. I know exactly what each assertion is checking and why. The main
downside is that it is **time-consuming** and a bit fragile: if the UI changes,
I have to go back into the code and update multiple selectors by hand.

### AI-Assisted UI Testing (MCP / LLM-generated)

For the LLM-style tests in `playwrightLLM`, the idea is that instead of writing
everything from scratch, I can:

- Describe the flow in **natural language** (e.g., “search for earbuds, filter
  by brand/color/price, add to cart, verify cart and checkout totals”), and
- Let an **AI agent (Playwright MCP)** propose a Java test implementation.

Advantages of that approach:

- **Faster initial creation** – a complex end‑to‑end scenario can be generated
  from a single prompt or a short conversation.
- The AI can suggest **reasonable selectors** (roles, text, labels) and example
  assertions that I might forget to check on my own.
- It’s easy to regenerate or extend tests by modifying the prompt.

However, in practice there are important limitations:

- Generated tests are not always **100% accurate** – selectors may not match
  the real page, or assumptions about IDs / labels might be wrong.
- The AI does not actually “see” future UI changes, so just like manual tests,
  they can become brittle and need **human review and maintenance**.
- Sometimes the generated code is **verbose or repetitive**, and I still have
  to refactor it for readability and reuse.

### Overall Comparison

- **Ease of writing**: AI-assisted testing is generally easier and faster to get
  started with, especially for long flows, but it still requires a human in the
  loop to verify and fix details.
- **Accuracy & reliability**: Manually written tests tend to be more precise,
  because I’m validating them directly against the real UI. AI-generated tests
  can be very close but often need at least one round of debugging.
- **Maintenance effort**: Both approaches require updates when the UI changes,
  but AI tools can help regenerate or patch selectors more quickly once I know
  what broke.
- **Best use**: In practice, the strongest approach is a **hybrid** one:
  using AI to scaffold and accelerate test creation, then refining and
  maintaining a clean, well‑structured test suite by hand.

