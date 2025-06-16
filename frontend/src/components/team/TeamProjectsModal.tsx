import { FC, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ProjectControllerApiFactory, Project } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroButton } from '@components/common/RetroButton';
import { RetroEntryCard } from '@components/common/RetroEntryCard';
import { CreateProjectModal } from '@components/project/CreateProjectModal';
import { CreateBoardModal } from '@components/board/CreateBoardModal';
import { KanbanBoard } from '@components/board/KanbanBoard';

interface TeamProjectsModalProps {
  teamId: number;
  teamName: string;
  onClose: () => void;
}

export const TeamProjectsModal: FC<TeamProjectsModalProps> = ({ teamId, teamName, onClose }) => {
  const [showCreateProjectModal, setShowCreateProjectModal] = useState(false);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [showCreateBoardModal, setShowCreateBoardModal] = useState(false);

  const { data: projects, isLoading } = useQuery<Project[], Error>({
    queryKey: ['team-projects', teamId],
    queryFn: async () => {
      const response = await ProjectControllerApiFactory().getProjectsByTeamId(teamId);
      return response.data;
    },
  });

  if (isLoading) {
    return (
      <RetroModal onClose={onClose} title={`${teamName} - Projects`}>
        <div className="flex items-center justify-center h-32">Loading projects...</div>
      </RetroModal>
    );
  }

  if (selectedProject) {
    return (
      <RetroModal onClose={onClose} title={selectedProject.name || ''}>
        <div className="flex flex-col gap-4">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-semibold">Boards</h2>
            <div className="flex gap-2">
              <RetroButton onClick={() => setShowCreateBoardModal(true)}>
                New Board
              </RetroButton>
              <RetroButton variant="secondary" onClick={onClose}>
                Close
              </RetroButton>
            </div>
          </div>
          {selectedProject.id && <KanbanBoard projectId={selectedProject.id} />}
        </div>
      </RetroModal>
    );
  }

  return (
    <>
      <RetroModal onClose={onClose} title={`${teamName} - Projects`}>
        <div className="flex flex-col gap-4">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-semibold">Projects</h2>
            <div className="flex gap-2">
              <RetroButton onClick={() => setShowCreateProjectModal(true)}>
                New Project
              </RetroButton>
              <RetroButton variant="secondary" onClick={onClose}>
                Close
              </RetroButton>
            </div>
          </div>
          <div className="flex flex-col gap-2">
            {projects?.map((project: Project) => (
              <RetroEntryCard
                key={project.id || 'temp-id'}
                left={<>{project.name}</>}
                right={(
                  <RetroButton
                    className="!px-4 !py-4 w-auto"
                    onClick={() => setSelectedProject(project)}
                  >
                    View Boards
                  </RetroButton>
                )}
              />
            ))}
          </div>
        </div>
      </RetroModal>

      {showCreateProjectModal && (
        <CreateProjectModal
          teamId={teamId}
          onClose={() => setShowCreateProjectModal(false)}
        />
      )}

      {showCreateBoardModal && selectedProject?.id && (
        <CreateBoardModal
          projectId={selectedProject.id}
          onClose={() => setShowCreateBoardModal(false)}
        />
      )}
    </>
  );
}; 