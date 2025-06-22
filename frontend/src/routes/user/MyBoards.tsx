import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { ColumnTitle } from '@components/board';
import { TaskCard } from '@components/task';
import { TaskControllerApiFactory, TeamControllerApiFactory } from '@/api';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router';
import { FC, useState } from 'react';
import { Loading } from '@components/common/Loading.tsx';
import { FileText, Folder, FolderOpen } from 'lucide-react';
import { useTranslation } from 'react-i18next';

const fetchUserTasks = async (userId: string) => {
  const response = await TaskControllerApiFactory().getTasksByUserId(userId);
  return response.data;
};

const fetchUsersTeams = async (userId: string) => {
  const response = await TeamControllerApiFactory().getTeamsByUserId(userId);
  return response.data;
};

export const MyBoards: FC = () => {
  const { t } = useTranslation();
  const { profile } = useUserProfile();
  const userId = profile?.id;
  const navigate = useNavigate();

  const [expandedTeams, setExpandedTeams] = useState<Record<string, boolean>>({});
  const [expandedProjects, setExpandedProjects] = useState<Record<string, boolean>>({});

  const {
    data: tasks,
    isLoading: isTasksLoading,
  } = useQuery({
    queryKey: ['my-tasks', userId],
    queryFn: () => fetchUserTasks(userId!),
    enabled: !!userId,
  });

  const {
    data: teams,
    isLoading: isTeamsLoading,
  } = useQuery({
    queryKey: ['my-teams', userId],
    queryFn: () => fetchUsersTeams(userId!),
    enabled: !!userId,
  });

  const toggleExpand = (id: string, isTeam = true) => {
    if (isTeam) {
      setExpandedTeams(prev => ({ ...prev, [id]: !prev[id] }));
    } else {
      setExpandedProjects(prev => ({ ...prev, [id]: !prev[id] }));
    }
  };

  if (!isTasksLoading && !userId) {
    navigate('/auth/login');
  }

  if (isTeamsLoading) {
    return <Loading className="h-screen"></Loading>;
  }

  return (
    <>
      <div className="w-full h-[125px] px-32 pt-12 pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] text-[var(--primary-black)]">
          {t('myBoards.hello')}
          {' '}
          {profile?.firstName}
          {' '}
          {profile?.lastName}
        </h1>
      </div>
      <div className="grid grid-cols-12 gap-8 min-h-[80vh] p-8">
        <main className="col-span-8">
          <div className="bg-[var(--primary-yellow)] p-6 retro-shadow mb-6">
            <h2 className="font-bold text-2xl text-[var(--primary-black)] font-[Josefin_Sans] tracking-wide">
              {t('myBoards.myTeamsBoards')}
            </h2>
          </div>

          {isTeamsLoading && (
            <div
              className="bg-[var(--secondary-yellow)] p-4 retro-shadow mt-4 text-[var(--primary-black)] font-mono"
            >
              {t('myBoards.loadingTeams')}
            </div>
          )}

          {teams?.map(team => (
            <div
              data-test={`team-${team.id!}`}
              key={window.crypto.randomUUID()}
              className="mt-6 bg-[var(--primary-white)] retro-shadow overflow-hidden"
            >
              <div
                className="cursor-pointer font-bold text-[var(--primary-black)] bg-[var(--primary-blue)]
                 py-6 px-6 hover:bg-[var(--secondary-blue)] transition-colors duration-200"
                onClick={() => toggleExpand(`${team.id!}`)}
              >
                <span className="text-xl mr-3">
                  {expandedTeams[team.id!]
                    ? <FolderOpen className="inline mr-2 w-5 h-5" />
                    : <Folder className="inline mr-2 w-5 h-5" />}
                </span>
                {' '}
                <span className="text-lg tracking-wide">{team.name}</span>
              </div>

              {expandedTeams[team.id!] && (
                <div className="p-6 bg-[rgba(243,243,243,1)]">
                  {team.projects?.map(project => (
                    <div
                      key={project.id}
                      className="ml-6 min-h-[5vh] mt-4 bg-[var(--primary-white)] retro-shadow overflow-hidden"
                    >
                      <div
                        className="cursor-pointer py-8 px-8 min-h-[5vh] text-[var(--primary-black)] bg-[var(--secondary-yellow)]
                         p-3 hover:bg-[var(--primary-yellow)] transition-colors duration-200 border-b-2 border-[var(--primary-black)]"
                        onClick={() => toggleExpand(`${project.id!}`, false)}
                      >
                        <span className="text-xl mr-3">
                          {expandedProjects[project.id!]
                            ? <FolderOpen className="inline mr-2 w-5 h-5" />
                            : <Folder className="inline mr-2 w-5 h-5" />}
                        </span>
                        <span className="font-semibold">{project.name}</span>
                      </div>

                      {expandedProjects[project.id!] && (
                        <div className="pt-6 bg-[var(--primary-white)] px-4 pb-4">
                          {project.boards?.map(board => (
                            <div
                              key={board.id}
                              onClick={() => navigate(`/board/${board.id}`)}
                              className="ml-6 mt-6 py-6 px-10 cursor-pointer text-[var(--primary-black)]
                               bg-[var(--primary-red)] hover:bg-[var(--primary-yellow)]  retro-shadow transition-all
                                duration-200 hover:translate-x-1 hover:translate-y-1 hover:shadow-[4px_4px_0px_#000000]"
                            >
                              <span className="text-lg mr-3">
                                <FileText className="inline mr-2 w-5 h-5" />
                              </span>
                              <span
                                className="font-medium underline decoration-2 decoration-[var(--primary-black)]"
                              >
                                {board.name}
                              </span>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </main>
        <aside className="col-span-4 grid grid-cols-12 gap-y-8 relative">
          <div className="w-1/2 h-full min-h-[70vh] top-22 bg-blue-100 absolute -z-1 retro-shadow"></div>
          <div className="col-span-full h-[60px]">
            <ColumnTitle title={t('myBoards.myTasks')} />
          </div>
          {isTasksLoading && <div>{t('myBoards.loadingTasks')}</div>}
          {tasks?.map(task => (
            <div key={task.id} className="col-span-full px-8">
              <TaskCard task={task} isDragging={false} />
            </div>
          ))}
        </aside>
      </div>
    </>
  );
};
