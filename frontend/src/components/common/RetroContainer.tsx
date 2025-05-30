import { FC, ReactNode } from 'react';
import { cn } from '@/lib/utils';

interface RetroContainerProps {
  children: ReactNode;
  className?: string;
  shadowSize?: 'sm' | 'md' | 'lg';
  bgColor?: string;
}

export const RetroContainer: FC<RetroContainerProps> = ({
  children,
  className,
  shadowSize = 'md',
  bgColor = 'bg-yellow-100',
}: RetroContainerProps) => {
  const shadowStyles = {
    sm: 'shadow-[4px_4px_0px_#000000]',
    md: 'shadow-[6px_6px_0px_#000000]',
    lg: 'shadow-[8px_8px_0px_#000000]',
  };

  return (
    <div className={cn('relative p-6 border border-black', bgColor, shadowStyles[shadowSize], className)}>
      {children}
    </div>
  );
};
