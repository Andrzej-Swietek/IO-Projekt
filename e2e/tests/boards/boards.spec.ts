import { test, expect } from '@playwright/test';
import { login } from '../helpers/auth';
import { ApiClient } from '../helpers/api-helpers';

const TEST_USER_ID = '969af1f1-0ea9-4cb6-a40e-666f7e3a0e01';

test.describe('Boards Management', () => {
    let api: ApiClient;
    let teamId: number;
    let projectId: number;
    let boardId: number;

    test.beforeAll(async ({ request }) => {
        api = new ApiClient(request);
        const teamRes = await api.createTeam('Boards Test Parent Team', 'desc', TEST_USER_ID);
        teamId = (await teamRes.json()).id;
        const projRes = await api.createProject('Boards Test Parent Project', 'desc', teamId);
        projectId = (await projRes.json()).id;
    });

    test.afterAll(async () => {
        if (teamId) {
            await api.deleteTeam(teamId);
        }
    });

    test.afterEach(async () => {
        if (boardId) {
            await api.deleteBoard(boardId);
            boardId = 0;
        }
    });
    test.describe('Boards - API tests', () => {
        test('should create, get, and delete a board via API', async () => {
            const boardName = 'API Board ' + Date.now();

            // CREATE
            const createRes = await api.createBoard(boardName, 'API board desc', TEST_USER_ID, projectId);
            expect(createRes.ok()).toBeTruthy();
            const board = await createRes.json();
            boardId = board.id;
            expect(board.name).toBe(boardName);

            // GET by ID
            const getRes = await api.getBoardById(boardId);
            expect(getRes.ok()).toBeTruthy();
            expect((await getRes.json()).id).toBe(boardId);

            // GET by Project ID
            const getByProjRes = await api.getBoardsByProjectId(projectId);
            expect(getByProjRes.ok()).toBeTruthy();
            const boards = await getByProjRes.json();
            expect(boards.map((b: any) => b.id)).toContain(boardId);
        });

        test('should fail to create a board without a project', async () => {
            const response = await api.createBoard('No Project Board', 'desc', TEST_USER_ID, null);
            expect(response.status()).toBe(400);
        });
    });

    test.describe('Boards - UI tests', () => {
        test.beforeEach(async ({ page }) => {
            await login(page, 'user@example.com', 'zaq1@WSX');
            await page.goto(`/team/${teamId}/project/${projectId}`);
        });

        test('should create a new board from the project page', async ({ page }) => {
            const boardName = 'UI Board ' + Date.now();

            await expect(page.getByRole('heading', { name: 'Boards Test Parent Project' })).toBeVisible();

            await page.getByRole('button', { name: /Create new board/i }).click();

            const createBoardModal = page.getByRole('dialog', { name: /Create new board/i });
            await expect(createBoardModal).toBeVisible();
            await createBoardModal.getByLabel('Board Name').fill(boardName);
            await createBoardModal.getByLabel('Description').fill('UI board description');
            await createBoardModal.getByRole('button', { name: 'Create' }).click();

            await expect(createBoardModal).not.toBeVisible();
            const newBoardCard = page.getByText(boardName);
            await expect(newBoardCard).toBeVisible();

            await newBoardCard.click();
            await expect(page).toHaveURL(/\/board\/\d+$/);
            await expect(page.getByText(boardName)).toBeVisible();

            const boards = await (await api.getBoardsByProjectId(projectId)).json();
            const createdBoard = boards.find((b: any) => b.name === boardName);
            boardId = createdBoard.id;
        });
    });
});