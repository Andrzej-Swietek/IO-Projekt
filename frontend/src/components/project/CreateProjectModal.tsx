import { ChangeEvent, FC, FormEvent, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ProjectControllerApiFactory, ProjectRequest } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';

interface CreateProjectModalProps {
  onClose: () => void;
  teamId: number;
}

export const CreateProjectModal: FC<CreateProjectModalProps> = ({ onClose, teamId }) => {
  const queryClient = useQueryClient();
  const [projectName, setProjectName] = useState('');
  const [description, setDescription] = useState('');

  const createProjectMutation = useMutation({
    mutationFn: async () => {
      const projectRequest: ProjectRequest = {
        name: projectName,
        description: description,
        teamId: teamId,
      };
      const response = await ProjectControllerApiFactory().createProject(projectRequest);
      return response.data;
    },
    onSuccess: async project => {
      queryClient.invalidateQueries({ queryKey: ['team-projects', teamId] });
      onClose();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    createProjectMutation.mutate();
  };

  return (
    <RetroModal onClose={onClose} title="Create New Project">
      <form onSubmit={handleSubmit} className="flex flex-col gap-6">
        <RetroInput
          label="Project Name"
          value={projectName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setProjectName(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose} variant="secondary" icon={null}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={createProjectMutation.isPending}>
            {createProjectMutation.isPending ? 'Creating...' : 'Create Project'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
};
