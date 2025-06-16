import { FC, InputHTMLAttributes } from 'react';

interface RetroInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
}

export const RetroInput: FC<RetroInputProps> = ({ label, ...props }) => {
  return (
    <div>
      <label htmlFor={props.id} className="mb-2 block text-sm font-medium text-gray-700">
        {label}
      </label>
      <input
        {...props}
        className="w-full rounded-md border border-gray-300 px-3 py-2 focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500"
      />
    </div>
  );
}; 