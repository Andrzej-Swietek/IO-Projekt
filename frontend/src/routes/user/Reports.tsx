import {FC, useState, useEffect} from 'react';
import {useMutation, useQuery} from '@tanstack/react-query';
import {
    ReportControllerApiFactory
} from '@/api';

import {RetroButton} from '@components/common/RetroButton';
import {CreateReportModal} from '@components/report/CreateReportModal';
import {useUserProfile} from '@context/UserProfileProvider';
import {useNavigate} from 'react-router-dom';
import {Trash2, ArrowDownToLine} from 'lucide-react';

import axios from 'axios';
import { StorageControllerApi } from '@/api';

// Needed for binary data
const axiosBinary = axios.create({
    responseType: 'arraybuffer',
    headers: {
        Accept: 'application/pdf',
    },
});

const storageApi = new StorageControllerApi(undefined, undefined, axiosBinary);


export const Reports: FC = () => {
    const {profile} = useUserProfile();
    const navigate = useNavigate();

    const userId = profile?.id;

    const [showModal, setShowModal] = useState(false);

    const [generatingReportId, setGeneratingReportId] = useState<number | null>(null);

    const {data: reports, refetch} = useQuery({
        queryKey: ['reports', userId],
        queryFn: async () => {
            const res = await ReportControllerApiFactory().getReportById1(userId!);
            return res.data;
        },
        enabled: !!userId,
    });

    const deleteReportMutation = useMutation({
        mutationFn: async (reportId: number) => {
            await ReportControllerApiFactory().deleteReport(reportId);
        },
        onSuccess: () => {
            refetch();
        },
    });

    const generateReportMutation = useMutation({
        mutationFn: async (reportId: number) => {
            await ReportControllerApiFactory().generateReport(reportId);
        },
        onSuccess: () => {
            refetch();
        },
    });

    const downloadReportMutation = useMutation({
        mutationFn: async (filePath: string) => {
            // const res = await StorageControllerApiFactory().downloadFile(filePath);
            const res = await storageApi.downloadFile(filePath);
            const blob = new Blob([res.data], {type: 'application/pdf'});
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filePath || 'report.pdf';
            a.click();
        },
    });

    const handleDelete = (reportId: number) => {
        if (confirm('Are you sure you want to delete this report?')) {
            deleteReportMutation.mutate(reportId);
        }
    };

    const handleDownload = (filePath: string) => {
        downloadReportMutation.mutate(filePath);
    };

    useEffect(() => {
        if (!generatingReportId) return;

        const interval = setInterval(async () => {
            const res = await ReportControllerApiFactory().getReportById1(userId!);
            const updatedReports = res.data;

            const updatedReport = updatedReports.find(r => r.id === generatingReportId);
            const isReady = !!updatedReport?.reportResults?.[0]?.filePath;

            if (isReady) {
                clearInterval(interval);
                setGeneratingReportId(null);
                refetch();
            }
        }, 1500);

        return () => clearInterval(interval);
    }, [generatingReportId, userId, refetch]);


    useEffect(() => {
        if (!profile?.id) {
            navigate('/auth/login');
        }
    }, [profile, navigate]);

    return (
        <>
            <div className="w-full mx-auto p-6 space-y-8 flex flex-col items-center justify-center">
                <header className="text-center mt-8">
                    <h1 className="text-4xl font-bold mb-2">
                        Reports
                    </h1>
                    <p className="text-gray-600 text-lg">Generate and manage your personal reports</p>
                </header>

                <div
                    className="space-y-6 bg-yellow-100 w-full min-h-[15vh] border py-12 px-20 mb-8 border-black shadow-[6px_6px_0px_#000000] font-mono w-full lg:w-[80%]">
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-2xl font-bold">Your Reports</h2>
                        <RetroButton onClick={() => setShowModal(true)}>
                            New Report
                        </RetroButton>
                    </div>

                    {reports && reports.length > 0 ? (
                        reports.map(report => (
                            <div
                                key={report.id}
                                className="relative border border-black bg-white/90 p-4 retro-shadow flex flex-col gap-2"
                            >
                                <button
                                    onClick={() => handleDelete(report.id!)}
                                    className="absolute top-2 right-2 text-red-500 hover:text-red-700 transition"
                                    title="Delete Report"
                                >
                                    <Trash2 className="h-5 w-5"/>
                                </button>

                                <h3 className="text-lg font-semibold">{report.title}</h3>
                                <p><strong>Description:</strong> {report.description}</p>
                                <p><strong>Type:</strong> {report.reportType}</p>
                                <p><strong>Status:</strong> {report.reportResults?.[0]?.status || 'Pending'}</p>

                                <div className="self-start mt-2">
                                    {report.reportResults?.[0]?.filePath ? (
                                        <RetroButton
                                            type="button"
                                            onClick={() => handleDownload(report.reportResults[0].filePath!)}
                                            icon={<ArrowDownToLine className="h-4 w-4"/>}
                                        >
                                            Download PDF
                                        </RetroButton>
                                    ) : (
                                        <RetroButton
                                            type="button"
                                            onClick={() => {
                                                setGeneratingReportId(report.id!);
                                                generateReportMutation.mutate(report.id!);
                                            }}
                                            disabled={generatingReportId === report.id}
                                            icon={null}
                                        >
                                            {generatingReportId === report.id ? (
                                                <span className="flex items-center gap-2">
                                                    <svg className="animate-spin h-4 w-4 text-black" viewBox="0 0 24 24">
                                                        <circle
                                                            className="opacity-25"
                                                            cx="12"
                                                            cy="12"
                                                            r="10"
                                                            stroke="currentColor"
                                                            strokeWidth="4"
                                                            fill="none"
                                                        />
                                                        <path
                                                            className="opacity-75"
                                                            fill="currentColor"
                                                            d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                                                        />
                                                    </svg>
                                                        Generating...
                                                    </span>
                                            ) : (
                                                'Generate Document'
                                            )}
                                        </RetroButton>
                                    )}
                                </div>
                            </div>
                        ))
                    ) : (
                        <p className="text-gray-700">No reports available.</p>
                    )}
                </div>
            </div>
            {showModal && (
                <CreateReportModal
                    userId={userId!}
                    onClose={() => setShowModal(false)}
                />
            )}
        </>
    );
};
