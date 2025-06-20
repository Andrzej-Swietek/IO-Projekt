import { TaskHistory, TaskHistoryActionEnum } from '@/api';
import { Clock, UserCircle2 } from 'lucide-react';
import { FC } from 'react';

interface TaskHistoryProps {
  history: TaskHistory[];
}

export const TaskHistoryTimeline: FC<TaskHistoryProps> = ({ history }) => {
  if (!history?.length) return null;

  return (
    <section className="my-6">
      <h3 className="text-lg font-bold mb-4 text-black tracking-wide uppercase">Task History</h3>
      <ul className="relative border-l border-gray-300 pl-6">
        {history.map(entry => (
          <li key={entry.timestamp} className="mb-6 ml-4">
            <div
              className="absolute w-3 h-3 bg-blue-600 rounded-full -left-1.5 top-1.5 border-2 border-white shadow-md"
            >
            </div>
            <div className="flex items-center gap-2 text-sm text-gray-500 mb-1">
              <Clock className="w-4 h-4" />
              <span>{new Date(entry.timestamp!).toLocaleString()}</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-gray-700">
              <UserCircle2 className="w-4 h-4" />
              <span className="font-semibold">{entry.user || 'System'}</span>
            </div>
            <div className="mt-1 ml-1 text-sm">
              {renderAction(entry.action, entry.actionDescription)}
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
};

// TODO: i18n
const renderAction = (action?: TaskHistoryActionEnum, desc?: string) => {
  const actionLabels: Partial<Record<TaskHistoryActionEnum, string>> = {
    CREATED: 'Task created',
    UPDATED_TITLE: 'Title updated',
    UPDATED_DESCRIPTION: 'Description updated',
    UPDATED_STATUS: 'Status changed',
    UPDATED_POSITION: 'Position changed',
    UPDATED_COLUMN: 'Moved to another column',
    ADDED_ASSIGNEE: 'Assignee added',
    DELETED_ASSIGNEE: 'Assignee removed',
    ADDED_LABEL: 'Label added',
    DELETED_LABEL: 'Label removed',
    ADDED_COMMENT: 'Comment added',
    EDITED_COMMENT: 'Comment edited',
    DELETED_COMMENT: 'Comment deleted',
    CLOSED: 'Task closed',
    TASK_DELETED: 'Task deleted',
  };

  return (
    <span className="text-black">
      {actionLabels[action!] || 'Action'}
      :
      <span className="mx-8 text-gray-700">{desc}</span>
    </span>
  );
};
