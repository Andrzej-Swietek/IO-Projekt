import { useCallback, useEffect, useState } from 'react';

interface UseAsyncResult<T> {
  loading: boolean;
  error?: Error;
  value?: T;
}

export const useAsync = <T>(callback: () => Promise<T>, dependencies: object[] = []): UseAsyncResult<T> => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | undefined>(undefined);
  const [value, setValue] = useState<T | undefined>(undefined);

  const callbackMemoized = useCallback(() => {
    setLoading(true);
    setError(undefined);
    setValue(undefined);
    callback()
      .then(setValue)
      .catch(setError)
      .finally(() => setLoading(false));
  }, dependencies);

  useEffect(() => {
    callbackMemoized();
  }, [callbackMemoized]);

  return { loading, error, value };
};
