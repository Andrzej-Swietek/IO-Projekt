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

  const refreshToken = async () => {
    try {
      await keycloak.updateToken(60 * 5); // Refresh token every 60 seconds
    } catch {
      keycloak.login();
    }
  };

  const syncToken = () => {
    if (keycloak.token) {
      localStorage.setItem('kanban_app_token', keycloak.token);
    } else {
      localStorage.removeItem('kanban_app_token');
    }
  };

  useEffect(() => {
    const fetchProfile = async () => {
      if (!keycloak?.authenticated || !keycloak.token) return;

      try {
        const api = UserControllerApiFactory();
        const response = await api.getUserDetails(keycloak?.tokenParsed?.sub ?? '');
        localStorage.setItem('kanban_app_token', keycloak.token);
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
    if (!keycloak) return;
    const interval = setInterval(() => {
      keycloak.updateToken(60).catch(() => {
        keycloak.login();
      });
    }, 60000);
    fetchProfile();
    keycloak.onTokenExpired = () => {
      localStorage.removeItem('kanban_app_token');
      keycloak.updateToken();
      // Keycloak.logout();
    };

    keycloak.onTokenExpired = async () => {
      localStorage.removeItem('kanban_app_token');
      try {
        await keycloak.updateToken();
      } catch {
        keycloak.login();
      }
    };

    keycloak.onAuthRefreshSuccess = syncToken;

    keycloak.onAuthLogout = () => {
      localStorage.removeItem('kanban_app_token');
    };
    return () => clearInterval(interval);
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
