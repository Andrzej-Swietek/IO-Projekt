import { FC, ChangeEvent, FormEvent, useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ReportControllerApiFactory, ReportReportTypeEnum, ReportRequest } from '@/api';
import { RetroModal } from '@components/common/RetroModal';
import { RetroInput } from '@components/common/RetroInput';
import { RetroSelect } from '@components/common/RetroSelect';
import { RetroButton } from '@components/common/RetroButton';

interface CreateReportModalProps {
    onClose: () => void;
    userId: number;
}

export const CreateReportModal: FC<CreateReportModalProps> = ({ onClose, userId }) => {
    const queryClient = useQueryClient();

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [reportType, setReportType] = useState<ReportReportTypeEnum>('TASK_REPORT');

    const createReportMutation = useMutation({
        mutationFn: async () => {
            const req: ReportRequest = {
                title,
                description,
                reportType,
                createdByUserId: userId,
            };
            const res = await ReportControllerApiFactory().createReport(req);
            return res.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['reports', userId] });
            onClose();
        },
    });

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        createReportMutation.mutate();
    };

    return (
        <RetroModal title="Create New Report" onClose={onClose}>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                <RetroInput
                    label="Title"
                    value={title}
                    inputColor="bg-yellow-50"
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)}
                    required
                />
                <RetroInput
                    label="Description"
                    value={description}
                    inputColor="bg-yellow-50"
                    onChange={(e: ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
                    required
                />
                <RetroSelect
                    label="Report Type"
                    value={reportType}
                    onChange={(e: ChangeEvent<HTMLSelectElement>) =>
                        setReportType(e.target.value as ReportReportTypeEnum)
                    }
                    options={Object.entries(ReportReportTypeEnum).map(([key, value]) => ({
                        label: key.replace(/([A-Z])/g, ' $1').trim(),
                        value,
                    }))}
                />
                <div className="flex justify-end gap-4 mt-4">
                    <RetroButton size="sm" type="button" onClick={onClose} icon={null}>
                        Cancel
                    </RetroButton>
                    <RetroButton size="sm" type="submit" disabled={createReportMutation.isPending}>
                        {createReportMutation.isPending ? 'Creating...' : 'Create Report'}
                    </RetroButton>
                </div>
            </form>
        </RetroModal>
    );
};