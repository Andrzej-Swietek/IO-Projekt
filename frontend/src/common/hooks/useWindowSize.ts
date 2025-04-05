import { useEventListener } from './useEventListener.ts';
import { useState } from 'react';

interface UseWindowSizeResult {
  width: number;
  height: number;
}

export const useWindowSize = (): UseWindowSizeResult => {
  const [windowSize, setWindowSize] = useState({
    width: window.innerWidth,
    height: window.innerHeight,
  });

  useEventListener('resize', () => {
    setWindowSize({ width: window.innerWidth, height: window.innerHeight });
  });

  return windowSize;
};
