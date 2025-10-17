package com.tpfinal.iw3.model;

/**
 * Estados posibles de una orden en el ciclo de carga.
 * Los códigos corresponden a la secuencia del proceso.
 */
public enum EstadoOrden {
    
    PENDIENTE_PESAJE_INICIAL(1, "Pendiente de pesaje inicial"),
    CON_PESAJE_INICIAL(2, "Con pesaje inicial registrado"),
    CERRADA_PARA_CARGAR(3, "Cerrada para carga"),
    FINALIZADA(4, "Finalizada");
    
    private final int codigo;
    private final String descripcion;
    
    EstadoOrden(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
    
    public int getCodigo() {
        return codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Valida si es posible transicionar al estado destino.
     * Los cambios de estado son secuenciales y unidireccionales.
     */
    public boolean puedeTransicionarA(EstadoOrden destino) {
        return destino.codigo == this.codigo + 1;
    }
    
    /**
     * Obtiene el siguiente estado válido.
     */
    public EstadoOrden siguiente() {
        switch (this) {
            case PENDIENTE_PESAJE_INICIAL:
                return CON_PESAJE_INICIAL;
            case CON_PESAJE_INICIAL:
                return CERRADA_PARA_CARGAR;
            case CERRADA_PARA_CARGAR:
                return FINALIZADA;
            case FINALIZADA:
                return null; // No hay siguiente
            default:
                return null;
        }
    }
    
    /**
     * Verifica si el estado permite recibir datos de carga.
     */
    public boolean permiteRecepcionDatos() {
        return this == CON_PESAJE_INICIAL;
    }
    
    /**
     * Verifica si el estado está finalizado.
     */
    public boolean esFinalizado() {
        return this == FINALIZADA;
    }
    
    /**
     * Busca un estado por su código.
     */
    public static EstadoOrden fromCodigo(int codigo) {
        for (EstadoOrden estado : values()) {
            if (estado.codigo == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Código de estado inválido: " + codigo);
    }
}