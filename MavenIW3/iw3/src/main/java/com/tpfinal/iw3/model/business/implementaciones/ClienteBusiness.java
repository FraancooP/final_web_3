package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Cliente;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IClienteBusiness;
import com.tpfinal.iw3.model.persistence.ClienteRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClienteBusiness implements IClienteBusiness {

    @Autowired
    private ClienteRepository clienteDAO;

    @Override
    public List<Cliente> list() throws BusinessException {
        try{
            return clienteDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Cliente load(long id) throws BusinessException, NotFoundException{

        Optional<Cliente> clienteEncontrado;

        try {
            clienteEncontrado = clienteDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(clienteEncontrado.isEmpty()){
            throw NotFoundException.builder().message("No se encontro el cliente con id = "+id).build();
        }

        return clienteEncontrado.get();
    }

    @Override
    public Cliente load(String codigoExterno) throws BusinessException, NotFoundException{

        Optional<Cliente> clienteEncontrado;

        try {
            clienteEncontrado = clienteDAO.findByCodigoExterno(codigoExterno);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(clienteEncontrado.isEmpty()){
            throw NotFoundException.builder().message("No se encontro el cliente con codigo externo = "+codigoExterno).build();
        }

        return clienteEncontrado.get();
    }

    @Override
    public Cliente loadByRazonSocial(String razonSocial) throws BusinessException, NotFoundException {
        Optional<Cliente> clienteEncontrado;
        try {
            clienteEncontrado = clienteDAO.findOneByRazonSocialIgnoreCase(razonSocial);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (clienteEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontro el cliente con razon social = " + razonSocial).build();
        }

        return clienteEncontrado.get();
    }

    @Override
    public Cliente add(Cliente cliente) throws BusinessException, DuplicateException {

        Optional<Cliente> clienteExistente;

        try{
            clienteExistente = clienteDAO.findById(cliente.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al crear un nuevo cliente").build();
        }

        if(clienteExistente.isPresent()){
            throw DuplicateException.builder().message("Ya existe un cliente con id = "+cliente.getId()).build();
        }

        try{
            return clienteDAO.save(cliente);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al crear un nuevo cliente").build();
        }
    }

    @Override
    public Cliente update(Cliente cliente) throws BusinessException, DuplicateException, NotFoundException {

        Optional<Cliente> clienteEncontrado;

        Optional<Cliente> clienteExistente;

        try{
            clienteExistente = clienteDAO.findById(cliente.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al buscar por Id").build();
        }

        if(clienteExistente.isEmpty()){
            throw NotFoundException.builder().message("El cliente no existe").build();
        }
        
        try{
            clienteEncontrado = clienteDAO.findByCodigoExterno(cliente.getCodigoExterno());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if(clienteEncontrado.isPresent() && !clienteEncontrado.get().getId().equals(cliente.getId())){
            throw DuplicateException.builder().message("Ya existe un cliente con codigo externo = "+cliente.getCodigoExterno()).build();
        }

        try{
            return clienteDAO.save(cliente);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al actualizar cliente existente").build();
        }
    }

    @Override
    public void delete(long id) throws BusinessException, NotFoundException {
        
        load(id);
        try {
            clienteDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al eliminar cliente").build();
        }
    }

    @Override
    public void delete(Cliente cliente) throws BusinessException, NotFoundException {
        load(cliente.getId());
    }
}

