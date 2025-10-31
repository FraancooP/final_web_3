package com.tpfinal.iw3.model.business.implementaciones;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.model.Producto;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.DuplicateException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.business.interfaces.IProductoBusiness;
import com.tpfinal.iw3.model.persistence.ProductoRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ProductoBusiness implements IProductoBusiness {

    @Autowired
    private ProductoRepository productoDAO;

    @Override
    public List<Producto> list() throws BusinessException {
        try{
            return productoDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }




    @Override
    public Producto add(Producto producto) throws BusinessException, DuplicateException {
        try {
            load(producto.getNombre());
            throw DuplicateException.builder().message("El producto con el nombre = "+producto.getNombre()+" ya existe.").build();
        } catch (NotFoundException e) {
            //log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
        }

        // Si el producto viene con ID (actualización o inserción manual), validar duplicado por ID.
        // Evitar NullPointerException al intentar desboxear un Long nulo cuando es alta nueva.
        if (producto.getId() != null) {
            try {
                load(producto.getId());
                throw DuplicateException.builder().message("El producto con el id = "+producto.getId()+" ya existe.").build();
            } catch (NotFoundException e) {
                //log.error(e.getMessage(), e);
                //throw BusinessException.builder().ex(e).build();
            }
        }

        try {
            producto = productoDAO.save(producto);
            return producto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //throw BusinessException.builder().ex(e).build();
            throw BusinessException.builder().message("Error al crear el nuevo producto").build();
        }

    }



    @Override
    public Producto update(Producto producto) throws NotFoundException, DuplicateException, BusinessException {
        load(producto.getId());
        //Producto productoExistente = load(producto.getId());

        Optional<Producto> productoEncontrado;
        try{
            productoEncontrado = productoDAO.findByNombreAndIdNot(producto.getNombre(), producto.getId());
        }catch(Exception e){
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }

        if (productoEncontrado.isPresent()) {
            throw DuplicateException.builder().message("Ya existe un producto con el nombre = "+producto.getNombre()).build();
        }

        try{
            return productoDAO.save(producto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Actualizar el producto").build();
        }
    }






    @Override
    public Producto load(long id) throws NotFoundException, BusinessException {
        Optional<Producto> productoEncontrado;

        try {
            productoEncontrado = productoDAO.findById(id);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (productoEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontro el producto con el id = "+id).build();
        }

        return productoEncontrado.get();
    }

    @Override
    public Producto load(String nombre) throws NotFoundException, BusinessException {
        Optional<Producto> productoEncontrado;

        try {
            productoEncontrado = productoDAO.findByNombre(nombre);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (productoEncontrado.isEmpty()) {
            throw NotFoundException.builder().message("No se encontro el producto con el nombre = "+nombre).build();
        }

        return productoEncontrado.get();
    }

    @Override
    public void delete(Producto producto) throws NotFoundException, BusinessException {
        delete(producto.getId());
    }

    @Override
    public void delete(long id) throws NotFoundException, BusinessException {
        load(id);
        try{
            productoDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al eliminar el producto").build();
        }
    }

}
