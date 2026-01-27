interface ProgressBarProps {
  porcentaje: number;
}

export const ProgressBar: React.FC<ProgressBarProps> = ({ porcentaje }) => {
  const getColorClass = () => {
    if (porcentaje < 30) return "bg-red-500";
    if (porcentaje < 70) return "bg-yellow-500";
    return "bg-green-500";
  };

  return (
    <div className="w-full bg-gray-200 rounded-full h-4 overflow-hidden">
      <div
        className={`h-full transition-all duration-300 ${getColorClass()}`}
        style={{ width: `${Math.min(porcentaje, 100)}%` }}
      />
    </div>
  );
};
