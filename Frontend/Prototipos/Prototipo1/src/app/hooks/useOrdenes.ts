import { useState, useEffect } from "react";
import { Orden, EstadoOrden, DatosCarga } from "@/app/types/orden";

// Datos simulados para las órdenes
const generarOrdenesSimuladas = (): Orden[] => {
  return [
    {
      id: "1",
      numeroOrden: "ORD-2026-001",
      estado: EstadoOrden.EN_CARGA,
      camion: "ABC-123",
      preset: 25000,
      pesajeInicial: 15000,
      datosCarga: {
        cargaActual: 18500,
        temperatura: 22.5,
        densidad: 850,
        caudal: 450,
        ultimaActualizacion: new Date(),
      },
      fechaCreacion: new Date(2026, 0, 27, 8, 30),
    },
    {
      id: "2",
      numeroOrden: "ORD-2026-002",
      estado: EstadoOrden.CON_PESAJE_INICIAL,
      camion: "DEF-456",
      preset: 30000,
      pesajeInicial: 16500,
      fechaCreacion: new Date(2026, 0, 27, 9, 15),
    },
    {
      id: "3",
      numeroOrden: "ORD-2026-003",
      estado: EstadoOrden.PENDIENTE_PESAJE_INICIAL,
      camion: "GHI-789",
      preset: 20000,
      fechaCreacion: new Date(2026, 0, 27, 10, 0),
    },
    {
      id: "4",
      numeroOrden: "ORD-2026-004",
      estado: EstadoOrden.CERRADA_PARA_CARGA,
      camion: "JKL-012",
      preset: 25000,
      pesajeInicial: 14800,
      datosCarga: {
        cargaActual: 25000,
        temperatura: 23.1,
        densidad: 852,
        caudal: 0,
        ultimaActualizacion: new Date(),
      },
      fechaCreacion: new Date(2026, 0, 27, 7, 45),
    },
    {
      id: "5",
      numeroOrden: "ORD-2026-005",
      estado: EstadoOrden.FINALIZADA,
      camion: "MNO-345",
      preset: 28000,
      pesajeInicial: 15200,
      pesajeFinal: 38800,
      temperaturaPromedio: 22.8,
      densidadPromedio: 851,
      fechaCreacion: new Date(2026, 0, 26, 15, 20),
      fechaFinalizacion: new Date(2026, 0, 26, 16, 45),
    },
    {
      id: "6",
      numeroOrden: "ORD-2026-006",
      estado: EstadoOrden.EN_CARGA,
      camion: "PQR-678",
      preset: 22000,
      pesajeInicial: 14500,
      datosCarga: {
        cargaActual: 10200,
        temperatura: 21.9,
        densidad: 848,
        caudal: 480,
        ultimaActualizacion: new Date(),
      },
      fechaCreacion: new Date(2026, 0, 27, 9, 45),
    },
  ];
};

export const useOrdenes = () => {
  const [ordenes, setOrdenes] = useState<Orden[]>(generarOrdenesSimuladas());

  // Simular actualización en tiempo real de las órdenes en carga
  useEffect(() => {
    const interval = setInterval(() => {
      setOrdenes((prevOrdenes) =>
        prevOrdenes.map((orden) => {
          if (orden.estado === EstadoOrden.EN_CARGA && orden.datosCarga) {
            const incremento = Math.random() * 100 + 50; // 50-150 litros por segundo
            const nuevaCargaActual = Math.min(
              orden.datosCarga.cargaActual + incremento,
              orden.preset
            );

            // Variaciones aleatorias en temperatura, densidad y caudal
            const nuevaTemperatura = orden.datosCarga.temperatura + (Math.random() - 0.5) * 0.5;
            const nuevaDensidad = orden.datosCarga.densidad + (Math.random() - 0.5) * 2;
            const nuevoCaudal = 450 + (Math.random() - 0.5) * 50;

            // Si alcanzó el preset, cambiar estado automáticamente
            if (nuevaCargaActual >= orden.preset) {
              return {
                ...orden,
                estado: EstadoOrden.CERRADA_PARA_CARGA,
                datosCarga: {
                  ...orden.datosCarga,
                  cargaActual: orden.preset,
                  temperatura: Number(nuevaTemperatura.toFixed(1)),
                  densidad: Number(nuevaDensidad.toFixed(0)),
                  caudal: 0, // Caudal cero cuando está cerrada
                  ultimaActualizacion: new Date(),
                },
              };
            }

            return {
              ...orden,
              datosCarga: {
                ...orden.datosCarga,
                cargaActual: nuevaCargaActual,
                temperatura: Number(nuevaTemperatura.toFixed(1)),
                densidad: Number(nuevaDensidad.toFixed(0)),
                caudal: Number(nuevoCaudal.toFixed(0)),
                ultimaActualizacion: new Date(),
              },
            };
          }
          return orden;
        })
      );
    }, 1000); // Actualizar cada segundo

    return () => clearInterval(interval);
  }, []);

  const actualizarEstadoOrden = (id: string, nuevoEstado: EstadoOrden) => {
    setOrdenes((prevOrdenes) =>
      prevOrdenes.map((orden) => {
        if (orden.id === id) {
          // Si se finaliza la orden, calcular promedios y pesaje final
          if (nuevoEstado === EstadoOrden.FINALIZADA && orden.datosCarga) {
            return {
              ...orden,
              estado: nuevoEstado,
              fechaFinalizacion: new Date(),
              temperaturaPromedio: orden.datosCarga.temperatura,
              densidadPromedio: orden.datosCarga.densidad,
              pesajeFinal: orden.pesajeInicial
                ? orden.pesajeInicial + (orden.preset * orden.datosCarga.densidad) / 1000
                : undefined,
            };
          }
          return { ...orden, estado: nuevoEstado };
        }
        return orden;
      })
    );
  };

  const registrarPesajeInicial = (id: string, peso: number) => {
    setOrdenes((prevOrdenes) =>
      prevOrdenes.map((orden) =>
        orden.id === id
          ? {
              ...orden,
              pesajeInicial: peso,
              estado: EstadoOrden.CON_PESAJE_INICIAL,
            }
          : orden
      )
    );
  };

  const iniciarCarga = (id: string) => {
    setOrdenes((prevOrdenes) =>
      prevOrdenes.map((orden) =>
        orden.id === id
          ? {
              ...orden,
              estado: EstadoOrden.EN_CARGA,
              datosCarga: {
                cargaActual: 0,
                temperatura: 20 + Math.random() * 5,
                densidad: 845 + Math.random() * 10,
                caudal: 450,
                ultimaActualizacion: new Date(),
              },
            }
          : orden
      )
    );
  };

  return {
    ordenes,
    actualizarEstadoOrden,
    registrarPesajeInicial,
    iniciarCarga,
  };
};