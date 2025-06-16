import { FC, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Task, TaskControllerApiFactory, Team, TeamControllerApiFactory } from '@/api';
import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { ColumnTitle } from '@components/board';
import { RetroContainer } from '@components/common/RetroContainer.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { FolderKanban } from 'lucide-react';
import { RetroEntryCard } from '@components/common/RetroEntryCard.tsx';
import { TaskCard } from '@components/task';
import { TeamProjectsModal } from '@components/team/TeamProjectsModal';
import { CreateTeamModal } from '@components/team/CreateTeamModal';

interface HomeProps {
}

const fetchTeams = async () => {
  const response = await TeamControllerApiFactory().getAllTeams({});
  return response.data;
};

const fetchUsersTeams = async (userId: string) => {
  const response = await TeamControllerApiFactory().getTeamsByUserId(userId);
  return response.data;
};

const fetchUserTasks = async (userId: string) => {
  const response = await TaskControllerApiFactory().getTasksByUserId(userId);
  return response.data;
};

export const Home: FC<HomeProps> = () => {
  const { profile } = useUserProfile();
  const userId = profile?.id;
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);
  const [showCreateTeamModal, setShowCreateTeamModal] = useState(false);

  const { data, error, isLoading, isError } = useQuery({
    queryKey: ['teams'],
    queryFn: fetchTeams,
  });

  const {
    data: teams,
    isLoading: isTeamsLoading,
  } = useQuery({
    queryKey: ['my-teams', userId],
    queryFn: () => fetchUsersTeams(userId!),
    enabled: !!userId,
  });

  const {
    data: tasks,
    isLoading: isTasksLoading,
  } = useQuery({
    queryKey: ['my-tasks', userId],
    queryFn: () => fetchUserTasks(userId!),
    enabled: !!userId,
  });

  if (isError) {
    return <div>Error loading teams</div>;
  }

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <div className="w-full h-[125px] !px-32 !pt-12 !pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] leading-[100%] tracking-[0%] align-bottom text-[var(--primary-black)]">
          Hello
          {' '}
          {profile?.firstName}
          {' '}
          {profile?.lastName}
        </h1>
      </div>
      <div className="grid grid-cols-12 gap-8 min-h-[80vh] grid-rows-[auto_1fr] items-start !p-8">
        <main className="col-span-8 grid grid-cols-12 gap-y-8">
          <div className="col-span-full !h-[60px]">
            <ColumnTitle title="My Teams" />
          </div>
          <RetroContainer shadowSize="lg" className="col-span-full h-full min-h-[40vh] !px-8 !py-4">
            {
              isTeamsLoading && (
                <div>Loading Teams ... </div>
              )
            }
            {teams && teams.map(team => (
              <RetroEntryCard
                key={team.id}
                left={<>{team.name}</>}
                right={(
                  <RetroButton
                    className="!px-4 !py-4 w-auto"
                    icon={<FolderKanban />}
                    onClick={() => setSelectedTeam(team)}
                  >
                    Boards
                  </RetroButton>
                )}
              />
            ))}

            <RetroButton 
              className="absolute -bottom-4 right-8 !px-12 !py-4 w-1/3 text-[24px]"
              onClick={() => setShowCreateTeamModal(true)}
            >
              Add a new team
            </RetroButton>
          </RetroContainer>
        </main>
        <aside className="col-span-4 grid grid-cols-12 gap-y-8 relative">
          <div className="w-1/2 h-full min-h-[70vh] top-22 bg-blue-100 absolute -z-1 retro-shadow"></div>
          <div className="col-span-full h-[60px]">
            <ColumnTitle title="My Tasks" />
          </div>
          {
            isTasksLoading && (
              <div>Loading Tasks ... </div>
            )
          }
          {
            tasks?.map(task => (
              <div key={task.id} className="col-span-full !px-8">
                <TaskCard className="col-span-full" task={task} isDragging={false} />
              </div>
            ))
          }
        </aside>
      </div>

      {selectedTeam && (
        <TeamProjectsModal
          teamId={selectedTeam.id!}
          teamName={selectedTeam.name!}
          onClose={() => setSelectedTeam(null)}
        />
      )}

      {showCreateTeamModal && (
        <CreateTeamModal onClose={() => setShowCreateTeamModal(false)} />
      )}
    </>
  );
};
