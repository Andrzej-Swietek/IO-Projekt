import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import { Task, TaskControllerApiFactory } from '@/api';
import { AlertCircle, Flame, GripVertical, Pencil, Trash2 } from 'lucide-react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TaskDetailsSheet } from './TaskSheet';
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

  const [showSheet, setShowSheet] = useState(false);

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
          'bg-[var(--primary-white)] border border-gray-200 text-sm transition-shadow !p-4 min-h-[15vh]',
          'hover:shadow-lg retro-shadow flex flex-col gap-2',
          dragging && 'opacity-60 ring-2 ring-blue-400',
          className,
        )}
        onClick={_ => {
          if (!isDragging) setShowSheet(true);
        }}
      >
        {/* Top Row: Title + Drag Handle + Edit Button */}
        <div className="flex justify-between items-start">
          <h3 className="mb-2 block font-bold text-md uppercase tracking-wider text-black">{task.title}</h3>
          <div className="flex gap-1 items-center">
            <button
              className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
              onClick={e => {
                e.stopPropagation();
                onEdit?.(task);
              }}
              title="Edit Task"
              type="button"
            >
              <Pencil className="h-4 w-4" />
            </button>
            <button
              className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-red-600"
              onClick={e => {
                e.stopPropagation();
                setShowDeleteModal(true);
              }}
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
          <div className="font-thin text-sm tracking-wider text-black">{task.description}</div>
        )}

        {/* Badges: status, labels, priority */}
        <div className="flex flex-wrap gap-2">
          {task.status && (
            <Badge className={cn('!px-4 !py-2 font-normal uppercase', statusStyles[task.status])}>
              {task.status && priorityIcons[task.status as keyof typeof priorityIcons]}
              {STATUS_TO_DISPLAY[task.status]}
            </Badge>
          )}

          {taskLabels?.map(label => (
            <Badge
              key={label.id}
              variant="outline"
              className={cn(
                'px-2 py-0.5 rounded-full text-xs font-bold tracking-wide shadow border',
                'border-gray-200',
                'transition-all duration-150 flex items-center px-4 uppercase',
              )}
              style={{
                backgroundColor: label.color,
                color: '#fff',
                textShadow: '0 1px 2px rgba(0,0,0,0.15)',
                letterSpacing: '0.05em',
              }}
              title={label.name}
            >
              {label.name}
            </Badge>
          ))}
        </div>

        {/* Footer: assignees and dueDate */}
        {(task.assignees?.length || task.createdDate) && (
          <div className="pt-2 text-xs text-gray-500">
            <div className="flex items-center gap-2 mb-2">
              {task.assignees?.map(userId => {
                const user = usersById?.[userId];
                if (!user) return null;
                return (
                  <Link
                    to={`/user/${userId}`}
                    key={userId}
                    title={`${user.firstName} ${user.lastName}`}
                  >
                    <img
                      src={avatarUrl(user.email!)}
                      alt={user.firstName}
                      className="h-7 w-7 rounded-full object-cover border-2 border-white -ml-2 first:ml-0 shadow"
                      loading="lazy"
                    />
                  </Link>
                );
              })}
              {task.assignees?.length === 0 && <span>No assignees</span>}
            </div>
            {task.createdDate && (
              <div className="text-gray-400">
                Created:
                {' '}
                {new Date(task.createdDate).toLocaleDateString()}
              </div>
            )}
          </div>
        )}
      </div>

      <TaskDetailsSheet
        task={task}
        open={showSheet}
        onOpenChange={setShowSheet}
      />

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

const STATUS_TO_DISPLAY: Record<string, string> = {
  TODO: 'To Do',
  IN_PROGRESS: 'In Progress',
  DONE: 'Done',
  BLOCKED: 'Blocked',
} as const;
