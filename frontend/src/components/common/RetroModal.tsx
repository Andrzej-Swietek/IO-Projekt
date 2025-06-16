import { FC, ReactNode } from 'react';
import { RetroContainer } from './RetroContainer';

interface RetroModalProps {
  onClose: () => void;
  title: string;
  children: ReactNode;
  subtitle?: string;
}

export const RetroModal: FC<RetroModalProps> = ({ onClose, title, children, subtitle }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <RetroContainer className="w-[500px] p-8">
        <h2 className="text-2xl font-bold text-amber-800 pb-2">{title}</h2>
        {subtitle && <div className="text-gray-600 text-base pb-4">{subtitle}</div>}
        <div className="space-y-6">
          {children}
        </div>
      </RetroContainer>
    </div>
  );
}; 