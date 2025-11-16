package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Conciliacion;
import com.tpfinal.iw3.model.EstadoOrden;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConciliacionException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IDetalleOrdenBusiness;
import com.tpfinal.iw3.model.business.interfaces.IOrdenBusiness;
import com.tpfinal.iw3.model.persistence.DetalleOrdenRepository;
import com.tpfinal.iw3.model.persistence.OrdenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

    @Autowired
    private OrdenRepository ordenDAO;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private IDetalleOrdenBusiness detalleOrdenBusiness;




    @Override
    public List<Orden> list() throws BusinessException {
        try {
            return ordenDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Orden load(long id) throws NotFoundException, BusinessException {
        Optional<Orden> ordenFound;
        try {
            ordenFound = ordenDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (ordenFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra la Orden id= " + id).build();
        return ordenFound.get();
    }

    @Override
    public Orden add(Orden orden) throws DuplicateException, BusinessException {
        try {
            load(orden.getId());
            throw DuplicateException.builder().message("Ya existe la Orden id= " + orden.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Crear Nueva Orden").build();
        }
    }

    @Override
    public Orden update(Orden orden) throws NotFoundException, BusinessException {
        load(orden.getId());
        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al Actualizar Orden").build();
        }
    }

    @Override
    public void delete(Orden orden) throws NotFoundException, BusinessException {
        delete(orden.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try {
            ordenDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    /**
     * Obtiene la conciliación de una orden finalizada.
     * Calcula la diferencia entre el peso neto de balanza y el producto cargado.
     */
    @Override
    public Conciliacion getReconciliation(Long ordenId) 
            throws NotFoundException, ConflictException, ConciliacionException, BusinessException {
        
        log.info("Consultando conciliación para ordenId={}", ordenId);

        // 1. Buscar orden
        Optional<Orden> ordenOpt;
        try {
            ordenOpt = ordenDAO.findById(ordenId);
        } catch (Exception e) {
            log.error("Error al buscar orden id={}: {}", ordenId, e.getMessage(), e);
            throw new BusinessException("Error al recuperar orden", e);
        }

        if (ordenOpt.isEmpty()) {
            log.error("No se encontró orden con id={}", ordenId);
            throw new NotFoundException("Orden no encontrada con id: " + ordenId);
        }

        Orden orden = ordenOpt.get();

        // 2. Validar estado FINALIZADA
        if (orden.getEstado() != EstadoOrden.FINALIZADA) {
            log.error("La orden {} no está finalizada. Estado actual: {}", 
                     orden.getNumeroOrden(), orden.getEstado());
            throw new ConflictException(
                "La conciliación solo está disponible para órdenes FINALIZADAS. Estado actual: " + orden.getEstado()
            );
        }

        // 3. Calcular conciliación
        return calculateReconciliation(orden);
    }

    /**
     * Calcula la conciliación para una orden.
     * Requiere: pesajeInicial (tara), pesajeFinal, ultimaMasaAcomulada.
     */
    public Conciliacion calculateReconciliation(Orden orden) 
            throws ConciliacionException, BusinessException {
        
        log.debug("Calculando conciliación para orden={}", orden.getId());

        // Validar datos requeridos
        if (orden.getTara() == null) {
            throw new ConciliacionException("Falta pesaje inicial (tara)");
        }
        if (orden.getPesajeFinal() == null) {
            throw new ConciliacionException("Falta pesaje final");
        }
        if (orden.getUltimaMasaAcomulada() == null) {
            throw new ConciliacionException("No hay masa acumulada registrada");
        }

        // Calcular neto por balanza
        Double netoPorBalanza = orden.getPesajeFinal() - orden.getTara();
        
        // Calcular diferencia
        Double diferencia = netoPorBalanza - orden.getUltimaMasaAcomulada();
        
        // Calcular porcentaje de diferencia
        Double porcentajeDiferencia = 0.0;
        if (orden.getUltimaMasaAcomulada() > 0) {
            porcentajeDiferencia = (diferencia / orden.getUltimaMasaAcomulada()) * 100.0;
        }

        // Calcular promedios de los detalles almacenados
        Float promedioTemperatura = detalleOrdenBusiness.calculateAverageTemperature(orden.getId());
        Float promedioDensidad = detalleOrdenBusiness.calculateAverageDensity(orden.getId());
        Float promedioCaudal = detalleOrdenBusiness.calculateAverageFlowRate(orden.getId());

        // Contar cantidad de detalles
        Optional<List<com.tpfinal.iw3.model.DetalleOrden>> detallesOpt = 
            detalleOrdenRepository.findByOrdenId(orden.getId());
        int cantidadDetalles = detallesOpt.isPresent() ? detallesOpt.get().size() : 0;

        // Construir objeto de conciliación
        Conciliacion conciliacion = Conciliacion.builder()
            .ordenId(orden.getId())
            .numeroOrden(orden.getNumeroOrden())
            .pesajeInicial(orden.getTara())
            .pesajeFinal(orden.getPesajeFinal())
            .productoCargado(orden.getUltimaMasaAcomulada())
            .netoPorBalanza(netoPorBalanza)
            .diferencia(diferencia)
            .porcentajeDiferencia(porcentajeDiferencia)
            .promedioTemperatura(promedioTemperatura)
            .promedioDensidad(promedioDensidad)
            .promedioCaudal(promedioCaudal)
            .presetKg(orden.getPresetKg())
            .estadoOrden(orden.getEstado().name())
            .cantidadDetalles(cantidadDetalles)
            .build();

        log.info("Conciliación calculada - Orden={}, Neto balanza={} kg, Producto cargado={} kg, Diferencia={} kg ({} %)", 
                 orden.getNumeroOrden(), netoPorBalanza, orden.getUltimaMasaAcomulada(), 
                 diferencia, String.format("%.2f", porcentajeDiferencia));

        return conciliacion;
    }

    
}
