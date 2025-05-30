import { useQuery, UseQueryResult } from '@tanstack/react-query';
import { UserControllerApiFactory, UserRepresentation } from '@/api';

const api = UserControllerApiFactory();

export const useUsersByIds = (userIds: string[] = []): UseQueryResult<Record<string, UserRepresentation>, unknown> => {
  const uniqueIds = [...new Set(userIds)].sort();
  const STALE_TIME = 5 * 60 * 1000;

  return useQuery({
    queryKey: ['users', uniqueIds],
    queryFn: async (): Promise<Record<string, UserRepresentation>> => {
      if (!uniqueIds.length) return {};
      const response = await api.populateUsers({ userIds: uniqueIds });
      return response.data.reduce((acc, user) => {
        acc[user.id!] = user;
        return acc;
      }, {} as Record<string, UserRepresentation>);
    },
    staleTime: STALE_TIME,
  });
};
