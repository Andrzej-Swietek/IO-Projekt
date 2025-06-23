import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { ColumnTitle } from '@components/board';
import { Team, TeamControllerApiFactory, TeamMember, UserControllerApiFactory } from '@/api';
import { useQuery } from '@tanstack/react-query';
import { FC, useState } from 'react';
import { Loading } from '@components/common/Loading.tsx';
import { useTranslation } from 'react-i18next';
import { RetroContainer } from '@components/common/RetroContainer.tsx';
import { RetroEntryCard } from '@components/common/RetroEntryCard.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { UserPlus } from 'lucide-react';
import { AddMemberModal } from '@components/team/AddMemberModal.tsx';

const fetchUsersTeams = async (userId: string) => {
  const response = await TeamControllerApiFactory().getTeamsByUserId(userId);
  return response.data;
};

const fetchAllUsers = async () => {
  const response = await UserControllerApiFactory().getAllUsers();
  return response.data;
};

export const Teams: FC = () => {
  const { t } = useTranslation();
  const { profile } = useUserProfile();
  const userId = profile?.id;

  const [showAddMemberModal, setShowAddMemberModal] = useState(false);
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);

  const {
    data: teams,
    isLoading: isTeamsLoading,
  } = useQuery({
    queryKey: ['my-teams', userId],
    queryFn: () => fetchUsersTeams(userId!),
    enabled: !!userId,
  });

  const {
    data: users,
    isLoading: isUsersLoading,
  } = useQuery({
    queryKey: ['all-users'],
    queryFn: fetchAllUsers,
    enabled: true,
  });

  if (isTeamsLoading || isUsersLoading) {
    return <Loading className="h-screen"></Loading>;
  }

  const avatarUrl = (email: string): string =>
    `https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=${encodeURIComponent(email)}`;

  return (
    <>
      <div className="w-full h-[125px] px-32 pt-12 pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] text-[var(--primary-black)]">
          {t('teams.title')}
        </h1>
      </div>
      <div className="grid grid-cols-12 gap-8 min-h-[80vh] grid-rows-[auto_1fr] items-start !p-8">
        <aside className="col-span-4 grid grid-cols-12 gap-y-8 relative">
          <div className="w-1/2 h-full min-h-[70vh] top-22 bg-blue-100 absolute -z-1 retro-shadow"></div>
          <div className="col-span-full h-[60px]">
            <ColumnTitle title={t('home.myTeams')} />
          </div>
          {teams?.map(team => (
            <div key={team.id} className="col-span-full !px-8">
              <RetroEntryCard
                className="col-span-full"
                onClick={() => setSelectedTeam(team)}
                left={team.name}
                right={(
                  <div className="flex flex-col gap-4">
                    <div>{team.description}</div>
                    <div>{team.members?.length ?? 0}</div>
                  </div>
                )}
              />
            </div>
          ))}
        </aside>
        <main className="col-span-8 grid grid-cols-12 gap-y-8">
          <div className="col-span-full !h-[60px]">
            <ColumnTitle title={t('home.users')} />
          </div>
          <RetroContainer shadowSize="lg" className="col-span-full h-full min-h-[40vh] !px-8 !py-4">
            <div className="w-full">
              <div className="text-gray-500 text-sm mb-4">
                {t('teams.addTeamMember')}
              </div>
              <div>
                <RetroButton
                  className="px-4 py-2 cursor-pointer"
                  icon={<UserPlus />}
                  onClick={() => setShowAddMemberModal(true)}
                >
                  {t('board.addMember')}
                </RetroButton>
              </div>
            </div>
            {selectedTeam
              ? (
                <>
                  <div className="mb-6">
                    <h2 className="text-2xl font-bold">{selectedTeam.name}</h2>
                    <div className="text-gray-500 text-sm">
                      Team ID:
                      {selectedTeam.id}
                    </div>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
                    {selectedTeam.members?.map((teamMember: TeamMember) => {
                      const user = users?.find(u => u.id === teamMember.userId);
                      if (!user) return null;
                      return (
                        <div key={user.id} className="bg-white p-4 flex items-center gap-4 retro-shadow">
                          <img
                            src={avatarUrl(user.email || '')}
                            alt={user.email}
                            className="w-14 h-14 rounded-full border-2 border-gray-200"
                          />
                          <div>
                            <div className="font-semibold text-lg">
                              {user.firstName}
                              {' '}
                              {user.lastName}
                            </div>
                            <div className="text-xs text-gray-500">
                              UUID:
                              {user.id}
                            </div>
                            <div className="text-xs text-gray-400">{user.email}</div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </>
              )
              : (
                <div className="text-gray-400 text-center">Select a team to see its members.</div>
              )}
          </RetroContainer>
        </main>
      </div>
      {showAddMemberModal && selectedTeam && (
        <AddMemberModal
          teamId={Number(selectedTeam.id)}
          onClose={() => setShowAddMemberModal(false)}
        />
      )}
    </>
  );
};
