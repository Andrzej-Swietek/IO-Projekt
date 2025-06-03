import { FC, ReactNode } from 'react';
import { KeycloakProvider } from '@context/KeycloakProvider.tsx';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { UserProfileProvider } from '@context/UserProfileProvider.tsx';

interface ProviderProps {
  children: ReactNode;
}

const queryClient = new QueryClient();

export const Providers: FC<ProviderProps> = ({ children }) => {
  return (
    <QueryClientProvider client={queryClient}>
      <KeycloakProvider>
        <UserProfileProvider>
          {children}
        </UserProfileProvider>
      </KeycloakProvider>
    </QueryClientProvider>
  );
};
