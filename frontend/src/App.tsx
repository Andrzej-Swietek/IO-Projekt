import { FC } from 'react';
import { createBrowserRouter, Outlet } from 'react-router';
import { RouterProvider } from 'react-router/dom';
import './App.css';

import { About } from '@routes/About.tsx';
import { Home } from '@routes/Home.tsx';
import { Login } from '@routes/auth/Login.tsx';
import { MainLayout } from '@layouts/MainLayout.tsx';
import { Register } from '@routes/auth/Register.tsx';
import { UserProfile } from '@routes/UserProfile.tsx';
import { BoardPage } from '@routes/Board.tsx';


interface Props {
}


const router = createBrowserRouter([
  {
    path: '/',
    element: (
      <MainLayout>
        <Outlet />
      </MainLayout>
    ),
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: 'about',
        element: <About />,
      },
      {
        path: 'auth',
        element: <Outlet />,
        children: [
          { path: 'login', element: <Login /> },
          { path: 'register', element: <Register /> },
        ],
      },
      {
        path: 'user/:id',
        element: <UserProfile />,
      },
      {
        path: 'board/:id',
        element: <BoardPage />,
      },
    ],
  },
]);

const App: FC<Props> = () => {
  return <RouterProvider router={router} />;
};

export default App;
