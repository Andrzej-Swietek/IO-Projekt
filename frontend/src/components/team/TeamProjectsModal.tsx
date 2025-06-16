import { FC, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { ProjectControllerApiFactory, Project, BoardControllerApiFactory, Board } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroButton } from '@components/common/RetroButton';
import { RetroEntryCard } from '@components/common/RetroEntryCard';
import { CreateProjectModal } from '@components/project/CreateProjectModal';
import { CreateBoardModal } from '@components/board/CreateBoardModal';

interface TeamProjectsModalProps {
  teamId: number;
  teamName: string;
  onClose: () => void;
}

export const TeamProjectsModal: FC<TeamProjectsModalProps> = ({ teamId, teamName, onClose }) => {
  const navigate = useNavigate();
  const [showCreateProjectModal, setShowCreateProjectModal] = useState(false);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);
  const [showCreateBoardModal, setShowCreateBoardModal] = useState(false);

  const { data: projects, isLoading: isProjectsLoading } = useQuery<Project[], Error>({
    queryKey: ['team-projects', teamId],
    queryFn: async () => {
      const response = await ProjectControllerApiFactory().getProjectsByTeamId(teamId);
      return response.data;
    },
  });

  const { data: boards, isLoading: isBoardsLoading } = useQuery<Board[], Error>({
    queryKey: ['project-boards', selectedProject?.id],
    queryFn: async () => {
      if (!selectedProject?.id) return [];
      const response = await BoardControllerApiFactory().getBoardsByProjectId(selectedProject.id);
      return response.data;
    },
    enabled: !!selectedProject?.id,
  });

  if (isProjectsLoading) {
    return (
      <RetroModal onClose={onClose} title={`${teamName} - Projects`}>
        <div className="flex items-center justify-center h-32">Loading projects...</div>
      </RetroModal>
    );
  }

  if (selectedProject) {
    return (
      <>
        <RetroModal onClose={onClose} title={selectedProject.name || ''}>
          <div className="flex flex-col gap-4">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-semibold">Boards</h2>
              <div className="flex gap-2">
                <RetroButton size="sm" onClick={() => setShowCreateBoardModal(true)}>
                  New Board
                </RetroButton>
                <RetroButton size="sm" variant="secondary" icon={null} onClick={onClose}>
                  Close
                </RetroButton>
              </div>
            </div>
            <div className="flex flex-col gap-2">
              {isBoardsLoading ? (
                <div className="flex items-center justify-center h-32">Loading boards...</div>
              ) : boards?.length === 0 ? (
                <div className="text-center text-gray-500 py-4">No boards found. Create a new board to get started.</div>
              ) : (
                boards?.map((board) => (
                  <RetroEntryCard
                    key={board.id}
                    left={<>{board.name}</>}
                    right={(
                      <RetroButton
                        size="sm"
                        className="!px-4 !py-2 w-auto"
                        onClick={() => navigate(`/board/${board.id}`)}
                      >
                        Open Board
                      </RetroButton>
                    )}
                  />
                ))
              )}
            </div>
          </div>
        </RetroModal>

        {showCreateBoardModal && selectedProject.id && (
          <CreateBoardModal
            projectId={selectedProject.id}
            onClose={() => setShowCreateBoardModal(false)}
          />
        )}
      </>
    );
  }

  return (
    <>
      <RetroModal onClose={onClose} title={`${teamName} - Projects`}>
        <div className="flex flex-col gap-4">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-semibold">Projects</h2>
            <div className="flex gap-2">
              <RetroButton size="sm" onClick={() => setShowCreateProjectModal(true)}>
                New Project
              </RetroButton>
              <RetroButton size="sm" variant="secondary" icon={null} onClick={onClose}>
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
                    size="sm"
                    className="!px-4 !py-2 w-auto"
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
    </>
  );
}; 