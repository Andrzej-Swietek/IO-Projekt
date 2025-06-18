import { ChangeEvent, FC, FormEvent, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TeamControllerApiFactory } from '@/api';
import { useUserProfile } from '@context/UserProfileProvider';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';

interface CreateTeamModalProps {
  onClose: () => void;
}

export const CreateTeamModal: FC<CreateTeamModalProps> = ({ onClose }) => {
  const { profile } = useUserProfile();
  const queryClient = useQueryClient();
  const [teamName, setTeamName] = useState('');
  const [description, setDescription] = useState('');

  const createTeamMutation = useMutation({
    mutationFn: async () => {
      const response = await TeamControllerApiFactory().createTeam({
        name: teamName,
        description: description,
        creatorId: profile?.id ?? '',
      });
      return response.data;
    },
    onSuccess: async team => {
      queryClient.invalidateQueries({ queryKey: ['my-teams'] });
      onClose();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    createTeamMutation.mutate();
  };

  return (
    <RetroModal onClose={onClose} title="Create New Team">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="Team Name"
          value={teamName}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setTeamName(e.target.value)}
          required
        />
        <RetroInput
          label="Description"
          value={description}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
          required
        />
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={createTeamMutation.isPending}>
            {createTeamMutation.isPending ? 'Creating...' : 'Create Team'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
};
