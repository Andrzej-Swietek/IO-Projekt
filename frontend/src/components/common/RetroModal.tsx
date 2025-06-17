import { FC, ReactNode, useEffect, useState } from 'react';
import { X } from 'lucide-react';
import { cn } from '@/lib/utils';
import { RetroContainer } from '@components/common/RetroContainer.tsx';
import { ColumnTitle } from '@components/board';

interface RetroModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children?: ReactNode;
  className?: string;
}

export const RetroModal: FC<RetroModalProps> = ({ isOpen, onClose, title, children, className }) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsVisible(true);
      document.body.style.overflow = 'hidden';
    } else {
      const timer = setTimeout(() => {
        setIsVisible(false);
      }, 300);
      document.body.style.overflow = '';
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  if (!isVisible) return null;

  return (
    <div
      className={cn(
        'fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/30 backdrop-blur-sm transition-opacity',
        isOpen ? 'opacity-100' : 'opacity-0',
      )}
      onClick={onClose}
    >
      <div
        className={cn('w-full max-w-2xl transition-all', isOpen ? 'scale-100' : 'scale-95', className)}
        onClick={e => e.stopPropagation()}
      >
        <div className="grid grid-rows-[auto_1fr] gap-0">
          {/* Modal Header */}
          <ColumnTitle title={title} />

          {/* Modal Content */}
          <RetroContainer className="min-h-[300px] relative">
            <button
              className="absolute right-2 top-2 w-8 h-8 flex items-center justify-center hover:bg-yellow-200 rounded-full transition-colors"
              onClick={onClose}
            >
              <X className="w-4 h-4" />
            </button>
            {children}
          </RetroContainer>
        </div>
      </div>
    </div>
  );
};
