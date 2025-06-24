import { test, expect } from '@playwright/test';
import { login } from '../helpers/auth';
import { ApiClient } from '../helpers/api-helpers';

const TEST_USER_ID = '969af1f1-0ea9-4cb6-a40e-666f7e3a0e01';

test.describe('Teams Management', () => {
    let api: ApiClient;
    let createdTeamId: number;

    test.beforeAll(async ({ request }) => {
        api = new ApiClient(request);
    });

    test.afterEach(async () => {
        if (createdTeamId) {
            await api.deleteTeam(createdTeamId);
            createdTeamId = 0;
        }
    });

    test.describe('Teams - API tests', () => {
        test('should create, update and delete a team via API', async () => {
            const teamName = 'API Test Team ' + Date.now();

            // CREATE
            const createResponse = await api.createTeam(teamName, 'API description', TEST_USER_ID);
            expect(createResponse.ok()).toBeTruthy();
            const team = await createResponse.json();
            createdTeamId = team.id;
            expect(team.name).toBe(teamName);

            // UPDATE
            const updatedName = teamName + ' (updated)';
            const updateResponse = await api.updateTeam(createdTeamId, { ...team, name: updatedName });
            expect(updateResponse.ok()).toBeTruthy();
            const updatedTeam = await updateResponse.json();
            expect(updatedTeam.name).toBe(updatedName);

        });

        test('should fail to create a team without a name', async () => {
            const response = await api.createTeam('', 'No name description', TEST_USER_ID);
            expect(response.status()).toBe(400);
        });
    });

    test.describe('Teams - UI tests', () => {
        test.beforeEach(async ({ page }) => {
            await login(page, 'user@example.com', 'zaq1@WSX');
        });

        test('should create and delete a team through the UI', async ({ page }) => {
            const teamName = 'UI Test Team ' + Date.now();

            await page.getByRole('button', { name: 'Add a new team' }).click();

            await expect(page.getByRole('dialog')).toBeVisible();
            await page.getByLabel('Team Name').fill(teamName);
            await page.getByLabel('Description').fill('UI test description');
            await page.getByRole('button', { name: 'Create' }).click();

            await expect(page.getByRole('dialog')).not.toBeVisible();
            const newTeamCard = page.getByText(teamName);
            await expect(newTeamCard).toBeVisible();

            const teamsResponse = await api.getTeamsByUserId(TEST_USER_ID);
            const teams = await teamsResponse.json();
            const createdTeam = teams.find((t: any) => t.name === teamName);
            expect(createdTeam).toBeDefined();
            createdTeamId = createdTeam.id;


            page.on('dialog', dialog => dialog.accept());
            await newTeamCard.locator('../..').getByRole('button', { name: /delete/i }).click();

            await expect(newTeamCard).not.toBeVisible();
            createdTeamId = 0;
        });
    });
});