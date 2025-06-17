import { FC, useEffect, useRef, useState } from 'react';
import { cn } from '@/lib/utils';


interface Option {
  label: string;
  value: string | number;
}

interface RetroMultiSelectProps {
  options: Option[];
  value: Array<string | number>;
  onChange: (selected: Array<string | number>) => void;
  placeholder?: string;
  label?: string;
  className?: string;
}

export const RetroMultiSelect: FC<RetroMultiSelectProps> = ({
  options,
  value,
  onChange,
  placeholder = 'Select...',
  label,
  className,
}) => {
  const [search, setSearch] = useState('');
  const [open, setOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const filtered = options.filter(
    o =>
      o.label.toLowerCase().includes(search.toLowerCase())
      && !value.includes(o.value),
  );

  useEffect(() => {
    if (open) inputRef.current?.focus();
  }, [open]);

  return (
    <div
      className={cn('relative w-auto font-mono', className)}
    >
      {label && <label className="mb-3 px-2 block font-bold text-xs">{label}</label>}
      <div
        className={cn(
          'bg-white p-4 pixelated shadow-[4px_4px_0px_black] '
          + 'flex flex-wrap items-center gap-2 min-h-[2.5rem] cursor-pointer retro-shadow',
          open && 'outline outline-4 outline-black',
        )}

        onClick={() => setOpen(v => !v)}
        tabIndex={0}
        onBlur={e => {
          if (!e.relatedTarget?.closest('.retro-multiselect')) {
            setOpen(false);
          }
        }}
      >
        {value.length === 0 && (
          <span className="text-gray-500">{placeholder}</span>
        )}

        {value.map(val => {
          const opt = options.find(o => o.value === val);
          return (
            <span
              key={val}
              className="bg-[#ffcc5b] border-2 border-black px-2 py-1 mr-1 mb-1 flex items-center gap-1 shadow-[2px_2px_0px_black]"
            >
              {opt?.label}
              <button
                className="ml-1 text-black font-bold"
                onClick={e => {
                  e.stopPropagation();
                  onChange(value.filter(v => v !== val));
                }}
                type="button"
                tabIndex={-1}
              >
                ×
              </button>
            </span>
          );
        })}
        <input
          ref={inputRef}
          className="flex-1 bg-transparent outline-none border-none p-0 m-0 min-w-[60px] font-mono"
          value={search}
          onChange={e => setSearch(e.target.value)}
          onFocus={() => setOpen(true)}
          placeholder={value.length === 0 ? '' : ''}
        />
      </div>

      {open && filtered.length > 0 && (
        <div
          className="absolute z-10 mt-1 w-full bg-[#fffdcc] border-4
          border-black shadow-[4px_4px_0px_black] max-h-48 overflow-auto font-mono pixelated"
        >
          {filtered.map(opt => (
            <div
              key={opt.value}
              className="px-4 py-2 hover:bg-[#ffcc5b] cursor-pointer border-b-2 border-black last:border-none"
              onMouseDown={e => {
                e.preventDefault();
                onChange([...value, opt.value]);
                setSearch('');
                inputRef.current?.focus();
              }}
            >
              {opt.label}
            </div>
          ))}
        </div>
      )}

      {/* Small pixelated style to add a "retro" feel */}
      <style>
        {`
        .pixelated {
          image-rendering: pixelated;
        }
      `}
      </style>
    </div>
  );
};

