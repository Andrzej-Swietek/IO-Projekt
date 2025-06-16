import { FC, useState, FormEvent, ChangeEvent, useEffect } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { TaskControllerApiFactory, TaskRequest, BoardControllerApiFactory, Board, Label, TeamControllerApiFactory, TeamMember, Task } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';
import { useUserProfile } from '@context/UserProfileProvider';
import { useUsersByIds } from '@/common/hooks/useUsersByIds';

interface AddTaskModalProps {
  onClose: () => void;
  columnId: number;
  boardId: number;
  teamId: number;
  task?: Task;
  isEdit?: boolean;
}

export const AddTaskModal: FC<AddTaskModalProps> = ({ onClose, columnId, boardId, teamId, task, isEdit }) => {
  const queryClient = useQueryClient();
  const { profile } = useUserProfile();
  const userId = profile?.id;
  const [title, setTitle] = useState(task?.title || '');
  const [description, setDescription] = useState(task?.description || '');
  const [priority, setPriority] = useState(1);
  const [labelIds, setLabelIds] = useState<number[]>(task?.labels ? Array.from(task.labels).map(l => l.id!) : []);
  const [assignees, setAssignees] = useState<string[]>(task?.assignees || []);

  useEffect(() => {
    if (isEdit && task) {
      setTitle(task.title || '');
      setDescription(task.description || '');
      setLabelIds(task.labels ? Array.from(task.labels).map(l => l.id!) : []);
      setAssignees(task.assignees || []);
    }
  }, [isEdit, task]);

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
  const { data: usersById } = useUsersByIds(teamMembers.map(m => m.userId!));

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

  const updateTaskMutation = useMutation({
    mutationFn: async () => {
      if (!task?.id) throw new Error('No task to update');
      const taskRequest: TaskRequest = {
        title,
        description,
        columnId,
        priority,
        position: task.position,
        status: task.status,
        labelIds,
        assignees,
      };
      const response = await TaskControllerApiFactory().updateTask(task.id, taskRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries();
      if (userId) queryClient.invalidateQueries({ queryKey: ['my-tasks', userId] });
      onClose();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (isEdit) {
      updateTaskMutation.mutate();
    } else {
      createTaskMutation.mutate();
    }
  };

  return (
    <RetroModal onClose={onClose} title={isEdit ? 'Edit Task' : 'Add Task'}>
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
            {(teamMembers || []).map(member => {
              const user = usersById?.[member.userId!];
              const name = user ? `${user.firstName || ''} ${user.lastName || ''}`.trim() : member.userId;
              return (
                <option key={member.userId} value={member.userId}>
                  {name} {member.role ? `(${member.role})` : ''}
                </option>
              );
            })}
          </select>
        </div>
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose} icon={null}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={isEdit ? updateTaskMutation.isPending : createTaskMutation.isPending}>
            {isEdit
              ? (updateTaskMutation.isPending ? 'Saving...' : 'Save Changes')
              : (createTaskMutation.isPending ? 'Creating...' : 'Add Task')}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
}; 