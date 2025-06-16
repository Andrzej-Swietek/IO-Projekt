import { FC } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Project, ProjectControllerApiFactory } from '@/api';
import { RetroContainer } from '@components/common/RetroContainer';
import { RetroButton } from '@components/common/RetroButton';
import { RetroEntryCard } from '@components/common/RetroEntryCard';
import { FolderKanban, Plus } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface TeamBoardsModalProps {
  teamId: number;
  teamName: string;
  onClose: () => void;
}

export const TeamBoardsModal: FC<TeamBoardsModalProps> = ({ teamId, teamName, onClose }) => {
  const navigate = useNavigate();

  const { data: boards, isLoading } = useQuery<Project[]>({
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
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-2xl font-bold text-amber-800">{teamName} Boards</h2>
          <RetroButton
            className="!px-4 !py-2"
            icon={<Plus className="h-5 w-5" />}
            onClick={() => {/* TODO: Implement create board */}}
          >
            New Board
          </RetroButton>
        </div>

        {isLoading ? (
          <div className="text-center">Loading boards...</div>
        ) : boards?.length === 0 ? (
          <div className="text-center text-gray-500">No boards found. Create your first board!</div>
        ) : (
          <div className="space-y-4">
            {boards?.map((board: Project) => (
              <RetroEntryCard
                key={board.id}
                left={<>{board.name}</>}
                right={(
                  <RetroButton
                    className="!px-4 !py-2"
                    icon={<FolderKanban className="h-5 w-5" />}
                    onClick={() => handleBoardClick(board.id!)}
                  >
                    Open
                  </RetroButton>
                )}
              />
            ))}
          </div>
        )}

        <div className="mt-6 flex justify-end">
          <RetroButton onClick={onClose}>Close</RetroButton>
        </div>
      </RetroContainer>
    </div>
  );
}; 