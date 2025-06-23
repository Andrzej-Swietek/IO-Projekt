import { FC, InputHTMLAttributes } from 'react';
// Import cn utility (adjust the path as needed)
import { cn } from '@/lib/utils';

interface RetroInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  inputColor?: string;
}

export const RetroInput: FC<RetroInputProps> = ({ label, inputColor, ...props }) => {
  return (
    <div className={props.className}>
      <label htmlFor={props.id} className="mb-1 block font-bold text-md uppercase tracking-wider text-black">
        {label}
      </label>
      <input
        {...props}
        style={inputColor ? { backgroundColor: inputColor, ...(props.style || {}) } : props.style}
        className={cn(
          'w-full border retro-shadow border-gray-300 px-3 py-4 '
          + 'focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500',
          props.className,
          inputColor ? inputColor : '',
        )}
      />
    </div>
  );
};
