export enum EstadoOrden {
  PENDIENTE_PESAJE_INICIAL = "Pendiente pesaje inicial",
  CON_PESAJE_INICIAL = "Con pesaje inicial registrado",
  EN_CARGA = "En carga",
  CERRADA_PARA_CARGA = "Cerrada para carga",
  FINALIZADA = "Finalizada",
}

export interface DatosCarga {
  cargaActual: number; // litros
  temperatura: number; // celsius
  densidad: number; // kg/m3
  caudal: number; // litros/min
  ultimaActualizacion: Date;
}

export interface Orden {
  id: string;
  numeroOrden: string;
  estado: EstadoOrden;
  camion: string;
  preset: number; // litros totales a cargar
  pesajeInicial?: number; // kg
  pesajeFinal?: number; // kg
  datosCarga?: DatosCarga;
  fechaCreacion: Date;
  fechaFinalizacion?: Date;
  temperaturaPromedio?: number;
  densidadPromedio?: number;
}
