import { useParams } from 'react-router-dom';
import { FC } from 'react';
import { KanbanBoard } from '@components/board/KanbanBoard.tsx';

interface BoardPageProps {
}

export const BoardPage: FC<BoardPageProps> = () => {
  const { id } = useParams();

  return (
    <div className="min-h-screen p-4 md:p-8">
      <div className="w-full">
        <header className="mb-32 text-center">
          <h1 className="mb-4 text-4xl font-bold tracking-tight text-amber-800 md:text-5xl">
            Retro Kanban
            {id}
          </h1>
          <p className="text-amber-700">Opis Boarda i inne typu zespół itd ile w plecy jestemy</p>
        </header>
        <aside>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-2xl font-bold text-amber-800">Zespół</h2>
            <button className="rounded bg-amber-700 px-4 py-2 text-white hover:bg-amber-600">
              Dodaj członka
            </button>
          </div>
        </aside>
        <KanbanBoard />
      </div>
    </div>
  );
};
