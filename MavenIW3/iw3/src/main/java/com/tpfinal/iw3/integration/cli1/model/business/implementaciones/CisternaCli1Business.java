package com.tpfinal.iw3.integration.cli1.model.business.implementaciones;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpfinal.iw3.integration.cli1.model.CisternaCli1;
import com.tpfinal.iw3.integration.cli1.model.business.interfaces.ICisternaCli1Business;
import com.tpfinal.iw3.integration.cli1.model.persistence.CisternaCli1Repository;
import com.tpfinal.iw3.integration.cli1.util.MapperEntity;
import com.tpfinal.iw3.model.Cisterna;
import com.tpfinal.iw3.model.business.excepciones.BusinessException;
import com.tpfinal.iw3.model.business.excepciones.NotFoundException;
import com.tpfinal.iw3.model.persistence.CisternaRepository;

@Service
public class CisternaCli1Business implements ICisternaCli1Business {

    @Autowired
    private CisternaCli1Repository cisternaCli1DAO;
    
    @Autowired
    private CisternaRepository cisternaBaseDAO;

    @Autowired
    private MapperEntity mapperEntity;

    @Override
    public CisternaCli1 load(String idCli1) throws BusinessException, NotFoundException {
        Optional<CisternaCli1> cisternaCli1Opt = cisternaCli1DAO.findOneByIdCli1(idCli1);
        if (cisternaCli1Opt.isEmpty()) {
            throw new NotFoundException("No se encontró la Cisterna CLI1 con id=" + idCli1);
        }
        return cisternaCli1Opt.get();
    }

    @Override
    public Cisterna loadOrCreate(CisternaCli1 cisternaCli1) throws BusinessException {
        // IMPORTANTE: La cisterna NO se guarda aquí
        // Se retorna sin persistir para que el deserializador la asocie al camión
        // y luego sí se persista como parte de la transacción principal
        
        // Buscar si ya existe una cisterna CLI1 mapeada
        Optional<CisternaCli1> cisternaCli1Existente = cisternaCli1DAO.findOneByIdCli1(cisternaCli1.getIdCli1());
        if (cisternaCli1Existente.isPresent()) {
            return cisternaCli1Existente.get();
        }
        
        // Buscar si ya existe una cisterna base con la misma capacidad
        Optional<Cisterna> cisternaBaseOpt = cisternaBaseDAO.findByCapacidad(cisternaCli1.getCapacidad());
        
        Cisterna cisternaBase;
        if (cisternaBaseOpt.isEmpty()) {
            // Crear nueva cisterna TEMPORAL (sin guardar aún)
            cisternaBase = new Cisterna();
            cisternaBase.setCapacidad(cisternaCli1.getCapacidad());
            cisternaBase.setNombre("Cisterna " + cisternaCli1.getCapacidad() + "L");
            cisternaBase.setCodigoExterno(cisternaCli1.getCodigoExterno());
            // NO GUARDAR AÚN - el deserializador le seteará el camión y luego guardará
        } else {
            cisternaBase = cisternaBaseOpt.get();
            // Mapear el código CLI1 a la cisterna existente
            mapperEntity.map(cisternaCli1, cisternaBase);
        }
        
        return cisternaBase;
    }
}
