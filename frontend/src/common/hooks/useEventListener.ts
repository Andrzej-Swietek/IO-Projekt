import { useEffect, useRef } from 'react';

export const useEventListener = (
  eventType: string,
  callback: (e: Event) => void,
  element: EventTarget = window,
): void => {
  const callbackRef = useRef(callback);

  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);

  useEffect(() => {
    if (element === null) return;
    const handler = (e: Event) => callbackRef.current(e);
    element.addEventListener(eventType, handler);

    // eslint-disable-next-line consistent-return
    return () => element.removeEventListener(eventType, handler);
  }, [eventType, element]);
};
