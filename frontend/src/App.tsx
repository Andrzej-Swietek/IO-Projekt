import { FC } from 'react';
import { createBrowserRouter, Outlet } from 'react-router';
import { RouterProvider } from 'react-router/dom';

import { About } from '@routes/About.tsx';
import { Home } from '@routes/Home.tsx';
import { Login } from '@routes/auth/Login.tsx';
import { MainLayout } from '@layouts/MainLayout.tsx';
import { Register } from '@routes/auth/Register.tsx';
import { OtherUserProfile } from '@routes/user/OtherUserProfile.tsx';
import { BoardPage } from '@routes/Board.tsx';
import { UserProfileTab } from '@routes/user/UserProfile.tsx';
import { MyBoards } from '@routes/user/MyBoards.tsx';

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
        path: 'user',
        element: <Outlet />,
        children: [
          { path: 'my-profile', element: <UserProfileTab /> },
          { path: 'my-boards', element: <MyBoards /> },
          { path: ':id', element: <OtherUserProfile /> },
        ],
      },
      {
        path: 'board/:id',
        element: <BoardPage />,
      },
      {
        path: 'board/:id/:teamId',
        element: <BoardPage />,
      },
    ],
  },
]);

const App: FC<Props> = () => {
  return <RouterProvider router={router} />;
};

export default App;
