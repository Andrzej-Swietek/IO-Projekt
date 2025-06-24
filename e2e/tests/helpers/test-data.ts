export const testTeam = {
    name: 'Test Team ' + Math.random().toString(36).substring(7),
    description: 'Automated test team'
};

export const testProject = {
    name: 'Test Project',
    description: 'Project created by E2E tests',
    teamId: null // będzie ustawiane dynamicznie
};