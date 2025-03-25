import { createContext, FC, ReactNode, useContext, useEffect, useState } from 'react';
import Keycloak from 'keycloak-js';

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
  const [authenticated, setAuthenticated] = useState<boolean>(false);

  useEffect(() => {
    const keycloakInstance = new Keycloak({
      url: import.meta.env.VITE_KEYCLOAK_URL,
      realm: import.meta.env.VITE_KEYCLOAK_REALM,
      clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
    });

    keycloakInstance.init({ onLoad: 'check-sso' }).then((successfullyAuthenticated: boolean) => {
      setKeycloak(keycloakInstance);
      setAuthenticated(successfullyAuthenticated);
    }).catch(error => {
      // eslint-disable-next-line
      console.error('Keycloak initialization error:', error);
    });
  }, []);

  return (
    <KeycloakContext.Provider value={{ keycloak, authenticated }}>
      {children}
    </KeycloakContext.Provider>
  );
};


export const useKeycloak = (): KeycloakContextType => {
  const context = useContext(KeycloakContext);
  if (!context) {
    throw new Error('useKeycloak must be used within a KeycloakProvider');
  }
  return context;
};
