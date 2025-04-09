import { Providers } from '@/context';
import { Toaster } from '@components/ui/sonner.tsx';
import { ReactElement, ReactNode } from 'react';

export const MainLayout = ({ children }: { children: ReactNode }): ReactElement => {
  return (
    <Providers>
      {children}
      <Toaster />
    </Providers>
  );
};
