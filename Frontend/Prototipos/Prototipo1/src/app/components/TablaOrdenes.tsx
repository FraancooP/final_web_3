import { Orden, EstadoOrden } from "@/app/types/orden";
import { Clock, Truck, Gauge, Droplet, Thermometer, Weight } from "lucide-react";

interface TablaOrdenesProps {
  ordenes: Orden[];
  ordenSeleccionadaId: string | null;
  onOrdenSelect: (id: string) => void;
}

export const TablaOrdenes: React.FC<TablaOrdenesProps> = ({
  ordenes,
  ordenSeleccionadaId,
  onOrdenSelect,
}) => {
  const getEstadoColor = (estado: EstadoOrden) => {
    switch (estado) {
      case EstadoOrden.PENDIENTE_PESAJE_INICIAL:
        return "bg-gray-100 text-gray-800 border-gray-300";
      case EstadoOrden.CON_PESAJE_INICIAL:
        return "bg-blue-100 text-blue-800 border-blue-300";
      case EstadoOrden.EN_CARGA:
        return "bg-green-100 text-green-800 border-green-300";
      case EstadoOrden.CERRADA_PARA_CARGA:
        return "bg-orange-100 text-orange-800 border-orange-300";
      case EstadoOrden.FINALIZADA:
        return "bg-purple-100 text-purple-800 border-purple-300";
      default:
        return "bg-gray-100 text-gray-800 border-gray-300";
    }
  };

  const formatearFecha = (fecha: Date) => {
    return new Date(fecha).toLocaleString("es-ES", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-200">
              <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Nº Orden
              </th>
              <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Estado
              </th>
              <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Camión
              </th>
              <th className="px-4 py-3 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Preset (L)
              </th>
              <th className="px-4 py-3 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Carga Actual (L)
              </th>
              <th className="px-4 py-3 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Temperatura (°C)
              </th>
              <th className="px-4 py-3 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Densidad (kg/m³)
              </th>
              <th className="px-4 py-3 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Caudal (L/min)
              </th>
              <th className="px-4 py-3 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                Fecha Creación
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {ordenes.map((orden) => (
              <tr
                key={orden.id}
                onClick={() => onOrdenSelect(orden.id)}
                className={`cursor-pointer transition-colors hover:bg-blue-50 ${
                  ordenSeleccionadaId === orden.id ? "bg-blue-50" : ""
                }`}
              >
                <td className="px-4 py-4">
                  <span className="font-mono font-semibold text-gray-900">
                    {orden.numeroOrden}
                  </span>
                </td>
                <td className="px-4 py-4">
                  <span
                    className={`inline-flex px-3 py-1 text-xs font-medium rounded-full border ${getEstadoColor(
                      orden.estado
                    )}`}
                  >
                    {orden.estado}
                  </span>
                </td>
                <td className="px-4 py-4">
                  <div className="flex items-center gap-2">
                    <Truck className="w-4 h-4 text-gray-500" />
                    <span className="font-medium text-gray-900">{orden.camion}</span>
                  </div>
                </td>
                <td className="px-4 py-4 text-right">
                  <span className="font-semibold text-gray-900">
                    {orden.preset.toLocaleString("es-ES")}
                  </span>
                </td>
                <td className="px-4 py-4 text-right">
                  {orden.datosCarga ? (
                    <div className="flex flex-col">
                      <span className="font-semibold text-gray-900">
                        {orden.datosCarga.cargaActual.toLocaleString("es-ES", {
                          maximumFractionDigits: 0,
                        })}
                      </span>
                      <span className="text-xs text-gray-500">
                        {((orden.datosCarga.cargaActual / orden.preset) * 100).toFixed(1)}%
                      </span>
                    </div>
                  ) : (
                    <span className="text-gray-400">-</span>
                  )}
                </td>
                <td className="px-4 py-4 text-right">
                  {orden.datosCarga ? (
                    <div className="flex items-center justify-end gap-1">
                      <Thermometer className="w-3 h-3 text-gray-500" />
                      <span className="text-gray-900">{orden.datosCarga.temperatura}°C</span>
                    </div>
                  ) : (
                    <span className="text-gray-400">-</span>
                  )}
                </td>
                <td className="px-4 py-4 text-right">
                  {orden.datosCarga ? (
                    <div className="flex items-center justify-end gap-1">
                      <Weight className="w-3 h-3 text-gray-500" />
                      <span className="text-gray-900">{orden.datosCarga.densidad}</span>
                    </div>
                  ) : (
                    <span className="text-gray-400">-</span>
                  )}
                </td>
                <td className="px-4 py-4 text-right">
                  {orden.datosCarga ? (
                    <div className="flex items-center justify-end gap-1">
                      <Gauge className="w-3 h-3 text-gray-500" />
                      <span className="text-gray-900">{orden.datosCarga.caudal}</span>
                    </div>
                  ) : (
                    <span className="text-gray-400">-</span>
                  )}
                </td>
                <td className="px-4 py-4">
                  <div className="flex items-center gap-1 text-sm text-gray-600">
                    <Clock className="w-3 h-3" />
                    <span>{formatearFecha(orden.fechaCreacion)}</span>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {ordenes.length === 0 && (
        <div className="py-12 text-center">
          <p className="text-gray-500">No se encontraron órdenes con los filtros aplicados</p>
        </div>
      )}
    </div>
  );
};
