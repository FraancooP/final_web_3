package com.tpfinal.iw3.model.business.interfaces;

import java.util.List;

import com.tpfinal.iw3.model.Conciliacion;
import com.tpfinal.iw3.model.Orden;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.ConciliacionException;
import com.tpfinal.iw3.model.business.excepciones.ConflictException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;

public interface IOrdenBusiness {

    public List<Orden> list() throws BusinessException;

    public Orden load(long id) throws NotFoundException, BusinessException;

    public Orden add(Orden orden) throws DuplicateException, BusinessException;

    public Orden update(Orden orden) throws NotFoundException, BusinessException, DuplicateException;

    public void delete(Orden orden) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    /**
     * Obtiene la conciliación de una orden finalizada.
     * Compara el peso neto de balanza con el producto cargado registrado.
     * 
     * @param ordenId ID de la orden
     * @return Objeto Conciliacion con todos los datos comparativos
     * @throws NotFoundException Si la orden no existe
     * @throws ConflictException Si la orden no está en estado FINALIZADA
     * @throws ConciliacionException Si faltan datos necesarios para conciliar
     * @throws BusinessException Si hay un error en la operación
     */
    public Conciliacion getReconciliation(Long ordenId) 
            throws NotFoundException, ConflictException, ConciliacionException, BusinessException;

}
