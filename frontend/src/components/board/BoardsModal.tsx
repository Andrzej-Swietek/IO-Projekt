import { Plus, User } from 'lucide-react';
import { FC, useState } from 'react';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { RetroModal } from '@components/common/RetroModal.tsx';

interface BoardModalProps {
  isOpen: boolean;
  onClose: () => void;
  teamName: string;
}

interface Board {
  id: number;
  name: string;
  users: Array<{ id: number; name: string }>;
}

export const BoardModal: FC<BoardModalProps> = ({ isOpen, onClose, teamName }) => {
  const [boards, setBoards] = useState<Board[]>([
    {
      id: 1,
      name: 'board name',
      users: [{ id: 1, name: 'User 1' }],
    },
    {
      id: 2,
      name: 'board name',
      users: [
        { id: 1, name: 'User 1' },
        { id: 2, name: 'User 2' },
      ],
    },
  ]);

  const addNewBoard = () => {
    const newBoard: Board = {
      id: boards.length + 1,
      name: 'board name',
      users: [{ id: 1, name: 'User 1' }],
    };
    setBoards([...boards, newBoard]);
  };

  return (
    <RetroModal isOpen={isOpen} onClose={onClose} title={teamName}>
      <div className="space-y-4 py-2">
        {boards.map(board => (
          <div
            key={board.id}
            className="bg-white border-2 border-black shadow-[2px_2px_0px_#000000] p-3 flex justify-between items-center"
          >
            <span className="font-medium text-gray-800">{board.name}</span>
            <div className="flex -space-x-2">
              {board.users.map(user => (
                <div
                  key={user.id}
                  className="w-8 h-8 rounded-full bg-[#83BDFF] border-2 border-black flex items-center justify-center"
                  title={user.name}
                >
                  <User className="w-4 h-4 text-black" />
                </div>
              ))}
            </div>
          </div>
        ))}

        {/* Add New Board Button */}
        <div className="flex justify-end mt-6 pt-4">
          <RetroButton
            icon={<Plus className="w-4 h-4" />}
            onClick={addNewBoard}
            className="bg-pink-200 hover:bg-pink-300"
          >
            Add new board
          </RetroButton>
        </div>
      </div>
    </RetroModal>
  );
};
