import { FC, useState, FormEvent, ChangeEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { TaskControllerApiFactory, TaskRequest, BoardControllerApiFactory, Board, Label, TeamControllerApiFactory, TeamMember } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';

interface AddTaskModalProps {
  onClose: () => void;
  columnId: number;
  boardId: number;
  teamId: number;
}

export const AddTaskModal: FC<AddTaskModalProps> = ({ onClose, columnId, boardId, teamId }) => {
  const queryClient = useQueryClient();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState(1);
  const [labelIds, setLabelIds] = useState<number[]>([]);
  const [assignees, setAssignees] = useState<string[]>([]);

  // Fetch team members
  const { data: team } = useQuery({
    queryKey: ['team', teamId],
    queryFn: async () => {
      const response = await TeamControllerApiFactory().getTeamById(teamId);
      return response.data;
    },
    enabled: !!teamId,
  });
  const teamMembers: TeamMember[] = team?.members || [];

  // Fetch labels (placeholder, as before)
  const { data: labels } = useQuery<Label[]>({
    queryKey: ['project-labels', boardId],
    queryFn: async () => {
      // Placeholder: fetch labels for the board/project if available
      return [];
    },
    enabled: !!boardId,
  });

  const createTaskMutation = useMutation({
    mutationFn: async () => {
      const taskRequest: TaskRequest = {
        title,
        description,
        columnId,
        priority,
        position: 0, // Placeholder, backend should set correct position
        status: 'TODO',
        labelIds,
        assignees,
      };
      const response = await TaskControllerApiFactory().createTask(taskRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries();
      onClose();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    createTaskMutation.mutate();
  };

  return (
    <RetroModal onClose={onClose} title="Add Task">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="Title"
          value={title}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <RetroInput
          label="Priority"
          type="number"
          value={priority}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setPriority(Number(e.target.value))}
        />
        {/* Labels (placeholder) */}
        <div>
          <label className="mb-2 block text-sm font-medium text-gray-700">Labels</label>
          <select
            multiple
            value={labelIds.map(String)}
            onChange={e => setLabelIds(Array.from(e.target.selectedOptions, o => Number(o.value)))}
            className="w-full rounded-md border border-gray-300 px-3 py-2"
          >
            {(labels || []).map(label => (
              <option key={label.id} value={label.id}>{label.name}</option>
            ))}
          </select>
        </div>
        {/* Assignees */}
        <div>
          <label className="mb-2 block text-sm font-medium text-gray-700">Assignees</label>
          <select
            multiple
            value={assignees}
            onChange={e => setAssignees(Array.from(e.target.selectedOptions, o => o.value))}
            className="w-full rounded-md border border-gray-300 px-3 py-2"
          >
            {(teamMembers || []).map(member => (
              <option key={member.userId} value={member.userId}>{member.userId} ({member.role})</option>
            ))}
          </select>
        </div>
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={createTaskMutation.isPending}>
            {createTaskMutation.isPending ? 'Creating...' : 'Add Task'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
}; 