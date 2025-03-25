import { FC, useEffect } from 'react';
import { useKeycloak } from '../../context/KeycloakProvider.tsx';

export const Login: FC = () => {
  const { keycloak } = useKeycloak();

  useEffect(() => {
    if (keycloak) {
      keycloak.login({
        redirectUri: window.location.href,
      });
    }
  }, [keycloak]);

  return <div>Logging in...</div>;
};
