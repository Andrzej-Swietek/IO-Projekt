import { createContext, FC, ReactNode, useContext, useEffect, useRef, useState } from 'react';
import Keycloak from 'keycloak-js';
import { toast } from 'sonner';

interface KeycloakContextType {
  keycloak: Keycloak | null;
  authenticated: boolean;
}

interface KeycloakProviderProps {
  children: ReactNode;
}

const KeycloakContext = createContext<KeycloakContextType | undefined>(undefined);

export const KeycloakProvider: FC<KeycloakProviderProps> = ({ children }) => {
  const [keycloak, setKeycloak] = useState<Keycloak | null>(null);
  const [authenticated, setAuthenticated] = useState(false);
  const initializedRef = useRef(false);

  useEffect(() => {
    if (initializedRef.current) return;

    initializedRef.current = true;

    const keycloakInstance = new Keycloak({
      url: import.meta.env.VITE_KEYCLOAK_URL,
      realm: import.meta.env.VITE_KEYCLOAK_REALM,
      clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
    });

    keycloakInstance
      .init({ onLoad: 'check-sso' })
      .then((isAuthenticated: boolean) => {
        setKeycloak(keycloakInstance);
        setAuthenticated(isAuthenticated);
      })
      .catch(err => {
        toast.error('Error during Keycloak initialization. Please try again later.');
        // eslint-disable-next-line
                console.error('Keycloak init error', err);
      });
  }, []);

  return (
    <KeycloakContext.Provider value={{ keycloak, authenticated }}>
      {children}
    </KeycloakContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useKeycloak = (): KeycloakContextType => {
  const context = useContext<KeycloakContextType | undefined>(KeycloakContext);
  if (!context) {
    throw new Error('useKeycloak must be used within a KeycloakProvider');
  }
  return context;
};
