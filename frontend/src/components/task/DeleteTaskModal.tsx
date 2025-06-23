import { FC } from 'react';
import { RetroModal } from '@components/common/RetroModal';
import { RetroButton } from '@components/common/RetroButton';
import { Trash2 } from "lucide-react";

interface DeleteTaskModalProps {
  onClose: () => void;
  onConfirm: () => void;
  taskTitle: string;
  isDeleting: boolean;
}

export const DeleteTaskModal: FC<DeleteTaskModalProps> = ({ onClose, onConfirm, taskTitle, isDeleting }) => {
  return (
    <RetroModal onClose={onClose} title="Delete Task">
      <div className="flex flex-col gap-4">
        <p className="text-gray-700">
          Are you sure you want to delete the task "{taskTitle}"? This action cannot be undone.
        </p>
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose} variant="secondary" icon={null}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="button" onClick={onConfirm} disabled={isDeleting} icon={<Trash2 className="h-4 w-4" />}>
            {isDeleting ? 'Deleting...' : 'Delete Task'}
          </RetroButton>
        </div>
      </div>
    </RetroModal>
  );
}; 