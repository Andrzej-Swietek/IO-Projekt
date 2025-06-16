import { FC, ChangeEvent } from 'react';

interface Option {
  value: string;
  label: string;
}

interface RetroSelectProps {
  label: string;
  value: string;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
  options: Option[];
  required?: boolean;
}

export const RetroSelect: FC<RetroSelectProps> = ({ label, value, onChange, options, required = false }) => {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-sm font-medium text-amber-800">{label}</label>
      <select
        value={value}
        onChange={onChange}
        required={required}
        className="rounded border border-amber-300 bg-amber-50 px-3 py-2 text-amber-900 focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500"
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}; 