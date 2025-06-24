import { APIRequestContext } from '@playwright/test';

export const API_URL = 'http://localhost:5173/api/v1';

export class ApiClient {
    constructor(private request: APIRequestContext) {}

    // === Teams ===
    async createTeam(name: string, description: string, creatorId: string) {
        return this.request.post(`${API_URL}/team`, { data: { name, description, creatorId } });
    }
    async getTeamById(id: number) {
        return this.request.get(`${API_URL}/team/${id}`);
    }
    async updateTeam(id: number, teamData: any) {
        return this.request.put(`${API_URL}/team/${id}`, { data: teamData });
    }
    async deleteTeam(id: number) {
        return this.request.delete(`${API_URL}/team/${id}`);
    }
    async getTeamsByUserId(userId: string) {
        return this.request.get(`${API_URL}/team/user/${userId}`);
    }
    async addTeamMember(teamId: number, userId: string, role: string) {
        return this.request.post(`${API_URL}/team/add-team-member`, { data: { teamId, teamMember: { userId, role } } });
    }

    // === Projects ===
    async createProject(name: string, description: string, teamId: number) {
        return this.request.post(`${API_URL}/project`, { data: { name, description, teamId } });
    }
    async getProjectById(id: number) {
        return this.request.get(`${API_URL}/project/${id}`);
    }
    async updateProject(id: number, projectData: any) {
        return this.request.put(`${API_URL}/project/${id}`, { data: projectData });
    }
    async deleteProject(id: number) {
        return this.request.delete(`${API_URL}/project/${id}`);
    }
    async getProjectsByTeamId(teamId: number) {
        return this.request.get(`${API_URL}/project/team/${teamId}`);
    }

    // === Boards ===
    async createBoard(name: string, description: string, ownerId: string, projectId: number) {
        return this.request.post(`${API_URL}/board`, { data: { name, description, ownerId, projectId } });
    }
    async getBoardById(id: number) {
        return this.request.get(`${API_URL}/board/${id}`);
    }
    async updateBoard(id: number, boardData: any) {
        return this.request.put(`${API_URL}/board/${id}`, { data: boardData });
    }
    async deleteBoard(id: number) {
        return this.request.delete(`${API_URL}/board/${id}`);
    }
    async getBoardsByProjectId(projectId: number) {
        return this.request.get(`${API_URL}/board/project/${projectId}`);
    }

    // === Tasks ===
    async createTask(taskData: any) {
        return this.request.post(`${API_URL}/task`, { data: taskData });
    }
    async getTaskById(id: number) {
        return this.request.get(`${API_URL}/task/${id}`);
    }
    async updateTask(id: number, taskData: any) {
        return this.request.put(`${API_URL}/task/${id}`, { data: taskData });
    }
    async deleteTask(id: number) {
        return this.request.delete(`${API_URL}/task/${id}`);
    }
    async changeTaskStatus(id: number, status: 'TODO' | 'IN_PROGRESS' | 'DONE' | 'BLOCKED') {
        return this.request.patch(`${API_URL}/task/${id}/status`, { data: status });
    }

    // === Columns ===
    async getColumnsByBoardId(boardId: number) {
        return this.request.get(`${API_URL}/columns/board/${boardId}`);
    }
}