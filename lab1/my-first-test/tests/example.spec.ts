import { test, expect } from "@playwright/test";

// Гарчиг "Playwright" гэдгийг агуулж байгааг шалгана
test("has title", async ({ page }) => {
  await page.goto("https://playwright.dev/");

  await expect(page).toHaveTitle(/Playwright/);
});

// "Get started" линк дээр дарж Installation хуудас руу шилждэгийг шалгана
test("get started link", async ({ page }) => {
  await page.goto("https://playwright.dev/");

  await page.getByRole("link", { name: "Get started" }).click();

  await expect(
    page.getByRole("heading", { name: "Installation" }),
  ).toBeVisible();
});
