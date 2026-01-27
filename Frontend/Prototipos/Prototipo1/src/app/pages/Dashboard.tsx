import { useState } from "react";
import { useAuth } from "@/app/context/AuthContext";
import { useOrdenes } from "@/app/hooks/useOrdenes";
import { EstadoOrden } from "@/app/types/orden";
import { DashboardHeader } from "@/app/components/DashboardHeader";
import { FiltrosOrdenes } from "@/app/components/FiltrosOrdenes";
import { TablaOrdenes } from "@/app/components/TablaOrdenes";
import { DetalleOrden } from "@/app/components/DetalleOrden";

export const Dashboard = () => {
  const { user } = useAuth();
  const { ordenes, actualizarEstadoOrden, registrarPesajeInicial, iniciarCarga } = useOrdenes();
  
  const [filtroEstado, setFiltroEstado] = useState<EstadoOrden | "TODOS">("TODOS");
  const [filtroBusqueda, setFiltroBusqueda] = useState("");
  const [ordenSeleccionada, setOrdenSeleccionada] = useState<string | null>(null);

  // Filtrar órdenes
  const ordenesFiltradas = ordenes.filter((orden) => {
    const cumpleFiltroEstado = filtroEstado === "TODOS" || orden.estado === filtroEstado;
    const cumpleBusqueda =
      orden.numeroOrden.toLowerCase().includes(filtroBusqueda.toLowerCase()) ||
      orden.camion.toLowerCase().includes(filtroBusqueda.toLowerCase());
    
    return cumpleFiltroEstado && cumpleBusqueda;
  });

  const ordenActual = ordenes.find((o) => o.id === ordenSeleccionada);

  return (
    <div className="min-h-screen bg-gray-50">
      <DashboardHeader user={user} />

      <div className="max-w-[1800px] mx-auto px-4 py-6">
        <FiltrosOrdenes
          filtroEstado={filtroEstado}
          filtroBusqueda={filtroBusqueda}
          onFiltroEstadoChange={setFiltroEstado}
          onFiltroBusquedaChange={setFiltroBusqueda}
          totalOrdenes={ordenes.length}
          ordenesFiltradas={ordenesFiltradas.length}
        />

        <div className="grid grid-cols-1 xl:grid-cols-3 gap-6 mt-6">
          <div className={ordenSeleccionada ? "xl:col-span-2" : "xl:col-span-3"}>
            <TablaOrdenes
              ordenes={ordenesFiltradas}
              ordenSeleccionadaId={ordenSeleccionada}
              onOrdenSelect={setOrdenSeleccionada}
            />
          </div>

          {ordenSeleccionada && ordenActual && (
            <div className="xl:col-span-1">
              <DetalleOrden
                orden={ordenActual}
                onClose={() => setOrdenSeleccionada(null)}
                onActualizarEstado={actualizarEstadoOrden}
                onRegistrarPesaje={registrarPesajeInicial}
                onIniciarCarga={iniciarCarga}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
