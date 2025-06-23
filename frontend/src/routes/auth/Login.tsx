import { FC, useEffect } from 'react';
import { useKeycloak } from '../../context/KeycloakProvider.tsx';
import { Navigate } from 'react-router';

export const Login: FC = () => {
  const { keycloak, authenticated } = useKeycloak();

  useEffect(() => {
    if (keycloak && !authenticated) {
      console.log('Attempting to log in with Keycloak...');
      console.log(authenticated);
      keycloak.login({
        redirectUri: window.location.href,
      });
    }
  }, [keycloak, authenticated]);

  if (authenticated) {
    return <Navigate to="/" replace />;
  }

  return <div>Logging in...</div>;
};
