import { FC, useEffect, useState } from 'react';
import { RetroContainer } from '@components/common/RetroContainer';
import { RetroButton } from '@components/common/RetroButton';
import { useUserProfile } from '@context/UserProfileProvider';
import { Moon, Sun, UserRoundPen } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const THEME_KEY = 'kanban_theme';
const COLOR_KEY = 'kanban_primary_color';

const PALETTE = [
  { name: 'Blue', value: '#3b82f6' },
  { name: 'Green', value: '#22c55e' },
  { name: 'Red', value: '#ef4444' },
  { name: 'Purple', value: '#a21caf' },
  { name: 'Orange', value: '#f59e42' },
];

export const Settings: FC = () => {
  const { profile } = useUserProfile();
  const navigate = useNavigate();
  const { i18n, t } = useTranslation();

  const [theme, setTheme] = useState<'light' | 'dark'>(
    (localStorage.getItem(THEME_KEY) as 'light' | 'dark') || 'light',
  );
  const [primaryColor, setPrimaryColor] = useState<string>(
    localStorage.getItem(COLOR_KEY) || PALETTE[0].value,
  );

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    document.documentElement.setAttribute('class', theme);
    localStorage.setItem(THEME_KEY, theme);
    document.documentElement.style.setProperty('--primary', primaryColor);
    localStorage.setItem(COLOR_KEY, primaryColor);
  }, [theme, primaryColor]);

  const keycloakAccountUrl = `${import.meta.env.VITE_KEYCLOAK_URL}/realms/${import.meta.env.VITE_KEYCLOAK_REALM}/account`;

  const handleChangeLanguage = (lang: string) => {
    i18n.changeLanguage(lang);
  };

  return (
    <>
      <div className="w-full h-[125px] !px-32 !pt-12 !pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] leading-[100%] tracking-[0%] align-bottom text-[var(--primary-black)]">
          {t('settings.hello')}
          {' '}
          {profile?.firstName}
          {' '}
          {profile?.lastName}
        </h1>
      </div>
      <div className="flex flex-col items-center justify-center min-h-[80vh]">
        <RetroContainer className="w-full lg:w-[80%] !px-12 !py-10 flex flex-col gap-8">
          <h2 className="text-2xl font-bold mb-4">{t('settings.title')}</h2>
          <div className="flex flex-col gap-4">
            <SettingRow>
              <>
                <div
                  className="font-bold text-md uppercase tracking-wider text-black flex items-center justify-center"
                >
                  {t('settings.keycloak')}
                </div>
                <RetroButton
                  className="ml-4"
                  icon={<UserRoundPen />}
                  onClick={() => window.open(keycloakAccountUrl, '_blank')}
                >
                  {t('settings.manageAccount')}
                </RetroButton>
              </>
            </SettingRow>
            <SettingRow>
              <>
                <div
                  className="font-bold text-md uppercase tracking-wider text-black flex items-center justify-center"
                >
                  {t('settings.labels')}
                </div>
                <RetroButton
                  className="ml-4"
                  icon={<UserRoundPen />}
                  onClick={() => navigate('/management/labels')}
                >
                  {t('settings.manageLabels')}
                </RetroButton>
              </>
            </SettingRow>
            <SettingRow>
              <>
                <span
                  className="font-bold text-md uppercase tracking-wider text-black flex items-center justify-center"
                >
                  {t('settings.theme')}
                </span>
                <div className="inline-flex ml-4 gap-2">
                  <RetroButton
                    icon={<Sun />}
                    variant={theme === 'light' ? 'primary' : 'secondary'}
                    onClick={() => setTheme('light')}
                  >
                    {t('settings.light')}
                  </RetroButton>
                  <RetroButton
                    icon={<Moon />}
                    variant={theme === 'dark' ? 'primary' : 'secondary'}
                    onClick={() => setTheme('dark')}
                  >
                    {t('settings.dark')}
                  </RetroButton>
                </div>
              </>
            </SettingRow>
            <SettingRow>
              <>
                <span
                  className="font-bold text-md mb-2 mt-2 uppercase tracking-wider text-black flex items-center justify-center"
                >
                  {t('settings.primaryColor')}
                </span>
                <div className="inline-flex ml-4 gap-2">
                  {PALETTE.map(color => (
                    <button
                      key={color.value}
                      className="w-8 h-8 cursor-pointer retro-shadow border-4 transition-all"
                      style={{
                        borderRadius: primaryColor === color.value ? '50%' : '0',
                        background: color.value,
                        borderColor: primaryColor === color.value ? 'red' : '#d1d5db',
                      }}
                      onClick={() => setPrimaryColor(color.value)}
                      aria-label={color.name}
                      value={color}
                    />
                  ))}
                </div>
              </>
            </SettingRow>
            <SettingRow>
              <>
                <span
                  className="font-bold text-md uppercase tracking-wider text-black flex items-center justify-center"
                >
                  {t('settings.language')}
                </span>
                <div className="inline-flex ml-4 gap-2">
                  <RetroButton
                    onClick={() => handleChangeLanguage('en')}
                    variant={i18n.language === 'en' ? 'primary' : 'secondary'}
                  >
                    EN
                  </RetroButton>
                  <RetroButton
                    onClick={() => handleChangeLanguage('pl')}
                    variant={i18n.language === 'pl' ? 'primary' : 'secondary'}
                  >
                    PL
                  </RetroButton>
                </div>
              </>
            </SettingRow>
          </div>
        </RetroContainer>
      </div>
    </>
  );
};

const SettingRow: FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <div className="w-full flex items-center justify-between flex-row border-b-2 pb-4">
      {children}
    </div>
  );
};
