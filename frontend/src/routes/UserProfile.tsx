import { useParams } from 'react-router-dom';
import { FC } from 'react';

interface UserProfileProps {
}

export const UserProfile: FC<UserProfileProps> = () => {
  const { id } = useParams();
  return (
    <div>
      User ID:
      {id}
    </div>
  );
};
