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
import { createPortal } from 'react-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import { KanbanColumn } from '@components/board/KanbanColumn.tsx';
import { TaskCard } from '@components/task/TaskCard.tsx';
import {
  BoardColumn,
  BoardControllerApiFactory,
  ColumnOrderItem,
  ReorderBoardRequest,
  Task,
  TaskControllerApiFactory,
} from '@/api';
import { AddTaskModal } from '@components/task/AddTaskModal';

// Column color mapping by name (case-insensitive)
const getColumnColorClass = (name?: string) => {
  if (!name) return 'bg-gray-100 border-gray-300';
  const lower = name.toLowerCase();
  if (lower.includes('done')) return 'bg-green-100 border-green-300';
  if (lower.includes('progress')) return 'bg-purple-100 border-purple-300';
  if (lower.includes('to do')) return 'bg-gray-100 border-gray-300';
  return 'bg-gray-100 border-gray-300';
};

// Helper to map column name to TaskStatus
const getStatusForColumn = (columnName: string): 'TODO' | 'IN_PROGRESS' | 'DONE' | 'BLOCKED' => {
  const lower = columnName.toLowerCase();
  if (lower.includes('progress')) return 'IN_PROGRESS';
  if (lower.includes('done')) return 'DONE';
  if (lower.includes('block')) return 'BLOCKED';
  return 'TODO';
};

interface KanbanBoardProps {
  teamId?: number;
}

export const KanbanBoard: FC<KanbanBoardProps> = ({ teamId }) => {
  const { id } = useParams();
  const queryClient = useQueryClient();
  const [activeTask, setActiveTask] = useState<Task | null>(null);
  const [activeColumnId, setActiveColumnId] = useState<number | null>(null);
  const [showAddTaskModal, setShowAddTaskModal] = useState(false);
  const [addTaskColumnId, setAddTaskColumnId] = useState<number | null>(null);
  const [editTask, setEditTask] = useState<Task | null>(null);
  const [editTaskColumnId, setEditTaskColumnId] = useState<number | null>(null);

  const { data: board, isLoading } = useQuery({
    queryKey: ['board', id],
    queryFn: async () => {
      if (!id) throw new Error('Board ID is required');
      const response = await BoardControllerApiFactory().getBoardById(Number(id));
      return response.data;
    },
    enabled: !!id,
  });

  // @ts-ignore
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

    type MoveTaskMutationType = {
      task: Task;
      columnId: number;
      position: number;
    };

    // Mutation for moving a task to a different column or position
    const moveTaskMutation = useMutation({
      mutationFn: async (data: MoveTaskMutationType) => {
        // Find the target column to get its name
        const targetColumn = board?.columns?.find(col => col.id === data.columnId);
        const newStatus = targetColumn ? getStatusForColumn(targetColumn.name || '') : data.task.status;
        await TaskControllerApiFactory().updateTask(Number(data.task.id), {
          ...data.task,
          columnId: Number(data.columnId),
          position: data.position,
          status: newStatus,
        });
      },
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['board', id] });
      },
    });

    // Mutation for reordering tasks within the same column
    const reorderTaskMutation = useMutation({
      mutationFn: async (data: { task: Task; position: number }) => {
        await TaskControllerApiFactory().updateTask(Number(data.task.id), {
          ...data.task,
          position: data.position,
        });
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
      const draggedId = active.id as string;

      if (draggedId.startsWith('task-')) {
        const taskId = Number.parseInt(draggedId.replace('task-', ''), 10);

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

        // Move task to a different column
        moveTaskMutation.mutate({
          task: activeTask,
          columnId: targetColumnId,
          position: board?.columns?.find(col => col.id === targetColumnId)?.tasks?.length || 0,
        });
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
          reorderTaskMutation.mutate({
            task: activeTask,
            position: targetTaskIndex,
          });
        } else {
          // Move to different column
          moveTaskMutation.mutate({
            task: activeTask,
            columnId: targetColumnId,
            position: targetTaskIndex,
          });
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
      return (
        <div className="flex items-center justify-center h-full">
          No board found. Create a new board to get
          started.
        </div>
      );
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
              boardId={board.id!}
              id={column.id?.toString() || ''}
              title={column.name || ''}
              tasks={column.tasks || []}
              colorClass={getColumnColorClass(column.name)}
              onAddTask={() => {
                setAddTaskColumnId(column.id ?? null);
                setShowAddTaskModal(true);
              }}
              onEditTask={task => {
                setEditTask(task);
                setEditTaskColumnId(column.id ?? null);
              }}
              isTaskDragging={!!activeTask}
            />
          ))}
        </div>

        {typeof window !== 'undefined'
          && createPortal(
            <DragOverlay>{activeTask && <TaskCard task={activeTask} isDragging />}</DragOverlay>,
            document.body,
          )}

        {showAddTaskModal && addTaskColumnId && board?.id && teamId && (
          <AddTaskModal
            columnId={addTaskColumnId}
            boardId={board.id}
            teamId={teamId}
            onClose={() => setShowAddTaskModal(false)}
          />
        )}

        {editTask && board?.id && teamId && editTaskColumnId && (
          <AddTaskModal
            columnId={editTaskColumnId}
            boardId={board.id}
            teamId={teamId}
            onClose={() => {
              setEditTask(null);
              setEditTaskColumnId(null);
            }}
            task={editTask}
            isEdit={true}
          />
        )}
      </DndContext>
    );
};
