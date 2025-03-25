import { useCallback, useState } from 'react';

type UseCookieResult<T> = [string | null, (newValue: string, options?: Record<string, T>) => void, () => void];

export const useCookie = <T>(
  name: string,
  defaultValue: string,
): UseCookieResult<T> => {
  const [value, setValue] = useState<string | null>(() => {
    const cookie = document.cookie.split('; ').find(row => row.startsWith(name));
    if (cookie) return cookie.split('=')[1];
    document.cookie = `${name}=${defaultValue}`;
    return defaultValue;
  });

  const updateCookie = useCallback((newValue: string, options?: Record<string, T>) => {
    document.cookie = `${name}=${newValue}; ${Object.entries(options || {})
      .map(([key, v]) => `${key}=${v}`).join('; ')}`;
    setValue(newValue);
  }, [name]);

  const deleteCookie = useCallback(() => {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT`;
    setValue(null);
  }, [name]);

  return [value, updateCookie, deleteCookie];
};
