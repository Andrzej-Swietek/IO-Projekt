import { FC } from 'react';

interface ColumnTitleProps {
  title: string;
  className?: string;
}

export const ColumnTitle: FC<ColumnTitleProps> = ({ title, className = '' }) => {
  return (
    <div className="relative w-full">
      <div className={`relative w-full h-[60px] bg-[#83BDFF] shadow-[8px_8px_0px_#000000] ${className}`}>
        <h2 className="absolute left-[29px] bottom-[10px] font-['Josefin_Sans',_sans-serif]
         font-normal text-[24px] leading-[24px] text-black"
        >
          {title}
        </h2>
      </div>
    </div>
  );
};
