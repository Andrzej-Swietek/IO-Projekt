import { FC } from 'react';
import { RetroModal } from '@components/common/RetroModal.tsx';
import { CancelButton } from '@components/common/CancelButton.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { X } from 'lucide-react';

export const ConfirmModal: FC<{
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  children?: React.ReactNode;
}> = ({ open, onClose, onConfirm, children }) => (
  <>
    {open && (
      <RetroModal open={open}>
        <div className="flex flex-col gap-6 items-center p-6">
          <div
            className="font-black text-2xl leading-[100%] tracking-[0%] align-bottom text-[var(--primary-black)]"
          >
            {children}
          </div>
          <div className="flex gap-4">
            <CancelButton
              onClick={onClose}
            />
            <RetroButton
              icon={<X className="w-4 h-4" />}
              variant="danger"
              onClick={onConfirm}
            >
              Delete
            </RetroButton>
          </div>
        </div>
      </RetroModal>
    )}
  </>
);
