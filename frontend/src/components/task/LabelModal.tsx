import { ChangeEvent, FC, FormEvent, useEffect, useState } from 'react';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroColorPicker } from '@components/common/RetroColorPicker';
import { RetroButton } from '@components/common/RetroButton';

interface LabelModalProps {
  open: boolean;
  onClose: () => void;
  onSave: (data: { name: string; color: string }) => void;
  initialName?: string;
  initialColor?: string;
  isEdit?: boolean;
  loading?: boolean;
}

export const LabelModal: FC<LabelModalProps> = ({
  open,
  onClose,
  onSave,
  initialName = '',
  initialColor = '#000000',
  isEdit = false,
  loading = false,
}) => {
  const [name, setName] = useState(initialName);
  const [color, setColor] = useState(initialColor);

  useEffect(() => {
    setName(initialName);
    setColor(initialColor);
  }, [initialName, initialColor, open]);

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    onSave({ name, color });
  };

  return (
    <>
      {open && (
        <RetroModal open={open} onClose={onClose} title={isEdit ? 'Edit Label' : 'Add Label'}>
          <form onSubmit={handleSubmit} className="space-y-4">
            <RetroInput
              label="Label Name"
              inputColor="bg-yellow-50"
              value={name}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
              required
            />
            <RetroColorPicker
              label="Color"
              value={color}
              onChange={e => setColor(e.target.value)}
            />
            <div className="flex gap-2 justify-end">
              <RetroButton
                variant="secondary"
                size="sm"
                type="button"
                onClick={onClose}
                disabled={loading}
                icon={null}
              >
                Cancel
              </RetroButton>
              <RetroButton
                variant="secondary"
                size="sm"
                type="submit"
                disabled={loading || !name}
              >
                {loading ? (isEdit ? 'Saving...' : 'Adding...') : (isEdit ? 'Save' : 'Add')}
              </RetroButton>
            </div>
          </form>
        </RetroModal>
      )}
    </>

  );
};
