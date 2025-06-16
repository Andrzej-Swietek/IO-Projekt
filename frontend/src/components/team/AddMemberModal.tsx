import { FC, useState, FormEvent } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { TeamControllerApiFactory, TeamMemberRequest, TeamMemberDTO, TeamMemberDTORoleEnum } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroButton } from '@components/common/RetroButton';
import { RetroSelect } from '@components/common/RetroSelect';

interface AddMemberModalProps {
  onClose: () => void;
  teamId: number;
}

export const AddMemberModal: FC<AddMemberModalProps> = ({ onClose, teamId }) => {
  const queryClient = useQueryClient();
  const [userId, setUserId] = useState('');
  const [role, setRole] = useState<TeamMemberDTORoleEnum>(TeamMemberDTORoleEnum.Member);

  const addMemberMutation = useMutation({
    mutationFn: async () => {
      const teamMemberRequest: TeamMemberRequest = {
        teamId: teamId,
        teamMember: {
          userId: userId,
          role: role
        }
      };
      const response = await TeamControllerApiFactory().addTeamMember(teamMemberRequest);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['team', teamId] });
      onClose();
    },
  });

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (!userId.trim()) return;
    addMemberMutation.mutate();
  };

  return (
    <RetroModal onClose={onClose} title="Add Team Member">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroInput
          label="User ID"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          required
        />
        <RetroSelect
          label="Role"
          value={role}
          onChange={(e) => setRole(e.target.value as TeamMemberDTORoleEnum)}
          options={[
            { value: TeamMemberDTORoleEnum.Member, label: 'Member' },
            { value: TeamMemberDTORoleEnum.Manager, label: 'Manager' },
            { value: TeamMemberDTORoleEnum.Admin, label: 'Admin' },
            { value: TeamMemberDTORoleEnum.Owner, label: 'Owner' }
          ]}
        />
        <div className="flex justify-end gap-4 mt-4">
          <RetroButton size="sm" type="button" onClick={onClose} variant="secondary" icon={null}>
            Cancel
          </RetroButton>
          <RetroButton size="sm" type="submit" disabled={addMemberMutation.isPending}>
            {addMemberMutation.isPending ? 'Adding...' : 'Add Member'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
}; 