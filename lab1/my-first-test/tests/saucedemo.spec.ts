import { test, expect } from "@playwright/test";

//  https://www.saucedemo.com
const BASE_URL = "https://www.saucedemo.com";

test.describe("Saucedemo нэвтрэх ба сагслах тестүүд", () => {
  // Зөв нэвтрэх мэдээллээр амжилттай нэвтэрч, гарахыг шалгана
  test("амжилттай нэвтрэх", async ({ page }) => {
    await page.goto(BASE_URL);

    await page.getByPlaceholder("Username").fill("standard_user");
    await page.getByPlaceholder("Password").fill("secret_sauce");
    await page.getByRole("button", { name: "Login" }).click();

    await expect(page.getByText("Products")).toBeVisible();
    await expect(page).toHaveURL(/inventory.html/);

    await page.locator("#react-burger-menu-btn").click();
    await page.getByRole("link", { name: "Logout" }).click();
  });

  // Буруу нууц үгээр нэвтрэхэд алдааны мэдэгдэл гардгийг шалгана
  test("амжилтгүй нэвтрэх болон буруу нууц үг", async ({ page }) => {
    await page.goto(BASE_URL);

    await page.getByPlaceholder("Username").fill("standard_user");
    await page.getByPlaceholder("Password").fill("wrong_password");
    await page.getByRole("button", { name: "Login" }).click();

    await expect(
      page.getByText("Username and password do not match"),
    ).toBeVisible();
    await expect(page).toHaveURL(BASE_URL + "/");
  });

  // Нэвтэрсний дараа бараа сагслаж, сагсны тоо зөв харагдахыг шалгана
  test("нэвтэрсний дараа бараа сагслах", async ({ page }) => {
    await page.goto(BASE_URL);

    await page.getByPlaceholder("Username").fill("standard_user");
    await page.getByPlaceholder("Password").fill("secret_sauce");
    await page.getByRole("button", { name: "Login" }).click();
    await expect(page.getByText("Products")).toBeVisible();

    await page.getByRole("button", { name: "Add to cart" }).first().click();
    await expect(page.locator(".shopping_cart_badge")).toHaveText("1");

    await page.locator("#react-burger-menu-btn").click();
    await page.getByRole("link", { name: "Logout" }).click();
  });
});
