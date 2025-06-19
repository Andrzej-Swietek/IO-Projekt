import { test, expect } from '@playwright/test';
import { login } from '../helpers/auth';
import { createTeam } from '../helpers/api';

test.describe('Teams Management', () => {
    test.beforeEach(async ({ page }) => {
        await login(page, 'user@example.com', 'zaq1@WSX');
    });

    test('Create new team', async ({ page }) => {
        await page.getByRole('button', { name: 'Create Team' }).click();
        await page.getByLabel('Team Name').fill('Dream Team');
        await page.getByLabel('Description').fill('Our awesome team');
        await page.getByRole('button', { name: 'Submit' }).click();

        await expect(page.getByText('Dream Team')).toBeVisible();
    });

    test('Add team member', async ({ page, request }) => {
        const teamId = await createTeam(request, 'Test Team');
        await page.goto(`/teams/${teamId}`);

        await page.getByRole('button', { name: 'Add Member' }).click();
        await page.getByLabel('User Email').fill('member@example.com');
        await page.getByRole('button', { name: 'Add' }).click();

        await expect(page.getByText('member@example.com')).toBeVisible();
    });
});