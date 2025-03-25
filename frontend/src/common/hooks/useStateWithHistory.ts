import { useCallback, useRef, useState } from 'react';

type UseStateWithHistoryReturn<T> = [
  T,
  (value: T | ((prevValue: T) => T)) => void,
  {
    history: T[];
    pointer: number;
    back: () => void;
    forward: () => void;
    go: (index: number) => void;
  },
];

const DEFAULT_MAX_CAPACITY = 10;

export const useStateWithHistory = <T>(
  defaultValue: T, { capacity = DEFAULT_MAX_CAPACITY }: { capacity?: number } = {},
): UseStateWithHistoryReturn<T> => {
  const [value, setValue] = useState<T>(defaultValue);
  const historyRef = useRef<T[]>([value]);
  const pointerRef = useRef<number>(0);

  const set = useCallback((v: T | ((prevValue: T) => T)) => {
    const resolvedValue = v instanceof Function ? v(value) : v;

    if (historyRef.current[pointerRef.current] !== resolvedValue) {
      if (pointerRef.current < historyRef.current.length - 1) {
        historyRef.current.splice(pointerRef.current + 1);
      }
      historyRef.current.push(resolvedValue);

      while (historyRef.current.length > capacity) {
        historyRef.current.shift();
      }

      pointerRef.current = historyRef.current.length - 1;
    }
    setValue(resolvedValue);
  }, [capacity, value]);

  const back = useCallback(() => {
    if (pointerRef.current <= 0) return;
    pointerRef.current--;
    setValue(historyRef.current[pointerRef.current]);
  }, []);

  const forward = useCallback(() => {
    if (pointerRef.current >= historyRef.current.length - 1) return;
    pointerRef.current++;
    setValue(historyRef.current[pointerRef.current]);
  }, []);

  const go = useCallback((index: number) => {
    if (index < 0 || index >= historyRef.current.length) return;
    pointerRef.current = index;
    setValue(historyRef.current[pointerRef.current]);
  }, []);

  return [
    value,
    set,
    {
      history: historyRef.current,
      pointer: pointerRef.current,
      back,
      forward,
      go,
    },
  ];
};
