import { FC, useState } from 'react';
import { useNavigate } from 'react-router';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {TaskControllerApiFactory, Team, TeamControllerApiFactory} from '@/api';
import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { ColumnTitle } from '@components/board';
import { RetroContainer } from '@components/common/RetroContainer.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { FolderKanban, Trash2 } from 'lucide-react';
import { RetroEntryCard } from '@components/common/RetroEntryCard.tsx';
import { TaskCard } from '@components/task';
import { TeamProjectsModal } from '@components/team/TeamProjectsModal';
import { CreateTeamModal } from '@components/team/CreateTeamModal';
import { Loading } from '@components/common/Loading.tsx';

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
  const navigate = useNavigate();

  const userId = profile?.id;
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);
  const [showCreateTeamModal, setShowCreateTeamModal] = useState(false);
  const queryClient = useQueryClient();
  const deleteTeamMutation = useMutation({
    mutationFn: async (teamId: number) => {
      await TeamControllerApiFactory().deleteTeam(teamId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['teams'] });
      queryClient.invalidateQueries({ queryKey: ['my-teams', userId] });
    },
  });

  const { isLoading, isError } = useQuery({
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

  if (!isLoading && !userId) {
    navigate('/auth/login');
  }

  if (isError) {
    return <div>Error loading teams</div>;
  }

  if (isLoading) {
    return <Loading className="h-screen"></Loading>;
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
                className="mb-6"
                key={team.id}
                left={<>{team.name}</>}
                right={(
                  <div className="flex gap-2 items-center">
                    <RetroButton
                      className="!px-4 !py-4 mr-8 w-auto"
                      icon={<FolderKanban />}
                      onClick={() => setSelectedTeam(team)}
                    >
                      Projects
                    </RetroButton>
                    <RetroButton
                      className="!px-2 !py-2 w-auto"
                      icon={<Trash2 className="text-red-500" />}
                      variant="secondary"
                      onClick={() => {
                        if (window.confirm(`Are you sure you want to delete team '${team.name}'?`)) {
                          deleteTeamMutation.mutate(team.id!);
                        }
                      }}
                      children=""
                    >

                    </RetroButton>
                  </div>
                )}
              />
            ))}

            <RetroButton
              className="absolute -bottom-4 right-8 !px-8 !py-3 w-1/3 text-base md:text-lg"
              icon={<span className="text-lg md:text-2xl">+</span>}
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
                <TaskCard className="col-span-full" task={task} isDragging={false} isEditable={false} />
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
