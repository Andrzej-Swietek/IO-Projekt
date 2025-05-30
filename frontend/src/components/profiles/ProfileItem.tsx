import { FC, ReactNode } from 'react';

interface ProfileItemProps {
  label: ReactNode | string;
  value?: ReactNode | string;
}

export const ProfileItem: FC<ProfileItemProps> = ({ label, value }) => (
  <li className="flex justify-between items-center row wrap px-8">
    <span className="font-semibold text-2xl">
      {label}
      :
    </span>
    {' '}
    <p className="text-xl">{value || '—'}</p>
  </li>
);
