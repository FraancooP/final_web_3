import api from './api';

/**
 * Servicio para gestionar órdenes de carga
 */
const ordenService = {
  /**
   * Obtiene todas las órdenes del sistema
   * @returns {Promise} Lista de órdenes
   */
  async getOrdenes() {
    try {
      console.log('🔍 Obteniendo órdenes del backend...');
      console.log('📍 URL:', 'http://localhost:8081/api/v1/ordenes');
      console.log('🔑 Token:', localStorage.getItem('token') ? 'Presente' : 'NO PRESENTE');
      
      const response = await api.get('/ordenes');
      console.log('✅ Órdenes obtenidas:', response.data);
      return response;
    } catch (error) {
      console.error('❌ Error al obtener órdenes:', error);
      console.error('📊 Status:', error.response?.status);
      console.error('📝 Mensaje:', error.response?.data);
      throw error;
    }
  },

  /**
   * Obtiene una orden específica por ID
   * @param {number|string} id - ID de la orden
   * @returns {Promise} Datos de la orden
   */
  async getOrdenById(id) {
    try {
      const response = await api.get(`/ordenes/${id}`);
      console.log('✅ Orden obtenida:', response.data);
      return response;
    } catch (error) {
      console.error(`❌ Error al obtener orden ${id}:`, error);
      throw error;
    }
  },

  /**
   * Obtiene la conciliación de una orden finalizada
   * @param {number|string} id - ID de la orden
   * @returns {Promise} Datos de conciliación
   */
  async getConciliacion(id) {
    try {
      const response = await api.get(`/ordenes/${id}/conciliacion`);
      console.log('✅ Conciliación obtenida:', response.data);
      return response;
    } catch (error) {
      console.error(`❌ Error al obtener conciliación de orden ${id}:`, error);
      throw error;
    }
  },

  /**
   * Crea una nueva orden de carga
   * @param {Object} ordenData - Datos de la orden
   * @returns {Promise} Orden creada
   */
  async crearOrden(ordenData) {
    try {
      const response = await api.post('/integration/cli1/ordenes', ordenData);
      console.log('✅ Orden creada:', response.data);
      return response;
    } catch (error) {
      console.error('❌ Error al crear orden:', error);
      throw error;
    }
  }
};

export default ordenService;
