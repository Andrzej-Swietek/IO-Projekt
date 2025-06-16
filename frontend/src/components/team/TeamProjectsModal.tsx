import { FC, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Project, ProjectControllerApiFactory } from '@/api';
import { RetroContainer } from '@components/common/RetroContainer';
import { RetroButton } from '@components/common/RetroButton';
import { RetroEntryCard } from '@components/common/RetroEntryCard';
import { FolderKanban, Plus } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { CreateBoardModal } from '@components/board/CreateBoardModal';
import { CreateProjectModal } from '@components/project/CreateProjectModal';

interface TeamProjectsModalProps {
  teamId: number;
  teamName: string;
  onClose: () => void;
}

export const TeamProjectsModal: FC<TeamProjectsModalProps> = ({ teamId, teamName, onClose }) => {
  const navigate = useNavigate();
  const [showCreateBoard, setShowCreateBoard] = useState(false);
  const [showCreateProject, setShowCreateProject] = useState(false);

  const { data: projects, isLoading } = useQuery<Project[]>({
    queryKey: ['team-boards', teamId],
    queryFn: async () => {
      const response = await ProjectControllerApiFactory().getProjectsByTeamId(teamId);
      return response.data;
    },
  });

  const handleBoardClick = (boardId: number) => {
    navigate(`/board/${boardId}`);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <RetroContainer className="w-[600px] max-h-[80vh] overflow-y-auto p-8">
        <div className="mb-8 flex items-center justify-between">
          <h2 className="text-2xl font-bold text-amber-800">{teamName} Projects</h2>
          <div className="flex gap-4">
            <RetroButton
              className="!px-4 !py-2"
              icon={<Plus className="h-5 w-5" />}
              onClick={() => setShowCreateProject(true)}
            >
              New Project
            </RetroButton>
            <RetroButton
              className="!px-4 !py-2"
              icon={<Plus className="h-5 w-5" />}
              onClick={() => setShowCreateBoard(true)}
            >
              New Board
            </RetroButton>
          </div>
        </div>

        {isLoading ? (
          <div className="text-center">Loading projects...</div>
        ) : projects?.length === 0 ? (
          <div className="text-center text-gray-500">No projects found. Create your first project!</div>
        ) : (
          <div className="space-y-6">
            {projects?.map((project: Project) => (
              <RetroEntryCard
                key={project.id}
                left={<>{project.name}</>}
                right={(
                  <RetroButton
                    className="!px-4 !py-2"
                    icon={<FolderKanban className="h-5 w-5" />}
                    onClick={() => handleBoardClick(project.id!)}
                  >
                    Open
                  </RetroButton>
                )}
              />
            ))}
          </div>
        )}

        <div className="mt-8 flex justify-end">
          <RetroButton onClick={onClose}>Close</RetroButton>
        </div>
      </RetroContainer>

      {showCreateBoard && (
        <CreateBoardModal
          onClose={() => setShowCreateBoard(false)}
          projectId={teamId}
        />
      )}

      {showCreateProject && (
        <CreateProjectModal
          onClose={() => setShowCreateProject(false)}
          teamId={teamId}
        />
      )}
    </div>
  );
}; 