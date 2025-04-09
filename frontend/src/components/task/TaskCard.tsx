import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardFooter, CardHeader } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import { Label, Task, TaskStatusEnum } from '@/api';
import { AlertCircle, Clock, GripVertical } from 'lucide-react';

import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { FC, ReactNode } from 'react';

interface TaskCardProps {
  task: Task;
  isDragging?: boolean;
}

const priorityColors: Record<TaskStatusEnum, string> = {
  TODO: 'bg-green-100 text-green-800 hover:bg-green-200',
  DONE: 'bg-amber-100 text-amber-800 hover:bg-amber-200',
  IN_PROGRESS: 'bg-red-100 text-red-800 hover:bg-red-200',
  BLOCKED: 'bg-red-100 text-red-800 hover:bg-red-200',
};

const priorityIcons: Record<TaskStatusEnum, ReactNode> = {
  TODO: null,
  DONE: <Clock className="mr-2 h-4 w-4" />,
  IN_PROGRESS: <AlertCircle className="mr-2 h-4 w-4" />,
  BLOCKED: <AlertCircle className="mr-2 h-4 w-4" />,
};

export const TaskCard: FC<TaskCardProps> = ({
  task,
  isDragging = false,
}) => {
  const taskId = `task-${task.id}`;

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging: isSortableDragging,
  } = useSortable({
    id: taskId,
    data: {
      type: 'task',
    },
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  const dragging = isDragging || isSortableDragging;

  return (
    <Card
      ref={setNodeRef}
      style={style}
      className={cn(
        'border-2 bg-white shadow-sm transition-shadow hover:shadow-md',
        dragging ? 'opacity-50 shadow-md ring-2 ring-primary' : '',
      )}
    >
      <CardHeader className="flex flex-row items-start justify-between !p-4 !pb-0">
        <h3 className="font-medium">{task.title}</h3>
        <button
          {...attributes}
          {...listeners}
          className="cursor-grab rounded !p-2 text-gray-400 hover:bg-gray-100 hover:text-gray-600 active:cursor-grabbing"
        >
          <GripVertical className="h-4 w-4" />
          <span className="sr-only">Drag task</span>
        </button>
      </CardHeader>

      <CardContent className="!p-4 !pt-2 text-sm text-gray-600">{task.description}</CardContent>

      <CardFooter className="flex flex-wrap gap-2 !p-4 pt-0">
        {task.status && (
          <Badge
            variant="secondary"
            className={cn(
              'font-normal px-2',
              priorityColors[task.status as keyof typeof priorityColors] || 'bg-gray-100 text-gray-800',
            )}
          >
            {task.status && priorityIcons[task.status as keyof typeof priorityIcons]}
            {task.status}
          </Badge>
        )}
        {
          Array.from(task?.labels ?? new Set<Label>()).map((label: Label) => (
            <Badge key={label.id} variant="outline" className={`bg-[${label.color}] font-normal px-2`}>
              {label.name}
            </Badge>
          ))
        }
      </CardFooter>
    </Card>
  );
};
