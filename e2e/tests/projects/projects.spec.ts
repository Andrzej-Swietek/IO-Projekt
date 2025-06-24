import { test, expect } from '@playwright/test';
import { login } from '../helpers/auth';
import { ApiClient } from '../helpers/api-helpers';

const TEST_USER_ID = '969af1f1-0ea9-4cb6-a40e-666f7e3a0e01';

test.describe('Projects Management', () => {
    let api: ApiClient;
    let teamId: number;
    let projectId: number;

    test.beforeAll(async ({ request }) => {
        api = new ApiClient(request);
        const teamResponse = await api.createTeam('Projects Test Parent Team', 'Parent team for project tests', TEST_USER_ID);
        const team = await teamResponse.json();
        teamId = team.id;
    });

    test.afterAll(async () => {
        if (teamId) {
            await api.deleteTeam(teamId);
        }
    });

    test.afterEach(async () => {
        if (projectId) {
            await api.deleteProject(projectId);
            projectId = 0;
        }
    });

    test.describe('Projects - API tests', () => {
        test('should create, update and delete a project via API', async () => {
            const projectName = 'API Project ' + Date.now();

            // CREATE
            const createResponse = await api.createProject(projectName, 'API project desc', teamId);
            expect(createResponse.ok()).toBeTruthy();
            const project = await createResponse.json();
            projectId = project.id;
            expect(project.name).toBe(projectName);

            // UPDATE
            const updatedName = projectName + ' (updated)';
            const updateResponse = await api.updateProject(projectId, { ...project, name: updatedName });
            expect(updateResponse.ok()).toBeTruthy();
            const updatedProject = await updateResponse.json();
            expect(updatedProject.name).toBe(updatedName);

            // GET by ID
            const getResponse = await api.getProjectById(projectId);
            expect(getResponse.ok()).toBeTruthy();
            expect((await getResponse.json()).id).toBe(projectId);
        });

        test('should retrieve all projects for a given team ID', async () => {
            const proj1Res = await api.createProject('API Project 1', 'desc', teamId);
            const proj2Res = await api.createProject('API Project 2', 'desc', teamId);
            const project1 = await proj1Res.json();
            const project2 = await proj2Res.json();

            const response = await api.getProjectsByTeamId(teamId);
            expect(response.ok()).toBeTruthy();
            const projects = await response.json();

            const projectIds = projects.map((p: any) => p.id);
            expect(projectIds).toContain(project1.id);
            expect(projectIds).toContain(project2.id);

            await api.deleteProject(project1.id);
            await api.deleteProject(project2.id);
        });
    });

    test.describe('Projects - UI tests', () => {
        test.beforeEach(async ({ page }) => {
            await login(page, 'user@example.com', 'zaq1@WSX');
        });

        test('should create a new project using the projects modal', async ({ page }) => {
            const projectName = 'UI Project ' + Date.now();

            const teamCard = page.getByText('Projects Test Parent Team');
            await expect(teamCard).toBeVisible();
            await teamCard.locator('../..').getByRole('button', { name: 'Projects' }).click();

            const projectsModal = page.getByRole('dialog', { name: 'Projects Test Parent Team Projects' });
            await expect(projectsModal).toBeVisible();
            await projectsModal.getByRole('button', { name: 'Create new project' }).click();

            const createProjectModal = page.getByRole('dialog', { name: 'Create new project' });
            await expect(createProjectModal).toBeVisible();

            await createProjectModal.getByLabel('Project Name').fill(projectName);
            await createProjectModal.getByLabel('Description').fill('UI project description');
            await createProjectModal.getByRole('button', { name: 'Create' }).click();

            await expect(createProjectModal).not.toBeVisible();
            await expect(projectsModal.getByText(projectName)).toBeVisible();

            await projectsModal.getByRole('button', { name: 'Close' }).click();
            await expect(projectsModal).not.toBeVisible();

            const projects = await (await api.getProjectsByTeamId(teamId)).json();
            const createdProject = projects.find((p: any) => p.name === projectName);
            projectId = createdProject.id;
        });
    });
});