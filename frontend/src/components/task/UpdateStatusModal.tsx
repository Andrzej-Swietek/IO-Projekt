import { FC, useState } from 'react';
import { RetroModal } from '@components/common/RetroModal';
import { RetroSelect } from '@components/common/RetroSelect';
import { RetroButton } from '@components/common/RetroButton';
import { X } from 'lucide-react';

interface UpdateStatusModalProps {
  open: boolean;
  onClose: () => void;
  currentStatus: string;
  onUpdate: (status: string) => void;
}

const statuses = [
  { label: 'To Do', value: 'TODO' },
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Done', value: 'DONE' },
  { label: 'Blocked', value: 'BLOCKED' },
];

export const UpdateStatusModal: FC<UpdateStatusModalProps> = ({
  open,
  onClose,
  currentStatus,
  onUpdate,
}) => {
  const [selectedStatus, setSelectedStatus] = useState(currentStatus);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedStatus !== currentStatus) {
      onUpdate(selectedStatus);
    }
    onClose();
  };

  return open
    ? (
      <RetroModal onClose={onClose} title="Update Task Status">
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <RetroSelect
            label="Status"
            options={statuses}
            value={selectedStatus}
            onChange={val => setSelectedStatus(val.target.value)}
          />
          <div className="flex justify-end gap-4 mt-4">
            <RetroButton size="sm" type="button" onClick={onClose} icon={<X className="w-4 h-4" />}>
              Cancel
            </RetroButton>
            <RetroButton
              size="sm"
              type="submit"
              disabled={selectedStatus === currentStatus}
            >
              Update
            </RetroButton>
          </div>
        </form>
      </RetroModal>
    )
    : null;
};
