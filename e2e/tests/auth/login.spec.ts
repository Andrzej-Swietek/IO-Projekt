import { test, expect } from '@playwright/test';
import { login } from '../helpers/auth';

test.describe('Authentication', () => {
    test('Successful login with Keycloak', async ({ page }) => {
        await login(page, 'user@example.com', 'zaq1@WSX');
        await expect(page.getByText('Hello')).toBeVisible();
    });
});