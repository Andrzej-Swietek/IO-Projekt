import { useEffect, useState } from 'react';

type GeolocationData = {
  latitude: number;
  longitude: number;
};

export const useGeolocation = (options?: PositionOptions): {
  loading: boolean;
  error?: GeolocationPositionError;
  data: GeolocationData;
} => {
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<GeolocationPositionError | undefined>();
  const [data, setData] = useState<GeolocationData>({ latitude: 0, longitude: 0 });

  useEffect(() => {
    const successHandler = (e: GeolocationPosition) => {
      setLoading(false);
      setError(undefined);
      setData({ latitude: e.coords.latitude, longitude: e.coords.longitude });
    };

    const errorHandler = (e: GeolocationPositionError) => {
      setError(e);
      setLoading(false);
    };

    navigator.geolocation.getCurrentPosition(successHandler, errorHandler, options);
    const id = navigator.geolocation.watchPosition(successHandler, errorHandler, options);

    return () => navigator.geolocation.clearWatch(id);
  }, [options]);

  return { loading, error, data };
};
