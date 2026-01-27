import { Orden, EstadoOrden } from "@/app/types/orden";
import {
  X,
  Truck,
  Calendar,
  Weight,
  Droplet,
  Thermometer,
  Gauge,
  Activity,
  CheckCircle2,
  Clock,
} from "lucide-react";
import { ProgressBar } from "@/app/components/ProgressBar";
import { MetricasTiempoReal } from "@/app/components/MetricasTiempoReal";

interface DetalleOrdenProps {
  orden: Orden;
  onClose: () => void;
  onActualizarEstado: (id: string, estado: EstadoOrden) => void;
  onRegistrarPesaje: (id: string, peso: number) => void;
  onIniciarCarga: (id: string) => void;
}

export const DetalleOrden: React.FC<DetalleOrdenProps> = ({
  orden,
  onClose,
  onActualizarEstado,
  onRegistrarPesaje,
  onIniciarCarga,
}) => {
  const porcentajeCarga = orden.datosCarga
    ? (orden.datosCarga.cargaActual / orden.preset) * 100
    : 0;

  const formatearFecha = (fecha: Date) => {
    return new Date(fecha).toLocaleString("es-ES", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getEstadoColor = (estado: EstadoOrden) => {
    switch (estado) {
      case EstadoOrden.PENDIENTE_PESAJE_INICIAL:
        return "bg-gray-100 text-gray-800";
      case EstadoOrden.CON_PESAJE_INICIAL:
        return "bg-blue-100 text-blue-800";
      case EstadoOrden.EN_CARGA:
        return "bg-green-100 text-green-800";
      case EstadoOrden.CERRADA_PARA_CARGA:
        return "bg-orange-100 text-orange-800";
      case EstadoOrden.FINALIZADA:
        return "bg-purple-100 text-purple-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-lg border border-gray-200 sticky top-6">
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-gray-900">Detalle de Orden</h2>
            <p className="text-sm text-gray-600 mt-1">Información completa y acciones</p>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <X className="w-5 h-5 text-gray-600" />
          </button>
        </div>
      </div>

      <div className="p-6 space-y-6 max-h-[calc(100vh-200px)] overflow-y-auto">
        {/* Información básica */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <span className="text-2xl font-bold font-mono text-gray-900">
              {orden.numeroOrden}
            </span>
            <span
              className={`px-3 py-1 text-sm font-medium rounded-full ${getEstadoColor(
                orden.estado
              )}`}
            >
              {orden.estado}
            </span>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="flex items-center gap-2">
              <Truck className="w-4 h-4 text-gray-500" />
              <div>
                <p className="text-xs text-gray-600">Camión</p>
                <p className="font-semibold text-gray-900">{orden.camion}</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Droplet className="w-4 h-4 text-gray-500" />
              <div>
                <p className="text-xs text-gray-600">Preset</p>
                <p className="font-semibold text-gray-900">
                  {orden.preset.toLocaleString("es-ES")} L
                </p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4 text-gray-500" />
              <div>
                <p className="text-xs text-gray-600">Fecha Creación</p>
                <p className="text-sm text-gray-900">{formatearFecha(orden.fechaCreacion)}</p>
              </div>
            </div>

            {orden.pesajeInicial && (
              <div className="flex items-center gap-2">
                <Weight className="w-4 h-4 text-gray-500" />
                <div>
                  <p className="text-xs text-gray-600">Pesaje Inicial</p>
                  <p className="font-semibold text-gray-900">
                    {orden.pesajeInicial.toLocaleString("es-ES")} kg
                  </p>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Progreso de carga */}
        {orden.estado === EstadoOrden.EN_CARGA && orden.datosCarga && (
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="font-semibold text-gray-900">Progreso de Carga</h3>
              <span className="text-sm text-gray-600">
                {porcentajeCarga.toFixed(1)}%
              </span>
            </div>
            <ProgressBar porcentaje={porcentajeCarga} />
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">
                {orden.datosCarga.cargaActual.toLocaleString("es-ES", {
                  maximumFractionDigits: 0,
                })}{" "}
                L
              </span>
              <span className="text-gray-600">
                {orden.preset.toLocaleString("es-ES")} L
              </span>
            </div>
          </div>
        )}

        {/* Métricas en tiempo real */}
        {orden.datosCarga && (
          <MetricasTiempoReal datosCarga={orden.datosCarga} />
        )}

        {/* Información adicional finalizada */}
        {orden.estado === EstadoOrden.FINALIZADA && (
          <div className="p-4 bg-purple-50 rounded-lg border border-purple-200">
            <div className="flex items-center gap-2 mb-3">
              <CheckCircle2 className="w-5 h-5 text-purple-700" />
              <h3 className="font-semibold text-purple-900">Orden Finalizada</h3>
            </div>
            <div className="space-y-2 text-sm">
              {orden.pesajeFinal && (
                <div className="flex justify-between">
                  <span className="text-purple-700">Pesaje Final:</span>
                  <span className="font-semibold text-purple-900">
                    {orden.pesajeFinal.toLocaleString("es-ES")} kg
                  </span>
                </div>
              )}
              {orden.temperaturaPromedio && (
                <div className="flex justify-between">
                  <span className="text-purple-700">Temp. Promedio:</span>
                  <span className="font-semibold text-purple-900">
                    {orden.temperaturaPromedio}°C
                  </span>
                </div>
              )}
              {orden.densidadPromedio && (
                <div className="flex justify-between">
                  <span className="text-purple-700">Densidad Promedio:</span>
                  <span className="font-semibold text-purple-900">
                    {orden.densidadPromedio} kg/m³
                  </span>
                </div>
              )}
              {orden.fechaFinalizacion && (
                <div className="flex justify-between">
                  <span className="text-purple-700">Fecha Finalización:</span>
                  <span className="font-semibold text-purple-900">
                    {formatearFecha(orden.fechaFinalizacion)}
                  </span>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Acciones según estado */}
        <div className="space-y-3 pt-4 border-t border-gray-200">
          <h3 className="font-semibold text-gray-900">Acciones Disponibles</h3>

          {orden.estado === EstadoOrden.PENDIENTE_PESAJE_INICIAL && (
            <button
              onClick={() => {
                const peso = prompt("Ingrese el peso inicial (kg):");
                if (peso) {
                  onRegistrarPesaje(orden.id, parseFloat(peso));
                }
              }}
              className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center justify-center gap-2"
            >
              <Weight className="w-5 h-5" />
              Registrar Pesaje Inicial
            </button>
          )}

          {orden.estado === EstadoOrden.CON_PESAJE_INICIAL && (
            <button
              onClick={() => onIniciarCarga(orden.id)}
              className="w-full bg-green-600 text-white py-3 rounded-lg hover:bg-green-700 transition-colors font-medium flex items-center justify-center gap-2"
            >
              <Activity className="w-5 h-5" />
              Iniciar Carga
            </button>
          )}

          {orden.estado === EstadoOrden.EN_CARGA && (
            <button
              onClick={() => onActualizarEstado(orden.id, EstadoOrden.CERRADA_PARA_CARGA)}
              className="w-full bg-orange-600 text-white py-3 rounded-lg hover:bg-orange-700 transition-colors font-medium flex items-center justify-center gap-2"
            >
              <Clock className="w-5 h-5" />
              Cerrar para Carga
            </button>
          )}

          {orden.estado === EstadoOrden.CERRADA_PARA_CARGA && (
            <button
              onClick={() => onActualizarEstado(orden.id, EstadoOrden.FINALIZADA)}
              className="w-full bg-purple-600 text-white py-3 rounded-lg hover:bg-purple-700 transition-colors font-medium flex items-center justify-center gap-2"
            >
              <CheckCircle2 className="w-5 h-5" />
              Finalizar Orden
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
