import { FC } from 'react';
import { RetroContainer } from '@/components/common/RetroContainer';

interface AlertsProps {
}

const fetchAlerts = async () => {
  // Placeholder for fetching alerts data
  // This function should be implemented to fetch actual alerts data from an API or service
  return [];
};

export const Alerts: FC<AlertsProps> = () => {
  return (
    <>
      <div className="w-full h-[125px] !px-32 !pt-12 !pb-8 border-b">
        <h1 className="font-[Josefin_Sans] font-normal text-[36px] leading-[100%] tracking-[0%] align-bottom text-[var(--primary-black)]">
          Alerts
        </h1>
      </div>
      <div className="flex flex-col items-center justify-start min-h-[80vh] my-16">
        <RetroContainer className="w-full lg:w-[80%] !px-12 !py-10 flex flex-col gap-8">
          <div className="flex flex-col gap-4">
          </div>
        </RetroContainer>
      </div>
    </>
  );
};
