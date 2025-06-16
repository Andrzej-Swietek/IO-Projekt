import { FC, ReactNode } from 'react';
import { RetroContainer } from './RetroContainer';

interface RetroModalProps {
  onClose: () => void;
  title: string;
  children: ReactNode;
}

export const RetroModal: FC<RetroModalProps> = ({ onClose, title, children }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <RetroContainer className="w-[500px] p-8">
        <h2 className="mb-6 text-2xl font-bold text-amber-800">{title}</h2>
        {children}
      </RetroContainer>
    </div>
  );
}; 