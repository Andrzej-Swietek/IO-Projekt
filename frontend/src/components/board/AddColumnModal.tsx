import { ChangeEvent, FC, FormEvent, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { BoardColumnControllerApi, BoardColumnRequest } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';
import { toast } from 'sonner';

interface CreateColumnModalProps {
  onClose?: () => void;
  boardId: number;
  position: number;
}

export const AddColumnModal: FC<CreateColumnModalProps> = ({ onClose, boardId, position }) => {
  const queryClient = useQueryClient();
  const [columnName, setColumnName] = useState('');
  const [description, setDescription] = useState('');

  const createColumnMutation = useMutation({
    mutationFn: async () => {
      const columnRequest: BoardColumnRequest = {
        name: columnName,
        boardId: boardId,
        position: position,
      };
      const response = await new BoardColumnControllerApi().createColumn(columnRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board-columns', boardId] });
      if (onClose) {
        onClose();
      }
    },
    onError: error => {
      toast.error('Error creating column. Please try again. ' + error.message);
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!columnName.trim()) {
      return;
    }
    createColumnMutation.mutate();
  };

  return (
    <RetroModal onClose={() => onClose ? onClose() : {}} title="Create New Column">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="Column Name"
          inputColor="bg-yellow-50"
          value={columnName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setColumnName(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          inputColor="bg-yellow-50"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
        />
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={createColumnMutation.isPending}>
            {createColumnMutation.isPending ? 'Creating...' : 'Create Column'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
};
