<template>
  <div>
    <!-- Filtros y Búsqueda -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-4 mb-6">
      <div class="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
        <div class="flex flex-col md:flex-row gap-4 flex-1">
          <div class="flex-1 min-w-[300px]">
            <label class="block text-xs font-medium text-gray-700 mb-1">Buscar</label>
            <div class="relative">
              <svg class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.35-4.35"></path>
              </svg>
              <input
                v-model="filtroBusqueda"
                type="text"
                placeholder="Buscar por número de orden o camión..."
                class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
              />
            </div>
          </div>

          <div class="min-w-[200px]">
            <label class="block text-xs font-medium text-gray-700 mb-1">Estado</label>
            <select
              v-model="filtroEstado"
              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
            >
              <option value="TODOS">Todos los estados</option>
              <option value="PENDIENTE_PESAJE_INICIAL">Pendiente Pesaje</option>
              <option value="CON_PESAJE_INICIAL">Con Pesaje Inicial</option>
              <option value="EN_CARGA">En Carga</option>
              <option value="CERRADA_PARA_CARGA">Cerrada para Carga</option>
              <option value="FINALIZADA">Finalizada</option>
            </select>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <div class="text-xs text-gray-600 mt-2 md:mt-6">
            Mostrando <span class="font-semibold text-gray-900">{{ ordenesFiltradas.length }}</span> de 
            <span class="font-semibold text-gray-900">{{ ordenes.length }}</span> órdenes
          </div>
          
          <button
            @click="abrirModalCrearOrden"
            class="mt-2 md:mt-6 flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            <span>Crear Orden</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Tabla de Órdenes -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="bg-gray-50 border-b border-gray-200">
                <th class="px-3 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Nº Orden</th>
                <th class="px-3 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Estado</th>
                <th class="px-3 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Camión</th>
                <th class="px-3 py-2.5 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">Preset (L)</th>
                <th class="px-3 py-2.5 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">Carga Actual (L)</th>
                <th class="px-3 py-2.5 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">Temperatura (°C)</th>
                <th class="px-3 py-2.5 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">Densidad (kg/m³)</th>
                <th class="px-3 py-2.5 text-right text-xs font-semibold text-gray-700 uppercase tracking-wider">Caudal (L/min)</th>
                <th class="px-3 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Fecha Creación</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr
                v-for="orden in ordenesFiltradas"
                :key="orden.id"
                @click="verDetalleOrden(orden.id)"
                class="cursor-pointer transition-colors hover:bg-gray-50"
              >
                <td class="px-3 py-3">
                  <span class="font-mono font-semibold text-gray-900 text-sm">{{ orden.numeroOrden }}</span>
                </td>
                <td class="px-3 py-3">
                  <span
                    class="inline-flex px-2 py-1 text-xs font-medium rounded-full border whitespace-nowrap"
                    :class="getEstadoColor(orden.estado)"
                  >
                    {{ orden.estado }}
                  </span>
                </td>
                <td class="px-3 py-3">
                  <div class="flex items-center gap-2">
                    <svg class="w-3.5 h-3.5 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="1" y="3" width="15" height="13"></rect>
                      <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                      <circle cx="5.5" cy="18.5" r="2.5"></circle>
                      <circle cx="18.5" cy="18.5" r="2.5"></circle>
                    </svg>
                    <span class="text-gray-900 text-sm">{{ orden.camion }}</span>
                  </div>
                </td>
                <td class="px-3 py-3 text-right font-medium text-gray-900 text-sm">{{ formatNumber(orden.preset) }}</td>
                <td class="px-3 py-3 text-right font-medium text-blue-600 text-sm">{{ formatNumber(orden.cargaActual) }}</td>
                <td class="px-3 py-3 text-right text-gray-600 text-sm">{{ orden.temperatura?.toFixed(1) || '-' }}</td>
                <td class="px-3 py-3 text-right text-gray-600 text-sm">{{ orden.densidad?.toFixed(2) || '-' }}</td>
                <td class="px-3 py-3 text-right text-gray-600 text-sm">{{ orden.caudal?.toFixed(0) || '-' }}</td>
                <td class="px-3 py-3 text-gray-600 text-xs">{{ formatearFecha(orden.fechaCreacion) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Mensaje si no hay órdenes -->
        <div v-if="ordenesFiltradas.length === 0" class="text-center py-12">
          <svg class="mx-auto w-12 h-12 text-gray-400 mb-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <p class="text-gray-600 text-base">No se encontraron órdenes</p>
          <p class="text-gray-500 text-sm mt-1">Intenta ajustar los filtros de búsqueda</p>
        </div>
      </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import ordenService from '@/services/ordenService';

const router = useRouter();
const ordenes = ref([]);
const filtroEstado = ref('TODOS');
const filtroBusqueda = ref('');
const cargando = ref(true);
const error = ref(null);

// Cargar órdenes del backend
onMounted(async () => {
  try {
    cargando.value = true;
    const response = await ordenService.getOrdenes();
    
    // Transformar datos del backend al formato del frontend
    ordenes.value = response.data.map(orden => ({
      id: orden.id,
      numeroOrden: orden.numeroOrden || `ORD-${orden.id}`,
      estado: orden.estado || 'PENDIENTE',
      camion: orden.camion?.patente || 'N/A',
      preset: orden.preset || 0,
      cargaActual: orden.masaAcumulada || 0,
      temperatura: orden.temperatura,
      densidad: orden.densidad,
      caudal: orden.caudal,
      fechaCreacion: orden.fechaPesajeInicial || orden.fechaRecepcion || new Date()
    }));
    
    console.log('Órdenes cargadas:', ordenes.value);
  } catch (err) {
    console.error('Error al cargar órdenes:', err);
    error.value = 'Error al cargar las órdenes del servidor';
  } finally {
    cargando.value = false;
  }
});

// Órdenes filtradas
const ordenesFiltradas = computed(() => {
  return ordenes.value.filter((orden) => {
    const cumpleFiltroEstado = filtroEstado.value === 'TODOS' || orden.estado === filtroEstado.value;
    const cumpleBusqueda =
      orden.numeroOrden.toString().toLowerCase().includes(filtroBusqueda.value.toLowerCase()) ||
      orden.camion.toLowerCase().includes(filtroBusqueda.value.toLowerCase());
    
    return cumpleFiltroEstado && cumpleBusqueda;
  });
});

const getEstadoColor = (estado) => {
  const colores = {
    'PENDIENTE_PESAJE_INICIAL': 'bg-gray-100 text-gray-800 border-gray-300',
    'CON_PESAJE_INICIAL': 'bg-blue-100 text-blue-800 border-blue-300',
    'EN_CARGA': 'bg-green-100 text-green-800 border-green-300',
    'CERRADA_PARA_CARGA': 'bg-orange-100 text-orange-800 border-orange-300',
    'FINALIZADA': 'bg-purple-100 text-purple-800 border-purple-300'
  };
  return colores[estado] || 'bg-gray-100 text-gray-800 border-gray-300';
};

const formatNumber = (num) => {
  return new Intl.NumberFormat('es-ES').format(num);
};

const formatearFecha = (fecha) => {
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const verDetalleOrden = (id) => {
  router.push({ name: 'DetalleOrden', params: { id } });
};

const abrirModalCrearOrden = () => {
  router.push({ name: 'CrearOrden' });
};
</script>

<style scoped>
/* Estilos adicionales si es necesario */
</style>
