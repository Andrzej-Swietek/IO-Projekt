import { ChangeEvent, FC, FormEvent, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { BoardControllerApiFactory, BoardRequest } from '@/api';
import { useUserProfile } from '@context/UserProfileProvider';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';
import { toast } from 'sonner';

interface CreateBoardModalProps {
  onClose: () => void;
  projectId: number;
}

export const CreateBoardModal: FC<CreateBoardModalProps> = ({ onClose, projectId }) => {
  const { profile } = useUserProfile();
  const queryClient = useQueryClient();
  const [boardName, setBoardName] = useState('');
  const [description, setDescription] = useState('');

  const createBoardMutation = useMutation({
    mutationFn: async () => {
      const boardRequest: BoardRequest = {
        name: boardName,
        description: description,
        projectId: projectId,
        ownerId: profile?.id,
      };
      const response = await BoardControllerApiFactory().createBoard(boardRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['project-boards', projectId] });
      onClose();
    },
    onError: error => {
      toast.error('Error creating board. Please try again. ' + error.message);
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!boardName.trim()) {
      return;
    }
    createBoardMutation.mutate();
  };

  return (
    <RetroModal onClose={onClose} title="Create New Board">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="Board Name"
          inputColor="bg-yellow-50"
          value={boardName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setBoardName(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          inputColor="bg-yellow-50"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose} variant="secondary" icon={null}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={createBoardMutation.isPending}>
            {createBoardMutation.isPending ? 'Creating...' : 'Create Board'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
};
