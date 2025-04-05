import { useEffect, useRef } from 'react';

export const useUpdateEffect = (callback: () => void, deps: React.DependencyList): void => {
  const firstRenderRef = useRef(true);

  useEffect(() => {
    if (firstRenderRef.current) {
      firstRenderRef.current = false;
      return;
    }
    callback();
  }, deps);
};
