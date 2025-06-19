import type { ButtonHTMLAttributes } from 'react';
import { FC, ReactNode } from 'react';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { X } from 'lucide-react';

interface CancelRetroButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children?: ReactNode;
  variant?: 'primary' | 'secondary';
  className?: string;
  iconSize?: number | string;
  size?: 'sm' | 'md' | 'lg';
  onClick?: () => void;
}

export const CancelButton: FC<CancelRetroButtonProps> = ({
  iconSize = '4',
  size = 'md',
  onClick,
  className = '',
  children,
  variant = 'primary',
  ...props
}) => (
  <RetroButton
    icon={<X className={`w-${iconSize} h-${iconSize}`} />}
    size={size}
    type="button"
    variant={variant}
    onClick={onClick}
    {...props}
  >
    Cancel
    {children}
  </RetroButton>
);
