import { test, expect } from '@playwright/test';

test.describe('SauceDemo Login Tests', () => {

  test('амжилттай нэвтрэх', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');

    await page.getByPlaceholder('Username').fill('standard_user');
    await page.getByPlaceholder('Password').fill('secret_sauce');
    await page.getByRole('button', { name: 'Login' }).click();

    // Шалгалт: URL зөв эсэхийг шалгах
    await expect(page).toHaveURL(/.*inventory.html/);

    // Шалгалт: "Products" текст харагдаж буйг шалгах
    const productTitle = page.locator('.title');
    await expect(productTitle).toBeVisible();
    await expect(productTitle).toHaveText('Products');
  });

  test('амжилтгүй нэвтрэх (буруу нууц үг)', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');

    await page.getByPlaceholder('Username').fill('standard_user');
    await page.getByPlaceholder('Password').fill('wrong_password');
    await page.getByRole('button', { name: 'Login' }).click();

    // Шалгалт: Алдааны мессеж гарч буйг шалгах
    const errorMessage = page.locator('[data-test="error"]');
    await expect(errorMessage).toBeVisible();
    await expect(errorMessage).toContainText('Username and password do not match');
  });

  test('нэвтэрсний дараа бараа сагсалгах', async ({ page }) => {
    await page.goto('https://www.saucedemo.com');

    await page.getByPlaceholder('Username').fill('standard_user');
    await page.getByPlaceholder('Password').fill('secret_sauce');
    await page.getByRole('button', { name: 'Login' }).click();

    // Барааг сагсалгах
    await page.locator('[data-test="add-to-cart-sauce-labs-backpack"]').click();

    // Шалгалт: Сагсны тоо 1 болсон эсэхийг шалгах
    const cartBadge = page.locator('.shopping_cart_badge');
    await expect(cartBadge).toBeVisible();
    await expect(cartBadge).toHaveText('1');
  });
});
