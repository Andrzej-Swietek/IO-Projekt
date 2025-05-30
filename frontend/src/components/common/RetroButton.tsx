import { FC, ReactNode } from 'react';
// eslint-disable-next-line no-duplicate-imports
import type { ButtonHTMLAttributes } from 'react';
import { Plus } from 'lucide-react';
import { cn } from '@/lib/utils';

interface RetroButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: 'primary' | 'secondary';
  icon?: ReactNode;
  className?: string;
}

export const RetroButton: FC<RetroButtonProps> = ({
  children,
  variant = 'primary',
  icon = <Plus className="w-4 h-4" />,
  className,
  ...props
}) => {
  return (
    <button
      className={cn(
        "relative px-12 py-4 flex items-center gap-2 font-['Josefin_Sans',_sans-serif] font-black text-md shadow-[4px_4px_0px_#000000]"
        + ' transition-all hover:translate-y-[1px] hover:translate-x-[1px] hover:shadow-[3px_3px_0px_#000000] '
        + 'active:translate-y-[2px] active:translate-x-[2px] active:shadow-[2px_2px_0px_#000000] cursor-pointer',
        variant === 'primary'
          ? 'bg-pink-200 hover:bg-pink-300 text-black'
          : 'bg-yellow-200 hover:bg-yellow-300 text-black',
        className,
      )}
      {...props}
    >
      {icon}
      {children}
    </button>
  );
};
