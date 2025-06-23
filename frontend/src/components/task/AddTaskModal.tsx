import { ChangeEvent, FC, FormEvent, useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Label,
  LabelControllerApiFactory,
  LabelRequest,
  Task,
  TaskControllerApiFactory,
  TaskRequest,
  TeamControllerApiFactory,
  TeamMember,
} from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';
import { useUserProfile } from '@context/UserProfileProvider';
import { useUsersByIds } from '@/common/hooks/useUsersByIds';
import { RetroMultiSelect } from '@components/common/RetroMultiSelect';
import { RetroColorPicker } from '@components/common/RetroColorPicker';

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

  const [showAddLabel, setShowAddLabel] = useState(false);
  const [newLabelName, setNewLabelName] = useState('');
  const [newLabelColor, setNewLabelColor] = useState('#000000');

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
      const response = await new TeamControllerApiFactory().getTeamById(teamId);
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
      const response = await new LabelControllerApiFactory().getAllLabels();
      return response.data;
    },
    enabled: !!boardId,
  });

  const createTaskMutation = useMutation({
    mutationFn: async () => {
      console.log('Creating task with assignees:', assignees);
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
      console.log('Task request:', taskRequest);
      const response = await TaskControllerApiFactory().createTask(taskRequest);
      console.log('Task creation response:', response.data);
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
      console.log('Updating task with assignees:', assignees);
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
      console.log('Task update request:', taskRequest);
      const response = await TaskControllerApiFactory().updateTask(task.id, taskRequest);
      console.log('Task update response:', response.data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries();
      if (userId) queryClient.invalidateQueries({ queryKey: ['my-tasks', userId] });
      onClose();
    },
  });

  const addLabelMutation = useMutation({
    mutationFn: async () => {
      const api = LabelControllerApiFactory();
      const req: LabelRequest = {
        name: newLabelName,
        color: newLabelColor,
        // TaskId: task?.id
      };
      const response = await api.createLabel(req);
      return response.data;
    },
    onSuccess: createdLabel => {
      setShowAddLabel(false);
      setNewLabelName('');
      setNewLabelColor('#000000');

      if (createdLabel?.id) {
        setLabelIds(prev => [...prev, createdLabel.id!]);
      }

      queryClient.invalidateQueries({ queryKey: ['project-labels', boardId] });
    },
  });


  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    console.log('Form submitted with assignees:', assignees);
    if (isEdit) {
      updateTaskMutation.mutate();
    } else {
      createTaskMutation.mutate();
    }
  };

  // Add log for assignee selection
  const handleAssigneeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    const selectedAssignees = Array.from(e.target.selectedOptions, o => o.value);
    console.log('Selected assignees:', selectedAssignees);
    setAssignees(selectedAssignees);
  };

  return (
    <RetroModal onClose={onClose} title={isEdit ? 'Edit Task' : 'Add Task'}>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="Title"
          value={title}
          inputColor="bg-yellow-50"
          onChange={(e: ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          value={description}
          inputColor="bg-yellow-50"
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <RetroInput
          label="Priority"
          type="number"
          inputColor="bg-yellow-50"
          value={priority}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setPriority(Number(e.target.value))}
        />

        <div className="flex items-start gap-4">
          <div className="flex-1">
            <RetroMultiSelect
              label="Labels"
              className="min-w-[50%] w-auto"
              options={labels?.map(label => ({
                label: label.name,
                value: label.id,
              })) || []}
              value={labelIds}
              onChange={selected => setLabelIds(selected.map(Number))}
            />
          </div>
          <div className="self-end">
            <RetroButton
              variant="secondary"
              className="retro-shadow"
              type="button"
              size="md"
              onClick={() => setShowAddLabel(!showAddLabel)}
            >
              New
            </RetroButton>
          </div>
        </div>

        {showAddLabel && (
          <div className="border border-black bg-white/90 p-4 space-y-4 retro-shadow">
            <RetroInput
              label="New Label Name"
              inputColor="bg-yellow-50"
              value={newLabelName}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setNewLabelName(e.target.value)}
            />
            <RetroColorPicker
              label="Color"
              value={newLabelColor}
              onChange={e => setNewLabelColor(e.target.value)}
            />
            <div className="flex gap-2">
              <RetroButton
                variant="secondary"
                size="sm"
                type="button"
                onClick={() => addLabelMutation.mutate()}
              >
                Save
              </RetroButton>
              <RetroButton
                variant="secondary"
                size="sm"
                type="button"
                icon={null}
                onClick={() => setShowAddLabel(false)}
              >
                Cancel
              </RetroButton>
            </div>
          </div>
        )}
        {/* Assignees */}
        <div>
          <RetroMultiSelect
            label="Assignees"
            className="min-w-[50%] w-auto"
            options={[
              ...teamMembers.map(member => {
                const user = usersById?.[member.userId!];
                const name = user ? `${user.firstName || ''} ${user.lastName || ''}`.trim() : member.userId;
                return {
                  label: `${name} ${member.role ? `(${member.role})` : ''}`,
                  value: member.userId,
                };
              }) || [],
            ]}
            value={assignees}
            onChange={selected => {
              setAssignees([...selected]);
            }}
          />
        </div>
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose} icon={null}>
            Cancel
          </RetroButton>
          <RetroButton
            size="sm"
            type="submit"
            disabled={isEdit ? updateTaskMutation.isPending : createTaskMutation.isPending}
          >
            {isEdit
              ? (updateTaskMutation.isPending ? 'Saving...' : 'Save Changes')
              : (createTaskMutation.isPending ? 'Creating...' : 'Add Task')}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
};
