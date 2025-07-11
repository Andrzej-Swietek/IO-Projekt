import { FC, FormEvent, useMemo, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Board, TaskControllerApiFactory } from '@/api';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';
import { RetroSelect } from '@components/common/RetroSelect';
import { RetroTextArea } from '@components/common/RetroTextArea';
import { Zap } from 'lucide-react';
import { useTranslation } from 'react-i18next';

interface GenerateWithAIBoxProps {
  board: Board;
  onNew?: () => void;
}

export const GenerateWithAIBox: FC<GenerateWithAIBoxProps> = ({
  board,
  onNew,
}) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [columnId, setColumnId] = useState<number>(board?.columns?.[0]?.id ?? 0);
  const [description, setDescription] = useState('');
  const [mode, setMode] = useState<'single' | 'multiple'>('single');
  const [count, setCount] = useState(2);

  const columnOptions = useMemo(
    () =>
      (board?.columns || []).map(col => ({
        value: col.id?.toString() ?? '',
        label: col.name ?? '',
      })),
    [board?.columns],
  );

  const modeOptions = [
    { value: 'single', label: t('generate.mode.single') },
    { value: 'multiple', label: t('generate.mode.multiple') },
  ];

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
      queryClient.invalidateQueries({ queryKey: ['board'] });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
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

  if (!board?.columns?.length) {
    return (
      <section
        className="w-full min-h-[15vh] bg-yellow-50 border-4 p-8 mb-8 retro-shadow font-mono flex items-center justify-center"
      >
        <span className="text-black font-mono">
          {t('generate.noColumns')}
        </span>
      </section>
    );
  }

  return (
    <section className="w-full min-h-[15vh] bg-yellow-50 border-4 p-8 mb-8 retro-shadow font-mono">
      <form
        onSubmit={handleSubmit}
        className="grid grid-cols-1 md:grid-cols-5 gap-6 items-end"
      >
        <RetroSelect
          label={t('generate.selectColumn')}
          value={columnId.toString()}
          onChange={e => setColumnId(Number(e.target.value))}
          options={columnOptions}
          required
        />
        <RetroSelect
          label={t('generate.selectMode')}
          value={mode}
          onChange={e => setMode(e.target.value as 'single' | 'multiple')}
          options={modeOptions}
          required
        />
        {mode === 'multiple' && (
          <RetroInput
            label={t('generate.howMany')}
            type="number"
            min={2}
            value={count}
            onChange={e => setCount(Number(e.target.value))}
            required
          />
        )}
        <div className="flex gap-2">
          <RetroButton
            size="sm"
            type="submit"
            icon={<Zap className="w-4 h-4" />}
            disabled={generateTaskMutation.isPending || generateMultipleTasksMutation.isPending}
          >
            {(generateTaskMutation.isPending || generateMultipleTasksMutation.isPending)
              ? t('generate.generating')
              : t('generate.generate')}
          </RetroButton>
        </div>
        <div className="w-full col-span-3 mt-8">
          <RetroTextArea
            label={t('generate.description')}
            value={description}
            onChange={e => setDescription(e.target.value)}
            required
            rows={3}
          />
        </div>
      </form>
    </section>
  );
};
