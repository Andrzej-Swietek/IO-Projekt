import { FC } from 'react';
import { useQuery } from '@tanstack/react-query';
import { TeamControllerApiFactory } from '@/api';

interface HomeProps {
}

const fetchTeams = async () => {
  const api = TeamControllerApiFactory();
  const response = await api.getAllTeams({});
  return response.data;
};

export const Home: FC<HomeProps> = () => {
  const { data, error, isLoading, isError } = useQuery({
    queryKey: ['teams'],
    queryFn: fetchTeams,
  });


  if (isError) {
    return <div>Loading...</div>;
  }

  if (isLoading) {
    return <div>Loading...</div>;
  }


  return (
    <div className="flex flex-col items-center justify-center h-screen">
      <h1 className="text-4xl font-bold">Welcome to the Home Page</h1>
      <p className="mt-4 text-lg">This is the main page of our application.</p>
      {
        data?.data && data.data[0].name
      }
      {
        isError && <>{error}</>
      }
    </div>
  );
};
