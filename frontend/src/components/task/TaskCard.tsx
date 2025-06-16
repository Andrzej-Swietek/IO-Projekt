import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import { Task } from '@/api';
import { AlertCircle, Flame, GripVertical, Pencil, Trash2 } from 'lucide-react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TaskControllerApiFactory } from '@/api';

import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { FC, HTMLAttributes, useState } from 'react';
import { useUsersByIds } from '@hooks/useUsersByIds.ts';
import { Link } from 'react-router';
import { DeleteTaskModal } from './DeleteTaskModal';

interface TaskCardProps extends HTMLAttributes<HTMLDivElement> {
  task: Task;
  isDragging?: boolean;
  onEdit?: (task: Task) => void;
}

const statusStyles = {
  TODO: 'bg-gray-100 text-gray-800 hover:bg-gray-200',
  IN_PROGRESS: 'bg-yellow-100 text-yellow-800 hover:bg-yellow-200',
  DONE: 'bg-green-100 text-green-800 hover:bg-green-200',
  BLOCKED: 'bg-red-100 text-red-800 hover:bg-red-200',
} as const;

const priorityIcons = {
  LOW: null,
  MEDIUM: <Flame className="h-4 w-4 text-yellow-500" />,
  HIGH: <Flame className="h-4 w-4 text-orange-600" />,
  CRITICAL: <AlertCircle className="h-4 w-4 text-red-600" />,
};

export const TaskCard: FC<TaskCardProps> = ({ task, isDragging = false, className, onEdit, ...props }) => {
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const queryClient = useQueryClient();

  const deleteTaskMutation = useMutation({
    mutationFn: async () => {
      if (!task.id) throw new Error('Task ID is required');
      await TaskControllerApiFactory().deleteTask(task.id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board'] });
      queryClient.invalidateQueries({ queryKey: ['boards'] });
      queryClient.invalidateQueries({ queryKey: ['my-tasks'] });
    },
  });

  const { attributes, listeners, setNodeRef, transform, transition, isDragging: dndDragging } = useSortable({
    id: `task-${task.id}`,
    data: { type: 'task' },
  });

  const dragging = isDragging || dndDragging;
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  const avatarUrl = (email: string): string =>
    `https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=${encodeURIComponent(email)}`;

  const taskLabels = Array.from(task.labels ?? []);
  const { data: usersById } = useUsersByIds(task.assignees ?? []);

  return (
    <>
      <div
        ref={setNodeRef}
        style={style}
        {...attributes}
        {...listeners}
        {...props}
        className={cn(
          'bg-[var(--primary-white)] rounded-md border border-gray-200 text-sm transition-shadow !p-4 min-h-[15vh]',
          'hover:shadow-lg retro-shadow flex flex-col gap-2',
          dragging && 'opacity-60 ring-2 ring-blue-400',
          className,
        )}
      >
        {/* Top Row: Title + Drag Handle + Edit Button */}
        <div className="flex justify-between items-start">
          <h3 className="font-semibold text-gray-800">{task.title}</h3>
          <div className="flex gap-1 items-center">
            <button
              className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              onClick={e => { e.stopPropagation(); onEdit?.(task); }}
              title="Edit Task"
              type="button"
            >
              <Pencil className="h-4 w-4" />
            </button>
            <button
              className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-red-600"
              onClick={e => { e.stopPropagation(); setShowDeleteModal(true); }}
              title="Delete Task"
              type="button"
            >
              <Trash2 className="h-4 w-4" />
            </button>
            <button
              className="cursor-grab rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600 active:cursor-grabbing"
            >
              <GripVertical className="h-4 w-4" />
              <span className="sr-only">Drag</span>
            </button>
          </div>
        </div>

        {/* Description */}
        {task.description && (
          <div className="text-gray-600 !text-[14px]">{task.description}</div>
        )}

        {/* Badges: status, labels, priority */}
        <div className="flex flex-wrap gap-2">
          {task.status && (
            <Badge className={cn('!px-4 !py-2 font-normal', statusStyles[task.status])}>
              {task.status && priorityIcons[task.status as keyof typeof priorityIcons]}
              {task.status}
            </Badge>
          )}

          {taskLabels?.map(label => (
            <Badge
              key={label.id}
              variant="outline"
              className="px-2 font-normal"
              style={{ backgroundColor: label.color, color: 'white' }}
            >
              {label.name}
            </Badge>
          ))}
        </div>

        {/* Footer: assignees and dueDate */}
        {(task.assignees?.length || task.createdDate) && (
          <div className="pt-2 text-xs text-gray-500">
            <div className="flex -space-x-2 mb-4">
              {task.assignees?.map(userId => {
                const user = usersById?.[userId];
                if (!user) return null;

                return (
                  <Link to={`/user/${userId}`} key={userId}>
                    <div className="w-auto flex row nowrap items-center justify-around gap-4 border-2 rounded-sm !px-4 !py-2">
                      <img
                        src={avatarUrl(user.email!)}
                        title={user.firstName}
                        className="h-6 w-6 rounded-full object-cover"
                        alt="user avatar"
                        loading="lazy"
                      />
                      <span>
                        {user.firstName}
                        {' '}
                        {user.lastName}
                      </span>
                    </div>
                  </Link>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {showDeleteModal && (
        <DeleteTaskModal
          taskTitle={task.title || 'Untitled Task'}
          onClose={() => setShowDeleteModal(false)}
          onConfirm={() => {
            deleteTaskMutation.mutate();
            setShowDeleteModal(false);
          }}
          isDeleting={deleteTaskMutation.isPending}
        />
      )}
    </>
  );
};
