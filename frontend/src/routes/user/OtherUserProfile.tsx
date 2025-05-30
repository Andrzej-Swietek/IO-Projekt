import { FC, ReactNode, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { CheckCircle2, User, XCircle } from 'lucide-react';

import { cn } from '@/lib/utils.ts';
import { ProfileItem } from '@components/profiles/ProfileItem.tsx';
import { UserControllerApiFactory, UserRepresentation } from '@/api';

interface UserProfileProps {}

const getUserById = async (id: string): Promise<UserRepresentation> => {
  const api = UserControllerApiFactory();
  const response = await api.getUserDetails(id);
  return response.data;
};

export const OtherUserProfile: FC<UserProfileProps> = () => {
  const { id } = useParams();
  const [profile, setProfile] = useState<UserRepresentation | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    getUserById(id)
      .then(data => {
        setProfile(data);
        setError(false);
      })
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading)
    return (
      <div className="flex flex-col items-center justify-center h-screen">
        <RetroBox>Ładowanie profilu użytkownika...</RetroBox>
      </div>
    );
  if (error || !profile)
    return (
      <div className="flex flex-col items-center justify-center h-screen">
        <RetroBox error>
          Błąd ładowania profilu użytkownika
        </RetroBox>
      </div>
    );

  const {
    username,
    firstName,
    lastName,
    email,
    emailVerified,
    createdTimestamp,
    enabled,
    realmRoles,
    groups,
    attributes,
  } = profile;

  const formatDate = (timestamp?: number) =>
    timestamp ? new Date(timestamp).toLocaleString() : '—';

  return (
    <div className="flex flex-col items-center justify-center h-[98vh]">
      <RetroBox>
        <h2 className="text-xl rounded-xl flex row items-center gap-4 font-bold bg-[var(--primary-blue)]
         border border-black !px-4 !py-4 !mb-8"
        >
          <User className="w-10 h-10" />
          Inny użytkownik
        </h2>
        <ul className="space-y-2 text-sm font-mono">
          <ProfileItem label="Username" value={username} />
          <ProfileItem label="Imię i nazwisko" value={`${firstName} ${lastName}`} />
          <ProfileItem label="Email" value={email} />
          <ProfileItem
            label="Email zweryfikowany"
            value={
              emailVerified
                ? (
                  <CheckCircle2 className="text-green-600 inline-block w-5 h-5" />
                )
                : (
                  <XCircle className="text-red-600 inline-block w-5 h-5" />
                )
            }
          />
          {' '}
          <ProfileItem
            label="Konto aktywne"
            value={
              enabled
                ? (
                  <CheckCircle2 className="text-green-600 inline-block w-5 h-5" />
                )
                : (
                  <XCircle className="text-red-600 inline-block w-5 h-5" />
                )
            }
          />
          <ProfileItem label="Utworzono" value={formatDate(createdTimestamp)} />
          <ProfileItem label="Role (realm)" value={realmRoles?.join(', ') || '—'} />
          <ProfileItem label="Grupy" value={groups?.join(', ') || '—'} />

          {attributes && Object.keys(attributes).length > 0 && (
            <li>
              <div className="font-bold">Atrybuty:</div>
              <ul className="ml-4 list-disc text-[var(--primary-blue)]">
                {Object.entries(attributes).map(([key, val]) => (
                  <li key={key}>
                    <span className="font-semibold">
                      {key}
                      :
                    </span>
                    {val.join(', ')}
                  </li>
                ))}
              </ul>
            </li>
          )}
        </ul>
      </RetroBox>
    </div>
  );
};

const RetroBox: FC<{ children: ReactNode; error?: boolean }> = ({ children, error }) => (
  <div
    className={cn('w-3/4 lg:w-1/2 mx-auto !my-8 !p-16 rounded-xl border border-black bg-[var(--secondary-blue)]',
      `shadow-[4px_4px_0px_0px_#808080] ${
        error ? 'bg-[var(--primary-red)] text-white' : 'text-black'
      }`)}
  >
    {children}
  </div>
);
