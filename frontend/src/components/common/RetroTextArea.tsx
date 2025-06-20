import { FC, TextareaHTMLAttributes } from 'react';

interface RetroTextAreaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  required?: boolean;
}

export const RetroTextArea: FC<RetroTextAreaProps> = ({
  label,
  required = false,
  className,
  ...props
}) => (
  <div className="flex flex-col gap-1">
    <label className="font-bold text-md mb-1 uppercase tracking-wider text-black">{label}</label>
    <textarea
      required={required}
      className={`retro-input border-2 border-black bg-yellow-100 retro-shadow px-4 py-2
       font-mono text-base focus:outline-none focus:ring-2 focus:ring-pink-400 transition-all resize-none ${className ?? ''}`}
      {...props}
    />
  </div>
);
