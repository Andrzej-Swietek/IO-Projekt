import { Providers } from '@/context';
import { Toaster } from '@components/ui/sonner.tsx';
import { ReactElement, ReactNode } from 'react';
import { Footer } from '@components/common/Footer.tsx';
import { RetroNavbar } from '@components/common/RetroNavbar.tsx';

export const MainLayout = ({ children }: { children: ReactNode }): ReactElement => {
  return (
    <Providers>
      <RetroNavbar />
      {children}
      <Toaster />
      <Footer />
    </Providers>
  );
};
