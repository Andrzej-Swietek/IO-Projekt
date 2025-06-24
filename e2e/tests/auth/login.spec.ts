import { test } from '@playwright/test';
import {login, logout} from '../helpers/auth';

test.describe('Authentication', () => {
    test('Successful login with Keycloak', async ({ page }) => {
        await login(page, 'user@example.com', 'zaq1@WSX');
    });

    test('Successful logout', async ({ page }) => {
        await login(page, 'user@example.com', 'zaq1@WSX');
        await logout(page);
    });
});