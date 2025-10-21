package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Chofer;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IChoferBusiness;
import com.tpfinal.iw3.model.persistence.ChoferRepository;



import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ChoferBusiness implements IChoferBusiness {

    @Autowired
    private ChoferRepository choferDAO;


    @Override
    public List<Chofer> list() throws BusinessException {
        try{
            return choferDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }


    @Override
    public Chofer load(long id) throws BusinessException, NotFoundException{

        Optional<Chofer> choferEncontrado;

        try{
            choferEncontrado = choferDAO.findById(id);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(choferEncontrado.isEmpty()){
            throw NotFoundException.builder().message("No se encontro el chofer con id = "+id).build();
        }

        return choferEncontrado.get();
    }


    @Override
     public Chofer load(String documento) throws BusinessException, NotFoundException{

        Optional<Chofer> choferEncontrado;

        try{
            choferEncontrado = choferDAO.findByDocumento(documento);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(choferEncontrado.isEmpty()){
            throw NotFoundException.builder().message("No se encontro el chofer con documento = "+documento).build();
        }

        return choferEncontrado.get();
    }

    @Override
    public Chofer add(Chofer chofer) throws BusinessException, DuplicateException{

        Optional<Chofer> choferExistente;

        try{
            choferExistente = choferDAO.findById(chofer.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al crear un nuevo chofer").build();
        }

        if(choferExistente.isPresent()){
            throw DuplicateException.builder().message("Ya existe un chofer con id = "+chofer.getId()).build();
        }

        try{
            return choferDAO.save(chofer);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al crear un nuevo chofer").build();
        }
    }
    
    @Override
    public Chofer update(Chofer chofer) throws NotFoundException, BusinessException, DuplicateException{


        //load(chofer.getId());
        Optional<Chofer> choferEncontrado;

        Optional<Chofer> choferExistente;

        try{
            choferExistente = choferDAO.findById(chofer.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al buscar por Id").build();
        }

        if(choferExistente.isEmpty()){
            throw NotFoundException.builder().message("El chofer no existe").build();
        }
        

        try{
            choferEncontrado = choferDAO.findByDocumentoAndIdNot(chofer.getDocumento(), chofer.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(choferEncontrado.isPresent()){
            throw DuplicateException.builder().message("Ya existe un chofer con documento = "+chofer.getDocumento()).build();
        }

        try{
            return choferDAO.save(chofer);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al actualizar chofer existente").build();
        }

    }

    @Override
    public void delete(long id) throws BusinessException, NotFoundException{

        load(id);
        try{
            choferDAO.deleteById(id);
        } catch (Exception e) { 
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al eliminar el chofer").build();
        }
    }

    @Override
    public void  delete(Chofer chofer) throws BusinessException, NotFoundException{
        delete(chofer.getId());
    }
}
