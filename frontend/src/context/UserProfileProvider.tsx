import { UserControllerApiFactory, UserRepresentation } from '@/api';
import { createContext, FC, ReactNode, useContext, useEffect, useState } from 'react';
import { useKeycloak } from '@context/KeycloakProvider.tsx';


interface UserProfileContextType {
  profile: UserRepresentation | null;
  loading: boolean;
  error: string | null;
}

export const UserProfileContext = createContext<UserProfileContextType>({
  profile: null,
  loading: true,
  error: null,
});


interface UserProfileProviderProps {
  children: ReactNode;
}

export const UserProfileProvider: FC<UserProfileProviderProps> = ({ children }) => {
  const { keycloak } = useKeycloak();
  const [profile, setProfile] = useState<UserRepresentation | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      if (!keycloak?.authenticated || !keycloak.token) return;

      try {
        const api = UserControllerApiFactory();
        const response = await api.getUserDetails(keycloak?.tokenParsed?.sub ?? '');
        localStorage.setItem('token', keycloak.token);
        setProfile(response.data);
        setError(null);
      } catch (err) {
        // eslint-disable-next-line
                console.error('Failed to fetch user profile', err);
        setError('Nie udało się pobrać profilu');
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [keycloak]);

  return (
      <UserProfileContext.Provider value={{ profile, loading, error }}>
        {children}
      </UserProfileContext.Provider>
  );
};

export const useUserProfile = (): UserProfileContextType => {
  return useContext(UserProfileContext);
};