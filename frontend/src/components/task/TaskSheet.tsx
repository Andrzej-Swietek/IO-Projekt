import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { FC, useState } from 'react';
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet';
import {
  Estimate,
  EstimateControllerApiFactory,
  Task,
  TaskControllerApiFactory,
  TaskHistoryControllerApiFactory,
} from '@/api';
import { AddCommentRetro } from '@components/task/AddCommentRetro.tsx';
import { Badge } from '@/components/ui/badge';
import { Link } from 'react-router';
import { useUsersByIds } from '@hooks/useUsersByIds.ts';
import { TaskHistoryTimeline } from '@components/task/TaskHistoryTimeline.tsx';
import { cn } from '@/lib/utils.ts';
import { AlertCircle, Flame, Zap } from 'lucide-react';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { UpdateStatusModal } from './UpdateStatusModal';
import { toast } from 'sonner';


export const TaskDetailsSheet: FC<{
  task: Task; open: boolean; onOpenChange: (v: boolean) => void;
}> = ({ task, open, onOpenChange }) => {
  const [showStatusModal, setShowStatusModal] = useState(false);
  const queryClient = useQueryClient();

  const updateStatusMutation = useMutation({
    mutationFn: async (newStatus: string) => {
      const api = TaskControllerApiFactory();
      const response = await api.changeTaskStatus(task.id!, newStatus || 'TODO');
      if (response.status === 200) {
        toast.success('Task status updated successfully');
        return response.data;
      } else {
        toast.error('Failed to update task status');
        return response.data;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      setShowStatusModal(false);
    },
  });

  const estimateTaskMutation = useMutation({
    mutationFn: async () => {
      const api = TaskControllerApiFactory();
      const response = await api.estimateTask({
        taskId: task.id,
        taskTitle: task.title,
        taskDescription: task.description,
      });
      if (response.status === 200) {
        toast.success('Task estimated successfully');
        return response.data;
      } else {
        toast.error('Failed to estimate task');
        return response.data;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      queryClient.invalidateQueries({ queryKey: ['taskEstimate', task.id] });
    },
  });

  const handleUpdateStatus = (newStatus: string) => {
    updateStatusMutation.mutate(newStatus);
  };

  const handleEstimateTask = () => {
    estimateTaskMutation.mutate();
  };

  const { data: taskHistory } = useQuery({
    queryKey: ['taskHistory', task.id],
    queryFn: async () => {
      const api = TaskHistoryControllerApiFactory();
      const response = await api.getTaskHistoryByTaskId(task.id!);
      return response.data;
    },
    enabled: !!task.id,
  });
  const { data: estimates } = useQuery({
    queryKey: ['taskEstimate', task.id],
    queryFn: async () => {
      const api = EstimateControllerApiFactory();
      const response = await api.getEstimatesByTaskId(task.id!);
      return response.data;
    },
    enabled: !!task.id,
  });

  const avatarUrl = (email: string): string =>
    `https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=${encodeURIComponent(email)}`;
  const { data: usersById } = useUsersByIds(task.assignees ?? []);

  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent className="min-w-1/2 max-w-none p-8 retro-shadow overflow-auto">
        <SheetHeader className="border-b border-b-black">
          <SheetTitle className="text-3xl font-extrabold text-black mb-2 tracking-tight">
            {task.title}
          </SheetTitle>
          <div className="text-sm text-gray-500">
            <p>
              Created:
              {task.createdDate}
            </p>
            <p>
              Last Modified:
              {task.lastModifiedDate}
            </p>
          </div>
          <div className="text-sm text-gray-500 mt-2 flex flex-row items-center gap-4">
            <div
              className="flex flex-row items-center gap-2 font-bold text-md uppercase tracking-wider "
            >
              <span>Status:</span>
            </div>
            {task.status && (
              <Badge
                className={cn('!px-8 !py-2 h-full font-normal flex justify-center items-center uppercase', statusStyles[task.status])}
              >
                {task.status && priorityIcons[task.status as keyof typeof priorityIcons]}
                {STATUS_TO_DISPLAY[task.status]}
              </Badge>
            )}
            <RetroButton
              size="sm"
              onClick={() => setShowStatusModal(true)}
              icon={<Zap className="w-4 h-4" />}
              className="ml-auto"
            >
              Update Status
            </RetroButton>
            <UpdateStatusModal
              open={showStatusModal}
              onClose={() => setShowStatusModal(false)}
              currentStatus={task.status!}
              onUpdate={handleUpdateStatus}
            />
            <RetroButton onClick={() => handleEstimateTask()} size="sm" icon={<Zap className="w-4 h-4" />}>
              Estimate
            </RetroButton>
          </div>
        </SheetHeader>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 my-4">
          <section className="my-4">
            <h3 className="font-bold text-md mb-6 uppercase tracking-wider text-black">Labels</h3>
            {Array.from(task.labels ?? []).map(label => (
              <Badge
                className="px-6 py-4 text-md font-semibold mr-2 mb-2"
                key={label.id}
                style={{ backgroundColor: label.color, color: '#fff' }}
              >
                {label.name}
              </Badge>
            ))}
          </section>

          <section className="my-4">
            <h3 className="font-bold text-md mb-4 uppercase tracking-wider text-black">Assignees</h3>
            <div className="pt-2 text-md text-gray-500">
              <div className="flex items-center gap-2 mb-2">
                {task.assignees?.map(userId => {
                  const user = usersById?.[userId];
                  if (!user) return null;
                  return (
                    <Link
                      to={`/user/${userId}`}
                      key={userId}
                      title={`${user.firstName} ${user.lastName}`}
                      className="flex flex-row items-center gap-4 justify-between bg-gray-100
                      hover:bg-gray-200 transition-colors duration-200 rounded-lg px-6 py-4"
                    >
                      <img
                        src={avatarUrl(user.email!)}
                        alt={user.firstName}
                        className="h-7 w-7 rounded-full object-cover border-2 border-white -ml-2 first:ml-0 shadow"
                        loading="lazy"
                      />
                      {
                        user.firstName && user.lastName
                          ? ` ${user.firstName} ${user.lastName}`
                          : ''
                      }
                    </Link>
                  );
                })}
                {task.assignees?.length === 0 && <span>No assignees</span>}
              </div>
            </div>
          </section>
        </div>


        <section className="my-4">
          <h3 className="font-bold text-md mb-4 uppercase tracking-wider text-black">Description</h3>
          <div
            className="bg-white/80 border border-black/10 retro-shadow p-8 text-gray-800 shadow-inner min-h-[80px]"
            dangerouslySetInnerHTML={{ __html: task.description ?? '<em>No description</em>' }}
          />
        </section>

        {estimates && (
          <EstimateList estimates={estimates} />
        )}

        <section className="my-4">
          <h3 className="font-bold text-md mb-4 uppercase tracking-wider text-black">Comments</h3>
          {(task.comments ?? []).map(comment => (
            <div key={comment.id} className="text-gray-400 retro-shadow px-8 py-6 border mt-4 mb-8">
              <p className="text-xs text-gray-500 mt-4">
                {comment.authorId || 'Unknown'}
                {' '}
                on
                {' '}
                {new Date(comment.createdDate!).toLocaleString()}
              </p>
              <p className="px-8 py-4">{comment.content}</p>
            </div>
          ))}

          {
            (task.comments?.length ?? 0) === 0 && (
              <p className="text-gray-400 italic retro-shadow px-8 py-6 border mt-4 mb-8">No comments yet.</p>
            )
          }

          <AddCommentRetro taskId={task.id!} />
        </section>

        <TaskHistoryTimeline history={taskHistory || []} />

      </SheetContent>
    </Sheet>
  );
};


export const EstimateList: FC<{ estimates: Estimate[] }> = ({ estimates }) => (
  <section className="my-4">
    <h3 className="font-bold text-md uppercase tracking-wider text-black mb-4">Estimate</h3>
    <div className="border border-gray-300 bg-white/90 shadow-inner retro-shadow p-4 flex flex-col gap-2">
      {estimates.length === 0 && (
        <span className="text-gray-400 italic">No estimates</span>
      )}
      {estimates.map((estimate, idx) => (
        <div
          className="flex items-center gap-8 text-sm border-b border-gray-200 last:border-b-0 px-2 py-2"
          key={idx}
        >
          <div className="flex-1 text-gray-700">
            <span className="font-semibold">Estimated:</span>
            {' '}
            {estimate.estimatedTime ?? <span className="text-gray-400">N/A</span>}
            {' '}
            man-hours
          </div>
          <div className="flex-1 text-gray-700">
            <span className="font-semibold">Actual:</span>
            {' '}
            {estimate.actualTime ?? <span className="text-gray-400">N/A</span>}
          </div>
        </div>
      ))}
    </div>
  </section>
);

const statusStyles = {
  TODO: 'bg-gray-100 text-gray-800 hover:bg-gray-200',
  IN_PROGRESS: 'bg-yellow-100 text-yellow-800 hover:bg-yellow-200',
  DONE: 'bg-green-100 text-green-800 hover:bg-green-200',
  BLOCKED: 'bg-red-100 text-red-800 hover:bg-red-200',
} as const;

const priorityIcons = {
  TODO: null,
  IN_PROGRESS: <Flame className="h-6 w-6 text-yellow-500" />,
  DONE: <Flame className="h-4 w-4 text-green-600" />,
  BLOCKED: <AlertCircle className="h-4 w-4 text-red-600" />,
};

const STATUS_TO_DISPLAY: Record<string, string> = {
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  DONE: 'Done',
  BLOCKED: 'Blocked',
} as const;
