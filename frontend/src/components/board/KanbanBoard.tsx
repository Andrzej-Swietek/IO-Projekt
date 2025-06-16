import { FC, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  DndContext,
  type DragEndEvent,
  type DragOverEvent,
  DragOverlay,
  type DragStartEvent,
  PointerSensor,
  useSensor,
  useSensors,
} from '@dnd-kit/core';
import { arrayMove } from '@dnd-kit/sortable';
import { createPortal } from 'react-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

import { KanbanColumn } from '@components/board/KanbanColumn.tsx';
import { TaskCard } from '@components/task/TaskCard.tsx';
import { BoardControllerApiFactory, BoardColumnControllerApiFactory, Board, BoardColumn, Task, ReorderBoardRequest, ColumnOrderItem } from '@/api';

// Column color mapping
const columnColors: Record<number, string> = {
  1: 'bg-pink-100 border-pink-300',
  2: 'bg-blue-100 border-blue-300',
  3: 'bg-purple-100 border-purple-300',
  4: 'bg-green-100 border-green-300',
};

export const KanbanBoard: FC = () => {
  const { id } = useParams();
  const queryClient = useQueryClient();
  const [activeTask, setActiveTask] = useState<Task | null>(null);
  const [activeColumnId, setActiveColumnId] = useState<number | null>(null);

  const { data: board, isLoading } = useQuery({
    queryKey: ['board', id],
    queryFn: async () => {
      if (!id) throw new Error('Board ID is required');
      const response = await BoardControllerApiFactory().getBoardById(Number(id));
      return response.data;
    },
    enabled: !!id,
  });

  const reorderColumnsMutation = useMutation({
    mutationFn: async (orderList: ColumnOrderItem[]) => {
      if (!id) throw new Error('Board ID is required');
      const reorderRequest: ReorderBoardRequest = { orderList };
      const response = await BoardControllerApiFactory().reorderColumns(Number(id), reorderRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board', id] });
    },
  });

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
  );

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    const id = active.id as string;

    if (id.startsWith('task-')) {
      const taskId = Number.parseInt(id.replace('task-', ''), 10);

      for (const column of board?.columns || []) {
        const task = column.tasks?.find(t => t.id === taskId);
        if (task) {
          setActiveTask(task);
          setActiveColumnId(column.id || null);
          break;
        }
      }
    }
  };

  const handleDragOver = (event: DragOverEvent) => {
    const { active, over } = event;
    if (!over) return;

    const activeId = active.id as string;
    const overId = over.id as string;

    // Skip if not dragging a task or not over a column or task
    if (!activeId.startsWith('task-') || !overId) return;

    // If over a column directly
    if (overId.startsWith('column-')) {
      const targetColumnId = Number.parseInt(overId.replace('column-', ''), 10);

      if (activeColumnId === targetColumnId) return;
      if (!activeTask) return;

      // Update task status in the backend
      const taskMutation = useMutation({
        mutationFn: async () => {
          const updatedTask = {
            ...activeTask,
            columnId: targetColumnId,
            position: board?.columns?.find(col => col.id === targetColumnId)?.tasks?.length || 0,
          };
          // TODO: Add task update API call
          return updatedTask;
        },
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: ['board', id] });
        },
      });

      taskMutation.mutate();
    }

    // If over another task
    if (overId.startsWith('task-')) {
      const targetTaskId = Number.parseInt(overId.replace('task-', ''), 10);

      // Find which column contains the target task
      let targetColumnId: number | null = null;
      let targetTaskIndex = -1;
      let targetColumnIndex = -1;

      for (let i = 0; i < (board?.columns || []).length; i++) {
        const column = board?.columns?.[i];
        if (!column) continue;
        
        const taskIndex = column.tasks?.findIndex(t => t.id === targetTaskId) || -1;

        if (taskIndex !== -1) {
          targetColumnId = column.id || null;
          targetTaskIndex = taskIndex;
          targetColumnIndex = i;
          break;
        }
      }

      if (targetColumnId === null || targetTaskIndex === -1 || targetColumnIndex === -1 || !activeTask) return;

      // If in the same column, reorder
      if (activeColumnId === targetColumnId) {
        const taskMutation = useMutation({
          mutationFn: async () => {
            const updatedTask = {
              ...activeTask,
              position: targetTaskIndex,
            };
            // TODO: Add task reorder API call
            return updatedTask;
          },
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['board', id] });
          },
        });

        taskMutation.mutate();
      } else {
        // Move to different column
        const taskMutation = useMutation({
          mutationFn: async () => {
            const updatedTask = {
              ...activeTask,
              columnId: targetColumnId,
              position: targetTaskIndex,
            };
            // TODO: Add task move API call
            return updatedTask;
          },
          onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['board', id] });
          },
        });

        taskMutation.mutate();
      }
    }
  };

  const handleDragEnd = (_: DragEndEvent) => {
    setActiveTask(null);
    setActiveColumnId(null);
  };

  if (isLoading) {
    return <div className="flex items-center justify-center h-full">Loading board...</div>;
  }

  if (!board) {
    return <div className="flex items-center justify-center h-full">No board found. Create a new board to get started.</div>;
  }

  return (
    <DndContext
      sensors={sensors}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        {(board.columns || []).map((column: BoardColumn) => (
          <KanbanColumn
            key={column.id}
            id={column.id?.toString() || ''}
            title={column.name || ''}
            tasks={column.tasks || []}
            colorClass={columnColors[column.id || 0] || 'bg-gray-100 border-gray-300'}
          />
        ))}
      </div>

      {typeof window !== 'undefined'
        && createPortal(
          <DragOverlay>{activeTask && <TaskCard task={activeTask} isDragging />}</DragOverlay>,
          document.body,
        )}
    </DndContext>
  );
};
