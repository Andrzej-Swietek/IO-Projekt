import { FC } from 'react';

interface AboutProps {
}

export const About: FC<AboutProps> = () => {
  return (
    <div className="flex flex-col items-center justify-center h-screen">
      <h1 className="text-4xl font-bold">About Page</h1>
      <p className="mt-4 text-lg">This is the about page of our application.</p>
    </div>
  );
};
