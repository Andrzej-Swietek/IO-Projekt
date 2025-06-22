import { FC, useState } from 'react';
import { RetroContainer } from '@components/common/RetroContainer.tsx';
import { RetroEntryCard } from '@components/common/RetroEntryCard.tsx';
import { RetroInput } from '@components/common/RetroInput.tsx';
import { ConfirmModal } from '@components/common/ConfirmModal.tsx';
import { RetroButton } from '@components/common/RetroButton.tsx';
import { LabelModal } from '@components/task/LabelModal.tsx';
import { Pen, X } from 'lucide-react';
import { LabelControllerApiFactory } from '@/api';
import { toast } from 'sonner';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Loading } from '@components/common/Loading.tsx';


const fetchLabels = async (filter: string) => {
  const api = LabelControllerApiFactory();
  try {
    const response = await api.getAllLabels(filter);
    return response.data;
  } catch (error) {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-expect-error
    toast.error('Error fetching labels:', error);
    return [];
  }
};

const deleteLabel = async (id: number) => {
  const api = LabelControllerApiFactory();
  await api.deleteLabel(id);
};

const createLabel = async ({ name, color }: { name: string; color: string }) => {
  const api = LabelControllerApiFactory();
  const response = await api.createLabel({ name, color });
  return response.data;
};

const editLabel = async ({ id, name, color }: { id: number; name: string; color: string }) => {
  const api = LabelControllerApiFactory();
  const response = await api.updateLabel(id, { name, color });
  return response.data;
};

export const LabelsManagement: FC = () => {
  const { t } = useTranslation();

  const [filter, setFilter] = useState<string>('');
  const queryClient = useQueryClient();

  const [deleteData, setDeleteData] = useState<{ id: number; name: string } | undefined>();
  const [showDeleteModal, setShowDeleteModal] = useState<boolean>(false);

  const [editData, setEditData] = useState<{ id: number; name: string; color: string } | undefined>();
  const [showEditModal, setShowEditModal] = useState<boolean>(false);

  const [showAddModal, setShowAddModal] = useState<boolean>(false);

  const { data: labels = [], isLoading, isError } = useQuery({
    queryKey: ['labels', filter],
    queryFn: () => fetchLabels(filter),
    keepPreviousData: true,
  });

  const deleteLabelMutation = useMutation({
    mutationFn: deleteLabel,
    onSuccess: () => {
      toast.success('Label deleted');
      queryClient.invalidateQueries({ queryKey: ['labels'] });
      setDeleteData(undefined);
    },
    onError: () => {
      toast.error('Failed to delete label');
    },
  });

  const createLabelMutation = useMutation({
    mutationFn: createLabel,
    onSuccess: () => {
      toast.success('Label added');
      queryClient.invalidateQueries({ queryKey: ['labels'] });
      setShowAddModal(false);
    },
    onError: () => {
      toast.error('Failed to add label');
    },
  });

  const editLabelMutation = useMutation({
    mutationFn: editLabel,
    onSuccess: () => {
      toast.success('Label updated');
      queryClient.invalidateQueries({ queryKey: ['labels'] });
      setShowEditModal(false);
      setEditData(undefined);
    },
    onError: () => {
      toast.error('Failed to update label');
    },
  });

  const handleDeleteClick = (id: number, name: string) => {
    setDeleteData({ id: id, name });
    setShowDeleteModal(true);
  };

  const handleConfirmDelete = () => {
    if (deleteData?.id !== null) {
      deleteLabelMutation.mutate(deleteData!.id!);
    }
  };

  const handleEditClick = (label: { id: number; name: string; color: string }) => {
    setEditData(label);
    setShowEditModal(true);
  };

  if (isLoading || isError) {
    return <Loading className="h-screen"></Loading>;
  }

  return (
    <>
      <div className="w-full h-[125px] !px-32 !pt-12 !pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] leading-[100%] tracking-[0%] align-bottom text-[var(--primary-black)]">
          {t('labels.management')}
        </h1>
      </div>
      <div className="flex flex-col items-center justify-start min-h-[80vh] my-16">
        <RetroContainer className="w-full lg:w-[80%] !px-12 !py-10 flex flex-col gap-8">
          <div className="w-full  flex justify-between items-center gap-4 mb-4">
            <div className="w-full">
              <RetroInput
                label={t('labels.filterLabel')}
                value={filter}
                onChange={e => setFilter(e.target.value)}
                placeholder={t('labels.searchPlaceholder')}
                className=""
              />
            </div>
            <div className="w-[20%]">
              <RetroButton
                className="w-full mt-8"
                onClick={() => setShowAddModal(true)}
              >
                {t('labels.add')}
              </RetroButton>
            </div>
          </div>
          <div className="flex flex-col gap-4">
            {labels.map(label => (
              <RetroEntryCard
                key={label.id}
                className="flex items-center justify-between p-4 border"
                left={(
                  <div
                    className="flex gap-10 flex-row items-center justify-between font-bold"
                    style={{ color: label.color }}
                  >
                    <div
                      className="h-full retro-shadow min-w-[50px] aspect-square border"
                      style={{ backgroundColor: label.color }}
                    >
                    </div>
                    <div
                      className="h-full font-bold text-md uppercase tracking-wider text-black flex items-center justify-center"
                    >
                      {label.name}
                    </div>
                  </div>
                )}
                right={(
                  <div className="flex gap-4">
                    <RetroButton
                      icon={<Pen className="w-4 h-4" />}
                      onClick={() => handleEditClick(label)}
                    >
                      {t('labels.edit')}
                    </RetroButton>
                    <RetroButton
                      icon={<X className="w-4 h-4" />}
                      onClick={() => handleDeleteClick(label.id, label.name)}
                    >
                      {t('labels.delete')}
                    </RetroButton>
                  </div>
                )}
              >
              </RetroEntryCard>
            ))}
          </div>
        </RetroContainer>
      </div>

      <ConfirmDeleteModal
        open={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={() => {
          handleConfirmDelete();
          setShowDeleteModal(false);
        }}
        labelName={deleteData?.name || ''}
      />
      <LabelModal
        open={showAddModal}
        onClose={() => setShowAddModal(false)}
        onSave={data => {
          createLabelMutation.mutate(data);
          setShowAddModal(false);
        }}
        loading={createLabelMutation.isPending}
      />
      <LabelModal
        open={showEditModal}
        onClose={() => setShowEditModal(false)}
        onSave={data => {
          if (editData) {
            editLabelMutation.mutate({ id: editData.id, ...data });
          }
        }}
        initialName={editData?.name}
        initialColor={editData?.color}
        isEdit
        loading={editLabelMutation.isPending}
      />
    </>
  );
};

const ConfirmDeleteModal: FC<{
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  labelName: string;
}> = ({ open, onClose, onConfirm, labelName }) => {
  const { t } = useTranslation(); // ← DODAJ TO TU

  return (
    <ConfirmModal open={open} onClose={onClose} onConfirm={onConfirm}>
      <div>
        {t('labels.confirmDelete')}
        {' '}
        <span className="text-[var(--primary-red)]">{labelName}</span>
        ?
      </div>
    </ConfirmModal>
  );
};
