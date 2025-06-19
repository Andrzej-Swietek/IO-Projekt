
import type { ButtonHTMLAttributes } from 'react';
import { FC, ReactNode } from 'react';
import { Plus } from 'lucide-react';
import { cn } from '@/lib/utils';

interface RetroButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: 'primary' | 'secondary';
  icon?: ReactNode;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

export const RetroButton: FC<RetroButtonProps> = ({
  children,
  variant = 'primary',
  icon = <Plus className="w-4 h-4" />,
  className,
  size = 'md',
  ...props
}) => {
  // Responsive and size-based padding/font-size
  const sizeClasses = {
    sm: 'px-4 py-2 text-sm md:px-6 md:py-3 md:text-base',
    md: 'px-8 py-3 text-base md:px-12 md:py-4 md:text-md',
    lg: 'px-12 py-4 text-lg md:px-16 md:py-6 md:text-lg',
  };

  return (
    <button
      className={cn(
        "relative flex items-center gap-2 font-['Josefin_Sans',_sans-serif] font-black shadow-[4px_4px_0px_#000000] "
        + 'transition-all hover:translate-y-[1px] hover:translate-x-[1px] hover:shadow-[3px_3px_0px_#000000] active:translate-y-[2px] active:translate-x-[2px] active:shadow-[2px_2px_0px_#000000] cursor-pointer',
        sizeClasses[size],
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
