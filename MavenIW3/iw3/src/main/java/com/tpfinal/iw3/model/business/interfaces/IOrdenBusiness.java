package com.tpfinal.iw3.model.business.interfaces;

public List<Orden> list() throws BusinessException;

    public Orden load(long id) throws NotFoundException, BusinessException;

    public Orden add(Orden orden) throws FoundException, BusinessException;

    public Orden update(Orden orden) throws NotFoundException, BusinessException, FoundException;

    public void delete(Orden orden) throws NotFoundException, BusinessException;

    public void delete(long id) throws NotFoundException, BusinessException;

    public Orden acknowledgeAlarm(Long idAlarm, User user) throws BusinessException, NotFoundException;

    public Orden confirmIssueAlarm(Long idAlarm, User user) throws BusinessException, NotFoundException;

    public byte[] generateConciliationPdf(Long orderNumber) throws BusinessException, NotFoundException;

    public Map<String, Object> getConciliationJson(Long idOrder) throws BusinessException, NotFoundException;



}
