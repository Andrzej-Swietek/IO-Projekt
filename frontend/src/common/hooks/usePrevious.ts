import { useRef } from 'react';

export const usePrevious = <T>(value: T): T | undefined => {
  const currentRef = useRef<T | undefined>(undefined);
  const previousRef = useRef<T | undefined>(undefined);

  if (currentRef.current !== value) {
    previousRef.current = currentRef.current;
    currentRef.current = value;
  }

  return previousRef.current;
};
