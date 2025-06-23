import { Page, expect } from '@playwright/test';

export async function openUserMenu(page: Page) {
    const menuButton = page.getByRole('button', { name: /user menu|open menu|profil/i });
    await menuButton.click();
}

export async function login(page: Page, username: string, password: string) {
    await page.goto('/');

    await expect(page.getByRole('button', { name: /user menu|open menu|profil/i })).toBeVisible();
    await openUserMenu(page);

    await page.getByRole('menuitem', { name: /Sign In|Zaloguj/i }).click();

    await expect(page).toHaveURL(/.*9098.*/, { timeout: 15000 });

    await page.locator('#username').fill(username);
    await page.locator('#password').fill(password);

    await page.locator('#kc-login').click();
    await page.waitForURL('http://localhost:5173/**', { timeout: 15000 });

    await expect(page.getByText(/Hello/)).toBeVisible({ timeout: 15000 });
}

export async function logout(page: Page) {
    await openUserMenu(page);
    await page.getByRole('menuitem', { name: /Sign Out|Wyloguj/i }).click();

    await page.waitForURL('http://localhost:5173', { timeout: 10000 });

    await openUserMenu(page);
    await expect(page.getByRole('menuitem', { name: /Sign In|Zaloguj/i })).toBeVisible();
}