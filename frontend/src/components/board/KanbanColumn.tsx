import { SortableContext, useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { GripVertical, Trash2 } from 'lucide-react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { FC, useState } from 'react';
import { cn } from '@/lib/utils';
import { BoardColumnControllerApiFactory, Task } from '@/api';
import { TaskCard } from '@components/task/TaskCard.tsx';
import { RetroButton } from '@components/common/RetroButton';
import { RetroModal } from '@components/common/RetroModal';
import { useTranslation } from 'react-i18next';

interface KanbanColumnProps {
  id: string;
  title: string;
  tasks: Task[];
  boardId: number;
  colorClass: string;
  onAddTask?: () => void;
  onEditTask?: (task: Task) => void;
  isTaskDragging?: boolean;
}

export const KanbanColumn: FC<KanbanColumnProps> = ({
  id,
  title,
  tasks,
  boardId,
  colorClass,
  onAddTask,
  onEditTask,
  isTaskDragging,
}) => {
  const { t } = useTranslation();
  const columnId = `column-${id}`;
  const taskIds = tasks.map(task => `task-${task.id}`);
  const queryClient = useQueryClient();
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const deleteColumnMutation = useMutation({
    mutationFn: async (deletedColumnId: number) => {
      await BoardColumnControllerApiFactory().deleteColumn(deletedColumnId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board', boardId] });
      queryClient.invalidateQueries({ queryKey: ['board'] });
    },
  });

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
    <>
      <div
        ref={setNodeRef}
        style={style}
        className={cn('flex flex-col border-2 retro-shadow', colorClass, isDragging ? 'opacity-50' : '')}
      >
        <div
          className="flex items-center justify-between border-b border-inherit
          !p-4 relative w-full h-[60px] bg-[#83BDFF] shadow-[8px_8px_0px_#000000]"
        >
          <h2 className="text-lg upercase font-bold">{title}</h2>
          <div className="flex items-center gap-2">
            <span className="flex h-6 w-6 items-center justify-center rounded-full bg-white text-xs font-medium">
              {tasks.length}
            </span>
            <button
              className="rounded p-1 text-gray-400 hover:bg-gray-100 hover:text-red-600"
              onClick={e => {
                e.stopPropagation();
                setShowDeleteModal(true);
              }}
              title={t('kanban.deleteColumn')}
              type="button"
            >
              <Trash2 className="h-4 w-4" />
            </button>
            <button
              {...attributes}
              {...listeners}
              className="cursor-grab rounded !p-2 hover:bg-black/5 active:cursor-grabbing"
            >
              <GripVertical className="h-6 w-6" />
              <span className="sr-only">{t('kanban.dragColumn')}</span>
            </button>
          </div>
        </div>
        <div className="px-4 pt-2 mt-8">
          <RetroButton
            size="sm"
            icon={null}
            onClick={onAddTask}
            className="w-full py-8 text-sm"
          >
            {t('kanban.addTask')}
          </RetroButton>
        </div>

        <div className="flex-1 p-4">
          <SortableContext items={taskIds}>
            <div className="flex flex-col gap-6">
              {tasks.map(task => (
                <TaskCard key={`task-${task.id}`} task={task} onEdit={onEditTask} />
              ))}

              {isTaskDragging && (
                <div
                  className="flex h-20 items-center justify-center rounded-lg
                border-2 border-dashed border-blue-400 bg-blue-50/60 p-4 text-center text-sm text-blue-700 mt-2"
                  style={{ minHeight: 48 }}
                >
                  {t('kanban.dropTasks')}
                </div>
              )}
            </div>
          </SortableContext>
        </div>
      </div>

      {showDeleteModal && (
        <RetroModal
          title={t('kanban.deleteColumn')}
          subtitle={t('kanban.deleteConfirm', { column: title })}
        >
          <div className="h-[10vh] flex items-center justify-end gap-4">
            <RetroButton
              variant="secondary"
              onClick={() => setShowDeleteModal(false)}
            >
              {t('common.cancel')}
            </RetroButton>
            <RetroButton
              onClick={() => {
                deleteColumnMutation.mutate(+id);
                setShowDeleteModal(false);
              }}
            >
              {t('common.delete')}
            </RetroButton>
          </div>
        </RetroModal>
      )}
    </>
  );
};
