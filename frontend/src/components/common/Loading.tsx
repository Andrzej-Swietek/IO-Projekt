import React from 'react';

interface LoadingProps {
  className?: string;
  color?: string;
  label?: string;
  circleSize?: string;
  children?: React.ReactNode;
}

export const Loading: React.FC<LoadingProps> = ({
  className = '',
  color = 'text-blue-500',
  label = 'Loading...',
  circleSize = 'h-16 w-16',
  children,
}) => {
  return (
    <div className={`flex items-center justify-center ${className}`}>
      <div className={`animate-spin rounded-full ${circleSize} border-b-2 ${color}`}></div>
      {children || <span className="ml-4 text-lg">{label}</span>}
    </div>
  );
};
