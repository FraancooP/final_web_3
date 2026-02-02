<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-900 mb-2">Cisternas</h2>
      <p class="text-gray-600 text-sm">Gestión y disponibilidad de cisternas</p>
    </div>

    <!-- Filtros -->
    <div class="mb-4 flex gap-3">
      <div class="flex-1">
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por código..."
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
        />
      </div>
      <select
        v-model="filtroDisponibilidad"
        class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
      >
        <option value="TODOS">Todos</option>
        <option value="DISPONIBLE">Disponibles</option>
        <option value="OCUPADO">Ocupados</option>
      </select>
    </div>

    <!-- Tabla de cisternas -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Código</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Capacidad (L)</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tipo</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Última Actualización</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr 
            v-for="cisterna in cisternasFiltradas" 
            :key="cisterna.id"
            class="hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <svg class="w-4 h-4 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <ellipse cx="12" cy="7" rx="10" ry="3"></ellipse>
                  <path d="M2 7v10c0 1.66 4.48 3 10 3s10-1.34 10-3V7"></path>
                  <line x1="12" y1="10" x2="12" y2="20"></line>
                </svg>
                <span class="font-mono font-semibold text-gray-900 text-sm">{{ cisterna.codigo }}</span>
              </div>
            </td>
            <td class="px-4 py-3 text-sm text-gray-700 text-right font-medium">{{ formatNumber(cisterna.capacidad) }}</td>
            <td class="px-4 py-3 text-sm text-gray-700">{{ cisterna.tipo }}</td>
            <td class="px-4 py-3">
              <span
                class="inline-flex px-3 py-1 text-xs font-medium rounded-full"
                :class="cisterna.disponible ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ cisterna.disponible ? 'DISPONIBLE' : 'OCUPADO' }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ formatearFecha(cisterna.fechaActualizacion) }}</td>
          </tr>
        </tbody>
      </table>

      <!-- Mensaje si no hay cisternas -->
      <div v-if="cisternasFiltradas.length === 0" class="text-center py-12">
        <svg class="mx-auto w-12 h-12 text-gray-400 mb-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <ellipse cx="12" cy="7" rx="10" ry="3"></ellipse>
          <path d="M2 7v10c0 1.66 4.48 3 10 3s10-1.34 10-3V7"></path>
          <line x1="12" y1="10" x2="12" y2="20"></line>
        </svg>
        <p class="text-gray-600 text-base">No se encontraron cisternas</p>
      </div>
    </div>

    <!-- Resumen -->
    <div class="mt-6 grid grid-cols-3 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Total</div>
        <div class="text-2xl font-bold text-gray-900">{{ cisternas.length }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Disponibles</div>
        <div class="text-2xl font-bold text-green-600">{{ cisternasDisponibles }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Ocupadas</div>
        <div class="text-2xl font-bold text-red-600">{{ cisternasOcupadas }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const cisternas = ref([]);
const busqueda = ref('');
const filtroDisponibilidad = ref('TODOS');
const cargando = ref(true);

onMounted(async () => {
  try {
    // TODO: Reemplazar con llamada al backend
    // const response = await cisternaService.getCisternas();
    // cisternas.value = response.data;
    
    // Datos temporales vacíos
    cisternas.value = [];
    cargando.value = false;
  } catch (error) {
    console.error('Error al cargar cisternas:', error);
    cargando.value = false;
  }
});

const cisternasFiltradas = computed(() => {
  return cisternas.value.filter((cisterna) => {
    const cumpleBusqueda = cisterna.codigo.toLowerCase().includes(busqueda.value.toLowerCase());
    const cumpleDisponibilidad = 
      filtroDisponibilidad.value === 'TODOS' || 
      (filtroDisponibilidad.value === 'DISPONIBLE' && cisterna.disponible) ||
      (filtroDisponibilidad.value === 'OCUPADO' && !cisterna.disponible);
    
    return cumpleBusqueda && cumpleDisponibilidad;
  });
});

const cisternasDisponibles = computed(() => {
  return cisternas.value.filter(c => c.disponible).length;
});

const cisternasOcupadas = computed(() => {
  return cisternas.value.filter(c => !c.disponible).length;
});

const formatNumber = (num) => {
  return new Intl.NumberFormat('es-ES').format(num);
};

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};
</script>
