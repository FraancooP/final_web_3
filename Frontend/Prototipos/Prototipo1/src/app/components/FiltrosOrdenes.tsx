import { EstadoOrden } from "@/app/types/orden";
import { Search, Filter } from "lucide-react";

interface FiltrosOrdenesProps {
  filtroEstado: EstadoOrden | "TODOS";
  filtroBusqueda: string;
  onFiltroEstadoChange: (estado: EstadoOrden | "TODOS") => void;
  onFiltroBusquedaChange: (busqueda: string) => void;
  totalOrdenes: number;
  ordenesFiltradas: number;
}

export const FiltrosOrdenes: React.FC<FiltrosOrdenesProps> = ({
  filtroEstado,
  filtroBusqueda,
  onFiltroEstadoChange,
  onFiltroBusquedaChange,
  totalOrdenes,
  ordenesFiltradas,
}) => {
  return (
    <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-200">
      <div className="flex flex-col lg:flex-row gap-4">
        <div className="flex-1">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <Search className="w-4 h-4 inline mr-1" />
            Buscar por orden o camión
          </label>
          <input
            type="text"
            value={filtroBusqueda}
            onChange={(e) => onFiltroBusquedaChange(e.target.value)}
            placeholder="ej: ORD-2026-001 o ABC-123"
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
          />
        </div>

        <div className="flex-1">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            <Filter className="w-4 h-4 inline mr-1" />
            Filtrar por estado
          </label>
          <select
            value={filtroEstado}
            onChange={(e) => onFiltroEstadoChange(e.target.value as EstadoOrden | "TODOS")}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none bg-white"
          >
            <option value="TODOS">Todos los estados ({totalOrdenes})</option>
            <option value={EstadoOrden.PENDIENTE_PESAJE_INICIAL}>
              {EstadoOrden.PENDIENTE_PESAJE_INICIAL}
            </option>
            <option value={EstadoOrden.CON_PESAJE_INICIAL}>
              {EstadoOrden.CON_PESAJE_INICIAL}
            </option>
            <option value={EstadoOrden.EN_CARGA}>{EstadoOrden.EN_CARGA}</option>
            <option value={EstadoOrden.CERRADA_PARA_CARGA}>
              {EstadoOrden.CERRADA_PARA_CARGA}
            </option>
            <option value={EstadoOrden.FINALIZADA}>{EstadoOrden.FINALIZADA}</option>
          </select>
        </div>
      </div>

      <div className="mt-4 flex items-center justify-between text-sm">
        <p className="text-gray-600">
          Mostrando <span className="font-semibold text-gray-900">{ordenesFiltradas}</span> de{" "}
          <span className="font-semibold text-gray-900">{totalOrdenes}</span> órdenes
        </p>
      </div>
    </div>
  );
};
