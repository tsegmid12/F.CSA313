import { test, expect, type Page } from '@playwright/test';

const BASE_URL = 'https://www.saucedemo.com/';

// Нэвтрэх дэлгэц рүү очиж, нэвтрэх үйлдлийг бие даасан функцээр гүйцэтгэнэ.
async function login(page: Page) {
  await page.goto(BASE_URL);
  await page.getByPlaceholder('Username').fill('standard_user');
  await page.getByPlaceholder('Password').fill('secret_sauce');
  await page.getByRole('button', { name: 'Login' }).click();
  await expect(page).toHaveURL(/inventory\.html/);
}

// Тест бүрийн дараа нэвтрэх хуудас руу буцаана.
async function logout(page: Page) {
  await page.locator('#react-burger-menu-btn').click();
  await page.getByRole('button', { name: 'Logout' }).click();
  await expect(page).toHaveURL(BASE_URL);
}

test.describe('SauceDemo UI тестүүд', () => {
  test('Амжилттай нэвтрэх', async ({ page }) => {
    await login(page);

    await expect(page.getByText('Products')).toBeVisible();
    await expect(page).toHaveURL(/inventory\.html/);

    await logout(page);
  });

  test('Буруу нууц үгээр нэвтрэхэд алдааны мэдэгдэл гарна', async ({ page }) => {
    await page.goto(BASE_URL);
    await page.getByPlaceholder('Username').fill('standard_user');
    await page.getByPlaceholder('Password').fill('wrong_password');
    await page.getByRole('button', { name: 'Login' }).click();

    await expect(page.getByText('Epic sadface: Username and password do not match any user in this service')).toBeVisible();
    await expect(page).toHaveURL(BASE_URL);
  });

  test('Нэвтэрсний дараа бараа сагслах', async ({ page }) => {
    await login(page);
    await expect(page.getByText('Products')).toBeVisible();

    await page.getByRole('button', { name: 'Add to cart' }).first().click();
    await expect(page.locator('.shopping_cart_badge')).toHaveText('1');

    await page.getByRole('button', { name: 'Cart, 1 items' }).click();
    await expect(page).toHaveURL(/cart\.html/);
    await expect(page.getByText('Your Cart')).toBeVisible();

    await page.locator('#react-burger-menu-btn').click();
    await page.getByRole('button', { name: 'Logout' }).click();
  });
});
