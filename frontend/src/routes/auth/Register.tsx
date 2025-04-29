import { FC, useEffect } from 'react';
import { useKeycloak } from '@/context';

export const Register: FC = () => {
  const { keycloak } = useKeycloak();

  useEffect(() => {
    if (keycloak) {
      keycloak.register({
        redirectUri: window.location.href,
      });
    }
  }, [keycloak]);

  return <div>Sign Up...</div>;
};
