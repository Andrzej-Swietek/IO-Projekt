import { TaskHistory } from '@/api';
import { Clock, UserCircle2 } from 'lucide-react';
import { FC } from 'react';
import { useTranslation } from 'react-i18next';

interface TaskHistoryProps {
  history: TaskHistory[];
}

export const TaskHistoryTimeline: FC<TaskHistoryProps> = ({ history }) => {
  const { t } = useTranslation();
  const actionLabels = useActionLabels(t);

  if (!history?.length) return null;

  return (
    <section className="my-6">
      <h3 className="text-lg font-bold mb-4 text-black tracking-wide uppercase">
        {t('taskHistory.title')}
      </h3>
      <ul className="relative border-l border-gray-300 pl-6">
        {history.map(entry => (
          <li key={entry.timestamp} className="mb-6 ml-4">
            <div
              className="absolute w-3 h-3 bg-blue-600 rounded-full -left-1.5 top-1.5 border-2 border-white shadow-md"
            />
            <div className="flex items-center gap-2 text-sm text-gray-500 mb-1">
              <Clock className="w-4 h-4" />
              <span>{new Date(entry.timestamp!).toLocaleString()}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-gray-700">
              <UserCircle2 className="w-4 h-4" />
              <span className="font-semibold">{entry.user || t('taskHistory.system')}</span>
            </div>
            <div className="mt-1 ml-1 text-sm text-black">
              {actionLabels[entry.action!] || t('taskHistory.actions.FALLBACK')}
              :
              <span className="mx-8 text-gray-700">{entry.actionDescription}</span>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
};

const useActionLabels = (t: ReturnType<typeof useTranslation>['t']) => ({
  CREATED: t('taskHistory.actions.CREATED'),
  UPDATED_TITLE: t('taskHistory.actions.UPDATED_TITLE'),
  UPDATED_DESCRIPTION: t('taskHistory.actions.UPDATED_DESCRIPTION'),
  UPDATED_STATUS: t('taskHistory.actions.UPDATED_STATUS'),
  UPDATED_POSITION: t('taskHistory.actions.UPDATED_POSITION'),
  UPDATED_COLUMN: t('taskHistory.actions.UPDATED_COLUMN'),
  ADDED_ASSIGNEE: t('taskHistory.actions.ADDED_ASSIGNEE'),
  DELETED_ASSIGNEE: t('taskHistory.actions.DELETED_ASSIGNEE'),
  ADDED_LABEL: t('taskHistory.actions.ADDED_LABEL'),
  DELETED_LABEL: t('taskHistory.actions.DELETED_LABEL'),
  ADDED_COMMENT: t('taskHistory.actions.ADDED_COMMENT'),
  EDITED_COMMENT: t('taskHistory.actions.EDITED_COMMENT'),
  DELETED_COMMENT: t('taskHistory.actions.DELETED_COMMENT'),
  CLOSED: t('taskHistory.actions.CLOSED'),
  TASK_DELETED: t('taskHistory.actions.TASK_DELETED'),
});
