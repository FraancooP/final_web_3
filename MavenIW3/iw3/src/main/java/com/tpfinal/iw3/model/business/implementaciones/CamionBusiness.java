package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Camion;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.ICamionBusiness;
import com.tpfinal.iw3.model.persistence.CamionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CamionBusiness implements ICamionBusiness {

    @Autowired
    private CamionRepository camionDAO;


    @Override
    public List<Camion> list() throws BusinessException {
        try{
            return camionDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }




    @Override
    public Camion load(String patente) throws BusinessException, NotFoundException {
        Optional<Camion> camionEncontrado;

        try {
            camionEncontrado = camionDAO.findByPatente(patente);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (camionEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontro el camion con la patente = "+patente).build();
        }

        return camionEncontrado.get();
    }

    @Override
    public Camion load(long id) throws BusinessException, NotFoundException {
        Optional<Camion> camionEncontrado;

        try {
            camionEncontrado = camionDAO.findById(id);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (camionEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontro el camion con el id = "+id).build();
        }

        return camionEncontrado.get();
    }

    @Override
    public Camion add(Camion camion) throws DuplicateException, BusinessException {
        
        try {
            load(camion.getPatente());
            throw DuplicateException.builder().message("El camion con la patente = "+camion.getPatente()+" ya existe.").build();
        } catch (NotFoundException e) {
            //log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
        }

        try {
            load(camion.getId());
            throw DuplicateException.builder().message("El camion con el id = "+camion.getId()+" ya existe.").build();
        } catch (NotFoundException e) {
            //log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
        }

        try {
            camion = camionDAO.save(camion);
            //Faltaria agregarle la cisterna si es que viene aparte.
            return camion;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al crear el nuevo camion").build();
        }
}




    @Override
    public Camion update(Camion camion) throws NotFoundException, DuplicateException, BusinessException {
        load(camion.getId());
        //Camion camionExistente = load(camion.getId());

        Optional<Camion> camionEncontrado;
        try{
            camionEncontrado = camionDAO.findByPatenteAndIdNot(camion.getPatente(), camion.getId());
        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (camionEncontrado.isPresent()) {
            throw DuplicateException.builder().message("Ya existe un camion con la patente = "+camion.getPatente()).build();
        }




        //CASO QUE NO PERMITIMOS CARGAR UNA PATENTE:
        //if(!camionExistente.getPatente().equals(camion.getPatente())){
        //    throw BusinessException.builder().message("No se puede cambiar la patente del camion.").build();
        //}


        try{
            return camionDAO.save(camion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Actualizar el camion").build();
        }
    }




    @Override
    public void delete(Camion camion) throws NotFoundException, BusinessException {
        delete(camion.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try{
            camionDAO.deleteById(id);
        } catch (Exception e) { 
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al eliminar el camion").build();
        }
    
    }
    
}
