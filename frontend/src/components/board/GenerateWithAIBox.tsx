import { ChangeEvent, FC, FormEvent, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Board, TaskControllerApiFactory } from '@/api';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';

interface GenerateWithAIBoxProps {
  board: Board;
  onNew?: () => void;
}

export const GenerateWithAIBox: FC<GenerateWithAIBoxProps> = ({
  board,
  onNew,
}) => {
  const queryClient = useQueryClient();
  const [columnId, setColumnId] = useState<number>(board.columns[0]?.id ?? 0);
  const [description, setDescription] = useState('');
  const [mode, setMode] = useState<'single' | 'multiple'>('single');
  const [count, setCount] = useState(2);

  const generateTaskMutation = useMutation({
    mutationFn: async () => {
      const response = await TaskControllerApiFactory().generateTask({
        columnId,
        position: 1,
        description,
      });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board', board.id] });
      onNew?.();
    },
  });

  const generateMultipleTasksMutation = useMutation({
    mutationFn: async () => {
      const response = await TaskControllerApiFactory().generateMultipleTasks({
        columnId,
        description,
        count,
      });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['board', board.id] });
      onNew?.();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!columnId || !description.trim()) return;
    if (mode === 'single') {
      generateTaskMutation.mutate();
    } else {
      generateMultipleTasksMutation.mutate();
    }
  };

  return (
    <section
      style={{
        width: '100%',
        minHeight: '15vh',
        background: '#f8fafc',
        borderRadius: '8px',
        padding: '2rem',
        marginBottom: '2rem',
        boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
      }}
      className="flex flex-col justify-center"
    >
      <form
        onSubmit={handleSubmit}
        className="flex flex-col md:flex-row items-center justify-between gap-4 w-full"
      >
        <label className="flex flex-col">
          Column
          <select
            className="retro-input"
            value={columnId}
            onChange={e => setColumnId(Number(e.target.value))}
            required
          >
            {board.columns.map(col => (
              <option key={col.id} value={col.id}>{col.name}</option>
            ))}
          </select>
        </label>
        <RetroInput
          label="Description"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <label className="flex flex-col ">
          Mode
          <select
            className="retro-input"
            value={mode}
            onChange={e => setMode(e.target.value as 'single' | 'multiple')}
          >
            <option value="single">Single</option>
            <option value="multiple">Multiple</option>
          </select>
        </label>
        {mode === 'multiple' && (
          <RetroInput
            label="How many?"
            type="number"
            min={2}
            value={count}
            onChange={e => setCount(Number(e.target.value))}
            required
          />
        )}
        <div className="flex gap-2 mt-4 md:mt-0">
          <RetroButton
            size="sm"
            type="submit"
            disabled={generateTaskMutation.isPending || generateMultipleTasksMutation.isPending}
          >
            {(generateTaskMutation.isPending || generateMultipleTasksMutation.isPending)
              ? 'Generating...'
              : 'Generate'}
          </RetroButton>
        </div>
      </form>
    </section>
  );
};
