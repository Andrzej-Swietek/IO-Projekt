import { FC, useState, FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { TeamControllerApiFactory, TeamMemberRequest, TeamMemberDTO, TeamMemberDTORoleEnum, UserControllerApiFactory, UserRepresentation, Team } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroSelect } from '@components/common/RetroSelect';
import { RetroButton } from '@components/common/RetroButton';

interface AddMemberModalProps {
  onClose: () => void;
  teamId: number;
}

export const AddMemberModal: FC<AddMemberModalProps> = ({ onClose, teamId }) => {
  const queryClient = useQueryClient();
  const [selectedUserId, setSelectedUserId] = useState('');
  const [role, setRole] = useState<TeamMemberDTORoleEnum>(TeamMemberDTORoleEnum.Member);

  // Fetch current team
  const { data: team, isLoading: isLoadingTeam } = useQuery({
    queryKey: ['team', teamId],
    queryFn: async () => {
      const response = await TeamControllerApiFactory().getTeamById(teamId);
      return response.data;
    }
  });

  // Fetch all users
  const { data: users, isLoading: isLoadingUsers } = useQuery({
    queryKey: ['users'],
    queryFn: async () => {
      const response = await UserControllerApiFactory().getAllUsers();
      return response.data;
    }
  });

  const addMemberMutation = useMutation({
    mutationFn: async () => {
      const teamMemberRequest: TeamMemberRequest = {
        teamId: teamId,
        teamMember: {
          userId: selectedUserId,
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
    if (!selectedUserId) return;
    addMemberMutation.mutate();
  };

  // Get current team member IDs
  const currentMemberIds = new Set(team?.members?.map(member => member.userId) || []);

  // Filter out users who are already team members
  const availableUsers = users?.filter(user => !currentMemberIds.has(user.id!)) || [];

  const userOptions = availableUsers.map(user => ({
    value: user.id!,
    label: `${user.firstName || ''} ${user.lastName || ''} (${user.email || user.username || ''})`
  }));

  const isLoading = isLoadingUsers || isLoadingTeam;

  return (
    <RetroModal onClose={onClose} title="Add Team Member">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <RetroSelect
          label="User"
          value={selectedUserId}
          onChange={(e) => setSelectedUserId(e.target.value)}
          options={userOptions}
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
          <RetroButton size="sm" type="submit" disabled={addMemberMutation.isPending || isLoading || userOptions.length === 0}>
            {addMemberMutation.isPending ? 'Adding...' : 'Add Member'}
          </RetroButton>
        </div>
      </form>
    </RetroModal>
  );
}; 