import { RefObject, useEffect } from 'react';

type UseClickOutsideCallback = (event: MouseEvent) => void;

export const useClickOutside = (ref: RefObject<HTMLElement>, cb: UseClickOutsideCallback): void => {
  useEffect(() => {
    const handleClick = (event: MouseEvent) => {
      if (ref.current === null || ref.current.contains(event.target as Node)) return;
      cb(event);
    };

    document.addEventListener('click', handleClick);
    return () => document.removeEventListener('click', handleClick);
  }, [ref, cb]);
};
