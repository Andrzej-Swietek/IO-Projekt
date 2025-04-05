import { useCallback, useEffect, useState } from 'react';

const useStorage = <T>(key: string, defaultValue: T, storageObject: Storage): [T | undefined, (value: T) => void, () => void] => {
  const [value, setValue] = useState<T | undefined>(() => {
    const storedValue = storageObject.getItem(key);
    if (storedValue !== null) return JSON.parse(storedValue) as T;
    return defaultValue;
  });

  useEffect(() => {
    if (value === undefined) return void storageObject.removeItem(key);
    storageObject.setItem(key, JSON.stringify(value));
  }, [value, key, storageObject]);

  const remove = useCallback(() => setValue(undefined), []);
  return [value, setValue, remove];
};

export const useLocalStorage = <T>(key: string, defaultValue: T): [T | undefined, (value: T) => void, () => void] =>
  useStorage(key, defaultValue, window.localStorage);

export const useSessionStorage = <T>(key: string, defaultValue: T): [T | undefined, (value: T) => void, () => void] =>
  useStorage(key, defaultValue, window.sessionStorage);
