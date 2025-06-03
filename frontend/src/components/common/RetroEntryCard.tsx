import { FC, HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/utils';

interface RetroEntryCardProps extends HTMLAttributes<HTMLDivElement> {
  left: ReactNode;
  right: ReactNode;
  height?: string;
  shadowSize?: string;
}

export const RetroEntryCard: FC<RetroEntryCardProps> = ({
  left,
  right,
  height = 'h-[10vh]',
  shadowSize = 'retro-shadow',
  className,
  ...rest
}) => {
  return (
    <div
      className={cn(
        'bg-[var(--primary-white)] flex justify-between items-center',
        height,
        shadowSize,
        className,
      )}
      {...rest}
    >
      <div className="w-auto !px-16">{left}</div>
      <div className="w-auto !px-8">{right}</div>
    </div>
  );
};
