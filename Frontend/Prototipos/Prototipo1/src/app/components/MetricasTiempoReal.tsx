import { DatosCarga } from "@/app/types/orden";
import { Thermometer, Weight, Gauge, Clock } from "lucide-react";

interface MetricasTiempoRealProps {
  datosCarga: DatosCarga;
}

export const MetricasTiempoReal: React.FC<MetricasTiempoRealProps> = ({ datosCarga }) => {
  const formatearTiempo = (fecha: Date) => {
    return new Date(fecha).toLocaleTimeString("es-ES", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  };

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold text-gray-900">Métricas en Tiempo Real</h3>
        <div className="flex items-center gap-1 text-xs text-gray-600">
          <Clock className="w-3 h-3" />
          <span>{formatearTiempo(datosCarga.ultimaActualizacion)}</span>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div className="p-4 bg-gradient-to-br from-red-50 to-orange-50 rounded-lg border border-red-200">
          <div className="flex items-center gap-2 mb-2">
            <Thermometer className="w-4 h-4 text-red-600" />
            <span className="text-xs font-medium text-red-900">Temperatura</span>
          </div>
          <div className="flex items-baseline gap-1">
            <span className="text-2xl font-bold text-red-900">
              {datosCarga.temperatura}
            </span>
            <span className="text-sm text-red-700">°C</span>
          </div>
          <div className="mt-2 h-1 bg-red-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-red-600 transition-all duration-300"
              style={{ width: `${(datosCarga.temperatura / 30) * 100}%` }}
            />
          </div>
        </div>

        <div className="p-4 bg-gradient-to-br from-blue-50 to-cyan-50 rounded-lg border border-blue-200">
          <div className="flex items-center gap-2 mb-2">
            <Weight className="w-4 h-4 text-blue-600" />
            <span className="text-xs font-medium text-blue-900">Densidad</span>
          </div>
          <div className="flex items-baseline gap-1">
            <span className="text-2xl font-bold text-blue-900">
              {datosCarga.densidad}
            </span>
            <span className="text-xs text-blue-700">kg/m³</span>
          </div>
          <div className="mt-2 h-1 bg-blue-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-blue-600 transition-all duration-300"
              style={{ width: `${((datosCarga.densidad - 800) / 100) * 100}%` }}
            />
          </div>
        </div>

        <div className="col-span-2 p-4 bg-gradient-to-br from-green-50 to-emerald-50 rounded-lg border border-green-200">
          <div className="flex items-center gap-2 mb-2">
            <Gauge className="w-4 h-4 text-green-600" />
            <span className="text-xs font-medium text-green-900">Caudal</span>
          </div>
          <div className="flex items-baseline gap-1">
            <span className="text-3xl font-bold text-green-900">
              {datosCarga.caudal}
            </span>
            <span className="text-sm text-green-700">L/min</span>
          </div>
          <div className="mt-3 h-2 bg-green-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-green-500 to-green-600 transition-all duration-300 animate-pulse"
              style={{ width: `${(datosCarga.caudal / 600) * 100}%` }}
            />
          </div>
        </div>
      </div>

      <div className="p-3 bg-gray-50 rounded-lg border border-gray-200">
        <div className="flex items-center justify-between text-xs text-gray-600">
          <span>Actualización automática cada segundo</span>
          <div className="flex items-center gap-1">
            <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse" />
            <span className="text-green-700 font-medium">En vivo</span>
          </div>
        </div>
      </div>
    </div>
  );
};
