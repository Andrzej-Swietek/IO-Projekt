import { FC, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { CommentControllerApiFactory } from '@/api';
import { RetroTextArea } from '@components/common/RetroTextArea.tsx';
import { useUserProfile } from '@context/UserProfileProvider.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';

export const AddCommentRetro: FC<{ taskId: number }> = ({ taskId }) => {
  const [text, setText] = useState('');
  const queryClient = useQueryClient();
  const { profile } = useUserProfile();
  const userId = profile?.id;

  const { mutate, isPending } = useMutation({
    mutationFn: () => CommentControllerApiFactory()
      .addComment({
        authorId: userId,
        content: text || '',
        taskId,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['task', taskId] });
      setText('');
    },
  });

  return (
    <form
      onSubmit={e => {
        e.preventDefault();
        mutate();
      }}
      className="mt-2"
    >
      <RetroTextArea
        value={text}
        onChange={e => setText(e.target.value)}
        className="mb-4 !h-auto"
        label="Add Comments"
      />
      <RetroButton
        type="submit"
        size="sm"
        disabled={isPending}
      >
        {isPending ? 'Adding...' : 'Add Comment'}
      </RetroButton>
    </form>
  );
};
