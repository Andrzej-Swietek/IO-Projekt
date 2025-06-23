import { FC, InputHTMLAttributes } from 'react';
import { cn } from '@/lib/utils';

interface RetroColorPickerProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
}

export const RetroColorPicker: FC<RetroColorPickerProps> = ({ label, ...props }) => {
    return (
        <div className={props.className}>
            <label htmlFor={props.id} className="mb-1 block font-bold text-md uppercase tracking-wider text-black">
                {label}
            </label>
            <input
                type="color"
                {...props}
                className={cn(
                    'w-12 h-12 border-2 border-black rounded retro-shadow cursor-pointer',
                    props.className
                )}
            />
        </div>
    );
};
