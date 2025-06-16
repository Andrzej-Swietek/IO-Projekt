import { useParams } from 'react-router-dom';
import { FC, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { BoardControllerApiFactory } from '@/api';
import { KanbanBoard } from '@components/board/KanbanBoard.tsx';
import { AddMemberModal } from '@components/team/AddMemberModal';

interface BoardPageProps {
}

export const BoardPage: FC<BoardPageProps> = () => {
  const { id, teamId } = useParams();
  const [showAddMemberModal, setShowAddMemberModal] = useState(false);

  const { data: board, isLoading } = useQuery({
    queryKey: ['board', id],
    queryFn: async () => {
      if (!id) throw new Error('Board ID is required');
      const response = await BoardControllerApiFactory().getBoardById(Number(id));
      return response.data;
    },
    enabled: !!id,
  });

  return (
    <div className="min-h-screen w-full px-8">
      <header className="!mb-32 text-center mt-8">
        <h1 className="mb-4 text-4xl font-bold tracking-tight text-amber-800 md:text-5xl">
          {isLoading ? 'Loading...' : board?.name || 'Board'}
        </h1>
        <p className="text-amber-700">{board?.description || 'No description available'}</p>
      </header>
      <aside>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-2xl font-bold text-amber-800">Team</h2>
          <button 
            className="rounded bg-amber-700 px-4 py-2 text-white hover:bg-amber-600 cursor-pointer"
            onClick={() => setShowAddMemberModal(true)}
          >
            Add Member
          </button>
        </div>
      </aside>
      <KanbanBoard teamId={teamId ? Number(teamId) : undefined} />

      {showAddMemberModal && teamId && (
        <AddMemberModal
          teamId={Number(teamId)}
          onClose={() => setShowAddMemberModal(false)}
        />
      )}
    </div>
  );
};
