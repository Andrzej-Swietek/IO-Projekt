import { useParams } from 'react-router-dom';
import { FC, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { BoardControllerApiFactory } from '@/api';
import { KanbanBoard } from '@components/board/KanbanBoard.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { AddMemberModal } from '@components/team/AddMemberModal.tsx';
import { AddColumnModal, GenerateWithAIBox } from '@components/board';
import { UserPlus } from 'lucide-react';
import { useNavigate } from 'react-router';
import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { useTranslation } from 'react-i18next';

interface BoardPageProps {
}

export const BoardPage: FC<BoardPageProps> = () => {
  const { id, teamId } = useParams();
  const { profile } = useUserProfile();
  const [showAddMemberModal, setShowAddMemberModal] = useState(false);
  const [showAddColumnModal, setShowAddColumnModal] = useState(false);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const { data: board, isLoading } = useQuery({
    queryKey: ['board', id],
    queryFn: async () => {
      if (!id) throw new Error('Board ID is required');
      const response = await BoardControllerApiFactory().getBoardById(Number(id));
      return response.data;
    },
    enabled: !!id,
  });

  if (!isLoading && !profile?.id) {
    navigate('/auth/login');
  }

  return (
    <div className="min-h-screen w-full px-8 mb-8">
      <header className="!mb-32 text-center mt-8">
        <h1 className="mb-4 text-4xl font-bold tracking-tight text-primary md:text-5xl">
          {isLoading ? t('board.loading') : board?.name || t('board.untitled')}
        </h1>
        <p className="text-gray-600">
          {board?.description || t('board.noDescription')}
        </p>
      </header>
      <aside>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-2xl font-bold text-primary">{t('board.teamSectionTitle')}</h2>
          <div className="flex gap-4">
            <RetroButton
              className="px-4 py-2 cursor-pointer"
              onClick={() => setShowAddColumnModal(true)}
            >
              {t('board.addColumn')}
            </RetroButton>
            <RetroButton
              className="px-4 py-2 cursor-pointer"
              icon={<UserPlus />}
              onClick={() => setShowAddMemberModal(true)}
            >
              {t('board.addMember')}
            </RetroButton>
          </div>
        </div>
      </aside>
      <div className="w-full flex items-center justify-between">
        {board && <GenerateWithAIBox board={board} />}
      </div>
      <KanbanBoard teamId={teamId ? Number(teamId) : undefined} />

      {showAddMemberModal && teamId && (
        <AddMemberModal
          teamId={Number(teamId)}
          onClose={() => setShowAddMemberModal(false)}
        />
      )}

      {showAddColumnModal && board?.id && (
        <AddColumnModal
          boardId={board?.id}
          position={board?.columns?.length ?? 1}
          onClose={() => setShowAddColumnModal(false)}
        />
      )}
    </div>
  );
};
