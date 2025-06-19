import { Page, expect } from '@playwright/test';

export async function login(page: Page, username: string, password: string) {
    await page.goto('http://localhost:5173');

    await openUserMenu(page);

    expect(page.getByRole('menuitem', { name: 'Sign In' }).click());

    await page.locator('#username').waitFor({ state: 'visible', timeout: 60000 });

    await page.locator('#username').fill(username);
    await page.locator('#password').fill(password);
    await page.locator('#kc-login').click();

    await page.waitForURL('http://localhost:5173/**');

    await expect(page.getByText(/Hello/)).toBeVisible();
}

export async function openUserMenu(page: Page) {
    const kebabMenu = page.getByRole('button').nth(1)
    await kebabMenu.click();

    expect(page.getByRole('menuitem', {name: /Sign in|Sign out/}))
}

export async function logout(page: Page) {
    await openUserMenu(page);
    await page.getByRole('menuitem', { name: /Sign out/i }).click();
    await page.waitForURL('http://localhost:5173');
    await openUserMenu(page);
    await expect(page.getByRole('button', { name: 'Sign In' })).toBeVisible();
}

export async function resetAuthState(page: Page) {
    try {
        await logout(page);
    } catch {
        // Jeśli użytkownik już jest wylogowany, kontynuuj
    }
    await page.context().clearCookies();
}