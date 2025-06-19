import { APIRequestContext, expect } from '@playwright/test';

export async function createTeam(request: APIRequestContext, name: string) {
    const response = await request.post('/api/v1/team', {
        data: {
            name: name,
            description: 'Test team description',
            creatorId: 'test-user-id'
        }
    });
    expect(response.ok()).toBeTruthy();
    return (await response.json()).id;
}