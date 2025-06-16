import { FC, useState, FormEvent, ChangeEvent } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { BoardControllerApiFactory, BoardRequest } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';

interface CreateBoardModalProps {
  onClose: () => void;
  projectId: number;
}

export const CreateBoardModal: FC<CreateBoardModalProps> = ({ onClose, projectId }) => {
  const queryClient = useQueryClient();
  const [boardName, setBoardName] = useState('');
  const [description, setDescription] = useState('');

  const createBoardMutation = useMutation({
    mutationFn: async () => {
      const boardRequest: BoardRequest = {
        name: boardName,
        description: description,
        projectId: projectId,
      };
      const response = await BoardControllerApiFactory().createBoard(boardRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team-boards', projectId] });
      onClose();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    createBoardMutation.mutate();
  };

  return (
    <RetroModal onClose={onClose} title="Create New Board">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="Board Name"
          value={boardName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setBoardName(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton type="button" onClick={onClose}>
            Cancel
          </RetroButton>
          <RetroButton type="submit" disabled={createBoardMutation.isPending}>
            {createBoardMutation.isPending ? 'Creating...' : 'Create Board'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
}; 