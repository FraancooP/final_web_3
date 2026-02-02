<template>
  <div class="p-6">
    <!-- Botón volver -->
    <button 
      @click="volver"
      class="mb-6 inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
    >
      <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="19" y1="12" x2="5" y2="12"></line>
        <polyline points="12 19 5 12 12 5"></polyline>
      </svg>
      <span>Volver a Órdenes</span>
    </button>

    <!-- Header -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-900 mb-2">Detalle de Orden</h2>
      <p class="text-gray-600 text-sm">{{ orden?.numeroOrden || 'Cargando...' }}</p>
    </div>

    <!-- Información básica -->
    <div v-if="orden" class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Información General</h3>
        <dl class="space-y-3">
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Número de Orden:</dt>
            <dd class="text-sm font-medium text-gray-900">{{ orden.numeroOrden }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Estado:</dt>
            <dd>
              <span class="inline-flex px-2 py-1 text-xs font-medium rounded-full border"
                    :class="getEstadoColor(orden.estado)">
                {{ orden.estado }}
              </span>
            </dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Camión:</dt>
            <dd class="text-sm font-medium text-gray-900">{{ orden.camion }}</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Fecha de Creación:</dt>
            <dd class="text-sm font-medium text-gray-900">{{ formatearFecha(orden.fechaCreacion) }}</dd>
          </div>
        </dl>
      </div>

      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Métricas Actuales</h3>
        <dl class="space-y-3">
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Preset:</dt>
            <dd class="text-sm font-bold text-gray-900">{{ formatNumber(orden.preset) }} L</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Carga Actual:</dt>
            <dd class="text-sm font-bold text-blue-600">{{ formatNumber(orden.cargaActual) }} L</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Temperatura:</dt>
            <dd class="text-sm font-medium text-gray-900">{{ orden.temperatura?.toFixed(1) || '-' }} °C</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Densidad:</dt>
            <dd class="text-sm font-medium text-gray-900">{{ orden.densidad?.toFixed(2) || '-' }} kg/L</dd>
          </div>
          <div class="flex justify-between">
            <dt class="text-sm text-gray-600">Caudal:</dt>
            <dd class="text-sm font-medium text-gray-900">{{ orden.caudal?.toFixed(0) || '-' }} L/min</dd>
          </div>
        </dl>
      </div>
    </div>

    <!-- Gráficos -->
    <div v-if="orden" class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Progreso de carga -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Progreso de Carga</h3>
        <div class="space-y-2">
          <div class="flex justify-between text-sm">
            <span class="text-gray-600">{{ formatNumber(orden.cargaActual) }} / {{ formatNumber(orden.preset) }} L</span>
            <span class="font-bold text-blue-600">{{ porcentajeCarga }}%</span>
          </div>
          <div class="w-full bg-gray-200 rounded-full h-8">
            <div 
              class="bg-blue-600 h-8 rounded-full transition-all duration-300 flex items-center justify-center"
              :style="{ width: porcentajeCarga + '%' }"
            >
              <span v-if="porcentajeCarga > 10" class="text-white text-xs font-bold">{{ porcentajeCarga }}%</span>
            </div>
          </div>
        </div>
        
        <!-- Placeholder para gráfico de progreso temporal -->
        <div class="mt-6 border-2 border-dashed border-gray-300 rounded-lg h-48 flex items-center justify-center">
          <div class="text-center text-gray-500">
            <svg class="mx-auto w-12 h-12 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="20" x2="18" y2="10"></line>
              <line x1="12" y1="20" x2="12" y2="4"></line>
              <line x1="6" y1="20" x2="6" y2="14"></line>
            </svg>
            <p class="text-sm">Gráfico de progreso en tiempo real</p>
            <p class="text-xs text-gray-400 mt-1">(Por implementar con Chart.js)</p>
          </div>
        </div>
      </div>

      <!-- Temperatura -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Temperatura</h3>
        <div class="border-2 border-dashed border-gray-300 rounded-lg h-64 flex items-center justify-center">
          <div class="text-center text-gray-500">
            <svg class="mx-auto w-12 h-12 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline>
              <polyline points="17 6 23 6 23 12"></polyline>
            </svg>
            <p class="text-sm">Gráfico de temperatura a lo largo del tiempo</p>
            <p class="text-xs text-gray-400 mt-1">(Por implementar con Chart.js)</p>
          </div>
        </div>
      </div>

      <!-- Densidad -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Densidad</h3>
        <div class="border-2 border-dashed border-gray-300 rounded-lg h-64 flex items-center justify-center">
          <div class="text-center text-gray-500">
            <svg class="mx-auto w-12 h-12 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline>
              <polyline points="17 6 23 6 23 12"></polyline>
            </svg>
            <p class="text-sm">Gráfico de densidad a lo largo del tiempo</p>
            <p class="text-xs text-gray-400 mt-1">(Por implementar con Chart.js)</p>
          </div>
        </div>
      </div>

      <!-- Caudal -->
      <div class="bg-white p-6 rounded-lg shadow">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Caudal</h3>
        <div class="border-2 border-dashed border-gray-300 rounded-lg h-64 flex items-center justify-center">
          <div class="text-center text-gray-500">
            <svg class="mx-auto w-12 h-12 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline>
              <polyline points="17 6 23 6 23 12"></polyline>
            </svg>
            <p class="text-sm">Gráfico de caudal a lo largo del tiempo</p>
            <p class="text-xs text-gray-400 mt-1">(Por implementar con Chart.js)</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div v-else-if="cargando" class="flex items-center justify-center py-12">
      <div class="text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
        <p class="text-gray-600">Cargando detalle de la orden...</p>
      </div>
    </div>

    <!-- Error state -->
    <div v-else class="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
      <svg class="mx-auto w-12 h-12 text-red-500 mb-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      <p class="text-red-800 font-medium">No se pudo cargar la orden</p>
      <p class="text-red-600 text-sm mt-2">Por favor, intenta nuevamente más tarde</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const orden = ref(null);
const cargando = ref(true);

const ordenId = route.params.id;

onMounted(async () => {
  try {
    // TODO: Reemplazar con llamada al backend
    // const response = await ordenService.getOrdenById(ordenId);
    // orden.value = response.data;
    
    // Datos temporales de ejemplo
    await new Promise(resolve => setTimeout(resolve, 500)); // Simular carga
    orden.value = {
      id: ordenId,
      numeroOrden: `ORD-2024-${ordenId.padStart(3, '0')}`,
      estado: 'EN_CARGA',
      camion: 'ABC-123',
      preset: 30000,
      cargaActual: 18500,
      temperatura: 18.5,
      densidad: 850.2,
      caudal: 250,
      fechaCreacion: new Date()
    };
    
    cargando.value = false;
  } catch (error) {
    console.error('Error al cargar la orden:', error);
    cargando.value = false;
  }
});

const porcentajeCarga = computed(() => {
  if (!orden.value) return 0;
  return Math.round((orden.value.cargaActual / orden.value.preset) * 100);
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

const volver = () => {
  router.push({ name: 'Ordenes' });
};
</script>
