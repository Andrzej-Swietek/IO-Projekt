import { MoreVertical, TriangleAlert, User } from 'lucide-react';
import { Button } from '@components/ui/button.tsx';
import { FC } from 'react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@components/ui/dropdown-menu.tsx';
import { useKeycloak } from '@/context';
import { NavLink } from 'react-router-dom';
import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { useTranslation } from 'react-i18next';

interface RetroNavbarProps {
}

export const RetroNavbar: FC<RetroNavbarProps> = () => {
  const { keycloak, authenticated } = useKeycloak();
  const { profile, loading, error } = useUserProfile();
  const { t } = useTranslation();

  const logout = () => {
    keycloak?.logout();
  };

  return (
    <header
      className="bg-yellow-300 border-b border-yellow-400 h-[80px] w-full !px-8 !py-3 flex justify-between items-center"
    >
      {/* Logo */}
      <div className="flex items-center gap-2 px-8">
        <NavLink to="/">
          <div className="w-12 h-12 rounded flex items-center justify-center">
            <img src="/logo.png" alt="Logo" />
          </div>
        </NavLink>
        <NavLink to="/">
          <span className="text-3xl font-normal text-black">{t('navbar.logoLeft')}</span>
          <span className="text-4xl font-bold text-black">{t('navbar.logoRight')}</span>
        </NavLink>
      </div>

      {/* User Actions */}
      <div className="flex items-center gap-2">
        <Button variant="ghost" size="icon" className="hover:bg-yellow-400 w-12 h-12">
          <NavLink to="/alerts">
            <TriangleAlert className="!w-8 !h-8 text-black" />
          </NavLink>
        </Button>
        <Button variant="ghost" size="icon" className="hover:bg-yellow-400 w-12 h-12">
          <NavLink to="/user/my-profile">
            <User className="!w-8 !h-8 text-black" />
          </NavLink>
        </Button>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="hover:bg-yellow-400">
              <MoreVertical className="!w-8 !h-8 text-black" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem className="!px-8 !py-6">
              <NavLink to="/user/my-profile">
                {!loading && !error && profile?.email}
              </NavLink>
            </DropdownMenuItem>
            <DropdownMenuItem className="!px-8 !py-6">
              <NavLink to="/user/my-boards">
                  {t('navbar.myBoards')}
              </NavLink>
            </DropdownMenuItem>
            <DropdownMenuItem className="!px-8 !py-6">
              <NavLink to="/user/reports">
                Reports
              </NavLink>
            </DropdownMenuItem>
            <DropdownMenuItem className="!px-8 !py-6">
              <NavLink to="/user/settings">
                  {t('navbar.settings')}
              </NavLink>
            </DropdownMenuItem>
            <DropdownMenuItem className="!px-8 !py-6">{t('navbar.help')}</DropdownMenuItem>
            {authenticated
              ? (
                <DropdownMenuItem className="!px-8 !py-6" onClick={logout}>
                  {t('navbar.signOut')}
                </DropdownMenuItem>
              )
              : (
                <DropdownMenuItem className="!px-8 !py-6">
                  <NavLink to="/auth/login">{t('navbar.signIn')}</NavLink>
                </DropdownMenuItem>
              )}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
};
