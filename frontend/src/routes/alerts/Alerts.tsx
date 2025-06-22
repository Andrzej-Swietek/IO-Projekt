import { FC } from 'react';
import { useQueries } from '@tanstack/react-query';
import { RetroContainer } from '@/components/common/RetroContainer';
import {
  AlertEntityControllerApiFactory,
  AlertOverloadedTeamEntity,
  AlertTaskStuckEntity,
  AlertUserInactiveEntity,
} from '@/api';
import { useTranslation } from 'react-i18next';
import { RetroEntryCard } from '@components/common/RetroEntryCard.tsx';

const api = AlertEntityControllerApiFactory();

const fetchUserInactive = async () => (await api.getAllUserInactive({})).data;
const fetchTaskStuck = async () => (await api.getAllTaskStuck({})).data;
const fetchOverloadedTeam = async () => (await api.getAllOverloadedTeam({})).data;

type AlertType = (AlertUserInactiveEntity | AlertTaskStuckEntity | AlertOverloadedTeamEntity) & {
  type: 'user-inactive' | 'task-stuck' | 'overloaded-team';
};

const getSeverityColor = (severity?: string) => {
  switch (severity) {
    case 'critical':
      return 'text-[var(--primary-red)]';
    case 'warning':
      return 'text-[var(--primary-yellow)]';
    case 'info':
      return 'text-[var(--primary-blue)]';
    default:
      return 'text-gray-700';
  }
};

const getAlertSubtitle = (alert: AlertType) => {
  switch (alert.type) {
    case 'user-inactive':
      return `User: ${(alert as AlertUserInactiveEntity).userId ?? ''} | Days inactive: ${(alert as AlertUserInactiveEntity).daysInactive ?? '?'}`;
    case 'task-stuck':
      return `Task: ${(alert as AlertTaskStuckEntity).taskId ?? ''} | Days stuck: ${(alert as AlertTaskStuckEntity).daysStuck ?? '?'}`;
    case 'overloaded-team':
      return `Team: ${(alert as AlertOverloadedTeamEntity).teamId ?? ''} | Overload: ${(alert as AlertOverloadedTeamEntity).overloadedMembers ?? '?'}`;
    default:
      return '';
  }
};

export const Alerts: FC = () => {
  const { t } = useTranslation();
  const results = useQueries({
    queries: [
      { queryKey: ['user-inactive-alerts'], queryFn: fetchUserInactive },
      { queryKey: ['task-stuck-alerts'], queryFn: fetchTaskStuck },
      { queryKey: ['overloaded-team-alerts'], queryFn: fetchOverloadedTeam },
    ],
  });

  const isLoading = results.some(r => r.isLoading);
  const isError = results.some(r => r.isError);
  const alerts: AlertType[] = results.flatMap((r, i) =>
    (r.data ?? []).map((a: any) => ({
      ...a,
      type: ['user-inactive', 'task-stuck', 'overloaded-team'][i],
    })),
  );

  return (
    <>
      <div className="w-full h-[125px] !px-32 !pt-12 !pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] leading-[100%] tracking-[0%] align-bottom text-[var(--primary-black)]">
          {t('alerts.title')}
        </h1>
      </div>
      <div className="flex flex-col items-center justify-start min-h-[80vh] my-8">
        <RetroContainer className="w-full lg:w-[80%] !px-12 !py-10 flex flex-col gap-8">
          <div className="flex flex-col gap-4">
            {isLoading && <div>Loading...</div>}
            {isError && <div>Error loading alerts</div>}
            {alerts.map(alert => (
              <div key={window.crypto.randomUUID()} className="retro-alert">
                <RetroEntryCard
                  left={(
                    <span
                      className={`font-bold ${getSeverityColor(alert.severity?.toLowerCase())}`}
                    >
                      {alert.type.replace('-', ' ').toUpperCase()}
                      {alert.severity ? ` [${alert.severity.toUpperCase()}]` : ''}
                    </span>
                  )}
                  right={(
                    <div>
                      <div>{alert.message}</div>
                      <div className="text-xs text-gray-500 mt-1">{getAlertSubtitle(alert)}</div>
                    </div>
                  )}
                />
              </div>
            ))}
          </div>
        </RetroContainer>
      </div>
    </>
  );
};
