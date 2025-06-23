import { ChangeEvent, FC } from 'react';

interface Option {
  value: string;
  label: string;
}

interface RetroSelectProps {
  label: string;
  value: string | number;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
  options: Option[];
  required?: boolean;
}

export const RetroSelect: FC<RetroSelectProps> = ({
  label,
  value,
  onChange,
  options,
  required = false,
}) => (
  <div className="flex flex-col gap-1">
    <label className="font-bold text-md mb-1 uppercase tracking-wider text-black">{label}</label>
    <select
      value={value}
      onChange={onChange}
      required={required}
      className="retro-input border-2 border-black bg-yellow-100 retro-shadow px-4 py-4 font-mono
       text-base focus:outline-none focus:ring-2 focus:ring-pink-400 transition-all"
    >
      {options?.map(option => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </select>
  </div>
);
