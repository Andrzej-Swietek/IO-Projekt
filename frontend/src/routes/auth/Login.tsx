import { FC, useEffect } from 'react';
import { useKeycloak } from '../../context/KeycloakProvider.tsx';
import { Navigate } from 'react-router';

export const Login: FC = () => {
  const { keycloak, authenticated } = useKeycloak();

  useEffect(() => {
    if (keycloak) {
      keycloak.login({
        redirectUri: window.location.href,
      });
    }
  }, [keycloak]);

  if (authenticated) {
    return <Navigate to="/" replace />;
  }

  return <div>Logging in...</div>;
};
