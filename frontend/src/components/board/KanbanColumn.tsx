import { SortableContext, useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { GripVertical } from 'lucide-react';

import { cn } from '@/lib/utils';
import { Task } from '@/api';
import { TaskCard } from '@components/task/TaskCard.tsx';
import { FC } from 'react';


interface KanbanColumnProps {
  id: string;
  title: string;
  tasks: Task[];
  colorClass: string;
}

export const KanbanColumn: FC<KanbanColumnProps> = ({ id, title, tasks, colorClass }) => {
  const columnId = `column-${id}`;
  const taskIds = tasks.map(task => `task-${task.id}`);

  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: columnId,
    data: {
      type: 'column',
    },
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={cn('flex flex-col rounded-xl border-2 shadow-md', colorClass, isDragging ? 'opacity-50' : '')}
    >
      <div className="flex items-center justify-between border-b border-inherit p-3">
        <h2 className="font-serif text-lg font-bold">{title}</h2>
        <div className="flex items-center gap-2">
          <span className="flex h-6 w-6 items-center justify-center rounded-full bg-white text-xs font-medium">
            {tasks.length}
          </span>
          <button
            {...attributes}
            {...listeners}
            className="cursor-grab rounded p-1 hover:bg-black/5 active:cursor-grabbing"
          >
            <GripVertical className="h-5 w-5" />
            <span className="sr-only">Drag column</span>
          </button>
        </div>
      </div>

      <div className="flex-1 p-3">
        <SortableContext items={taskIds}>
          <div className="flex flex-col gap-3">
            {tasks.map(task => (
              <TaskCard key={`task-${task.id}`} task={task} />
            ))}

            {tasks.length === 0 && (
              <div
                className="flex h-20 items-center justify-center rounded-lg border-2
                 border-dashed border-gray-300 bg-white/50 p-4 text-center text-sm text-gray-500"
              >
                Drop tasks here
              </div>
            )}
          </div>
        </SortableContext>
      </div>
    </div>
  );
};
