import { test, expect } from '@playwright/test';
import { login } from '../helpers/auth';
import { ApiClient } from '../helpers/api-helpers';

const TEST_USER_ID = '969af1f1-0ea9-4cb6-a40e-666f7e3a0e01';

test.describe('Tasks Management', () => {
    let api: ApiClient;
    let teamId: number;
    let projectId: number;
    let boardId: number;
    let columnId: number;
    let taskId: number;

    test.beforeAll(async ({ request }) => {
        api = new ApiClient(request);
        const teamRes = await api.createTeam('Tasks Test Team', 'desc', TEST_USER_ID);
        teamId = (await teamRes.json()).id;

        const projRes = await api.createProject('Tasks Test Project', 'desc', teamId);
        projectId = (await projRes.json()).id;

        const boardRes = await api.createBoard('Tasks Test Board', 'desc', TEST_USER_ID, projectId);
        const board = await boardRes.json();
        boardId = board.id;

        const colsRes = await api.getColumnsByBoardId(boardId);
        columnId = (await colsRes.json())[0].id;
    });

    test.afterAll(async () => {
        if (teamId) await api.deleteTeam(teamId);
    });

    test.afterEach(async () => {
        if (taskId) {
            await api.deleteTask(taskId);
            taskId = 0;
        }
    });

    test.describe('Tasks - API tests', () => {
        test('should create and update a task via API', async () => {
            const taskTitle = 'API Task ' + Date.now();

            // CREATE
            const createRes = await api.createTask({ title: taskTitle, description: 'api desc', columnId });
            expect(createRes.ok()).toBeTruthy();
            const task = await createRes.json();
            taskId = task.id;
            expect(task.title).toBe(taskTitle);

            // UPDATE STATUS
            const statusRes = await api.changeTaskStatus(taskId, 'IN_PROGRESS');
            expect(statusRes.ok()).toBeTruthy();

            const updatedTaskRes = await api.getTaskById(taskId);
            const updatedTask = await updatedTaskRes.json();
            expect(updatedTask.status).toBe('IN_PROGRESS');
        });
    });

    test.describe('Tasks - UI tests', () => {
        test.beforeEach(async ({ page }) => {
            await login(page, 'user@example.com', 'zaq1@WSX');
            await page.goto(`/team/${teamId}/project/${projectId}/board/${boardId}`);
            await expect(page.getByText('Tasks Test Board')).toBeVisible();
        });

        test('should create a task and move it to another column', async ({ page }) => {
            const taskTitle = 'UI Drag-Drop Task ' + Date.now();

            await page.getByRole('button', { name: 'Add a column' }).click();
            await page.getByPlaceholder('Enter column title...').fill('In Progress Column');
            await page.getByRole('button', { name: 'Save' }).click();
            await expect(page.getByText('In Progress Column')).toBeVisible();

            const firstColumn = page.locator('.kanban-column').first();
            const secondColumn = page.locator('.kanban-column').nth(1);

            await firstColumn.getByRole('button', { name: 'Add a card' }).click();
            await firstColumn.getByPlaceholder('Enter a title for this card...').fill(taskTitle);
            await firstColumn.getByRole('button', { name: 'Add card' }).click();

            const taskCard = page.getByText(taskTitle);
            await expect(taskCard).toBeVisible();
            await expect(firstColumn.getByText(taskTitle)).toBeVisible();

            await taskCard.dragTo(secondColumn.locator('.task-list'));

            await expect(firstColumn.getByText(taskTitle)).not.toBeVisible();
            await expect(secondColumn.getByText(taskTitle)).toBeVisible();

            const tasksAfterMove = await api.getTaskById(taskId);
        });
    });
});