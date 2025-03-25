import { useEffect, useState } from 'react';

export const useMediaQuery = (mediaQuery: string): boolean => {
  const [isMatch, setIsMatch] = useState<boolean>(false);

  useEffect(() => {
    const mediaQueryList = window.matchMedia(mediaQuery);
    setIsMatch(mediaQueryList.matches);

    const listener = (e: MediaQueryListEvent) => setIsMatch(e.matches);
    mediaQueryList.addEventListener('change', listener);

    return () => mediaQueryList.removeEventListener('change', listener);
  }, [mediaQuery]);

  return isMatch;
};
