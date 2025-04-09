import { FC, useState } from 'react';
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

import { KanbanColumn } from '@components/board/KanbanColumn.tsx';
import { TaskCard } from '@components/task/TaskCard.tsx';
import type { Board, BoardColumn, Label } from '@api/api';
import type { Task } from '@/api';

const mockLabels: Label[] = [
  { id: 1, name: 'Research', color: 'bg-blue-500' },
  { id: 2, name: 'Marketing', color: 'bg-green-500' },
  { id: 3, name: 'Development', color: 'bg-red-500' },
  { id: 4, name: 'Design', color: 'bg-yellow-500' },
  { id: 5, name: 'Documentation', color: 'bg-purple-500' },
  { id: 6, name: 'Management', color: 'bg-rose-500' },
];


const initialBoard: Board = {
  id: 1,
  name: 'Project Tasks',
  description: 'Organize and track project tasks',
  ownerId: 'user-123',
  columns: [
    {
      id: 1,
      name: 'To Do',
      position: 0,
      tasks: [
        {
          id: 1,
          title: 'Research competitors',
          description: 'Look into what our competitors are doing',
          status: 'TODO',
          labels: new Set([mockLabels[1], mockLabels[1]]),
          position: 0,
        },
        {
          id: 2,
          title: 'Design new landing page',
          description: 'Create wireframes for the new landing page',
          status: 'TODO',
          labels: new Set([mockLabels[1], mockLabels[1]]),
          position: 1,
        },
        {
          id: 3,
          title: 'Update documentation',
          description: 'Make sure our docs are up to date',
          status: 'TODO',
          labels: new Set([mockLabels[1], mockLabels[1]]),
          position: 2,
        },
      ],
    },
    {
      id: 2,
      name: 'In Progress',
      position: 1,
      tasks: [
        {
          id: 4,
          title: 'Implement authentication',
          description: 'Add login and signup functionality',
          status: 'IN_PROGRESS',
          labels: new Set([mockLabels[3], mockLabels[4]]),
          position: 0,
        },
        {
          id: 5,
          title: 'Create component library',
          description: 'Build reusable UI components',
          status: 'IN_PROGRESS',
          labels: new Set([mockLabels[3], mockLabels[4]]),
          position: 1,
        },
      ],
    },
    {
      id: 3,
      name: 'Review',
      position: 2,
      tasks: [
        {
          id: 6,
          title: 'Code review PR #42',
          description: 'Review the pull request for the new feature',
          status: 'IN_PROGRESS',
          labels: new Set([mockLabels[4], mockLabels[5]]),
          position: 0,
        },
      ],
    },
    {
      id: 4,
      name: 'Done',
      position: 3,
      tasks: [
        {
          id: 7,
          title: 'Set up CI/CD pipeline',
          description: 'Configure automated testing and deployment',
          status: 'DONE',
          labels: new Set([mockLabels[4], mockLabels[5]]),
          position: 0,
        },
        {
          id: 8,
          title: 'User interviews',
          description: 'Conduct user interviews for feedback',
          status: 'DONE',
          labels: new Set([mockLabels[4], mockLabels[5]]),
          position: 1,
        },
      ],
    },
  ],
};

// Column color mapping
const columnColors: Record<number, string> = {
  1: 'bg-pink-100 border-pink-300',
  2: 'bg-blue-100 border-blue-300',
  3: 'bg-purple-100 border-purple-300',
  4: 'bg-green-100 border-green-300',
};

interface KanbanBoardProps {
}

export const KanbanBoard: FC<KanbanBoardProps> = () => {
  const [board, setBoard] = useState<Board>(initialBoard);
  const [activeTask, setActiveTask] = useState<Task | null>(null);
  const [activeColumnId, setActiveColumnId] = useState<number | null>(null);

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

      for (const column of board.columns || []) {
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

      setBoard((prev: Board) => {
        const newBoard = { ...prev };
        const newColumns = [...(newBoard.columns || [])];

        // Find source and target columns
        const sourceColumnIndex = newColumns.findIndex(col => col.id === activeColumnId);
        const targetColumnIndex = newColumns.findIndex(col => col.id === targetColumnId);

        if (sourceColumnIndex === -1 || targetColumnIndex === -1) return prev;

        // Remove from source column
        const sourceColumn = { ...newColumns[sourceColumnIndex] };
        sourceColumn.tasks = sourceColumn.tasks?.filter(task => task.id !== activeTask.id) || [];

        // Add to target column
        const targetColumn = { ...newColumns[targetColumnIndex] };
        const updatedTask = {
          ...activeTask,
          columnId: targetColumnId,
          position: targetColumn.tasks?.length || 0,
        };
        targetColumn.tasks = [...(targetColumn.tasks || []), updatedTask];

        // Update columns in the board
        newColumns[sourceColumnIndex] = sourceColumn;
        newColumns[targetColumnIndex] = targetColumn;
        newBoard.columns = newColumns;

        setActiveColumnId(targetColumnId);
        return newBoard;
      });
    }

    // If over another task
    if (overId.startsWith('task-')) {
      const targetTaskId = Number.parseInt(overId.replace('task-', ''), 10);

      // Find which column contains the target task
      let targetColumnId: number | null = null;
      let targetTaskIndex = -1;
      let targetColumnIndex = -1;

      for (let i = 0; i < (board.columns || []).length; i++) {
        const column = board.columns![i];
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
        setBoard((prev: Board) => {
          const newBoard = { ...prev };
          const newColumns = [...(newBoard.columns || [])];
          const columnIndex = newColumns.findIndex(col => col.id === activeColumnId);

          if (columnIndex === -1) return prev;

          const column = { ...newColumns[columnIndex] };
          const sourceTaskIndex = column.tasks?.findIndex(task => task.id === activeTask.id) || -1;

          if (sourceTaskIndex === -1 || !column.tasks) return prev;

          // Reorder tasks in the column
          column.tasks = arrayMove(column.tasks, sourceTaskIndex, targetTaskIndex);

          // Update positions
          column.tasks = column.tasks.map((task, index) => ({
            ...task,
            position: index,
          }));

          newColumns[columnIndex] = column;
          newBoard.columns = newColumns;

          return newBoard;
        });
      } else {
        // Move to different column
        setBoard((prev: Board) => {
          const newBoard = { ...prev };
          const newColumns = [...(newBoard.columns || [])];

          // Find source column
          const sourceColumnIndex = newColumns.findIndex(col => col.id === activeColumnId);

          if (sourceColumnIndex === -1) return prev;

          // Remove from source column
          const sourceColumn = { ...newColumns[sourceColumnIndex] };
          sourceColumn.tasks = sourceColumn.tasks?.filter(task => task.id !== activeTask.id) || [];

          // Add to target column at specific position
          const targetColumn = { ...newColumns[targetColumnIndex] };
          const updatedTask = {
            ...activeTask,
            columnId: targetColumnId,
          };

          const newTasks = [...(targetColumn.tasks || [])];
          newTasks.splice(targetTaskIndex, 0, updatedTask);

          // Update positions
          targetColumn.tasks = newTasks.map((task, index) => ({
            ...task,
            position: index,
          }));

          // Update columns in the board
          newColumns[sourceColumnIndex] = sourceColumn;
          newColumns[targetColumnIndex] = targetColumn;
          newBoard.columns = newColumns;

          setActiveColumnId(targetColumnId);
          return newBoard;
        });
      }
    }
  };

  const handleDragEnd = (_: DragEndEvent) => {
    setActiveTask(null);
    setActiveColumnId(null);
  };

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
