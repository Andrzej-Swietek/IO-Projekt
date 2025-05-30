import { FC } from 'react';
import { Monitor } from 'lucide-react';

export const Footer: FC = () => (
  <footer className="bg-black text-white !py-4 h-[80px] flex items-center justify-between !px-16">
    {/* Left - Brand */}
    <div className="flex flex-col">
      <span className="text-sm font-semibold">retroBoard</span>
      <span className="text-xs text-gray-400">Keep it simple. Keep it retro.</span>
    </div>

    {/* Center - Copyright */}
    <div className="text-xs text-gray-400">© 2025 retroBoard All Rights Reserved.</div>

    {/* Right - Icon */}
    <div className="w-8 h-8 bg-white rounded flex items-center justify-center">
      <Monitor className="w-5 h-5 text-black" />
    </div>
  </footer>
);
