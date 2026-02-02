<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-900 mb-2">Camiones</h2>
      <p class="text-gray-600 text-sm">Gestión y disponibilidad de camiones</p>
    </div>

    <!-- Filtros -->
    <div class="mb-4 flex gap-3">
      <div class="flex-1">
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por patente..."
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

    <!-- Tabla de camiones -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Patente</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Marca</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Modelo</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Última Actualización</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr 
            v-for="camion in camionesFiltrados" 
            :key="camion.id"
            class="hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <svg class="w-4 h-4 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="1" y="3" width="15" height="13"></rect>
                  <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                  <circle cx="5.5" cy="18.5" r="2.5"></circle>
                  <circle cx="18.5" cy="18.5" r="2.5"></circle>
                </svg>
                <span class="font-mono font-semibold text-gray-900 text-sm">{{ camion.patente }}</span>
              </div>
            </td>
            <td class="px-4 py-3 text-sm text-gray-700">{{ camion.marca }}</td>
            <td class="px-4 py-3 text-sm text-gray-700">{{ camion.modelo }}</td>
            <td class="px-4 py-3">
              <span
                class="inline-flex px-3 py-1 text-xs font-medium rounded-full"
                :class="camion.disponible ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ camion.disponible ? 'DISPONIBLE' : 'OCUPADO' }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ formatearFecha(camion.fechaActualizacion) }}</td>
          </tr>
        </tbody>
      </table>

      <!-- Mensaje si no hay camiones -->
      <div v-if="camionesFiltrados.length === 0" class="text-center py-12">
        <svg class="mx-auto w-12 h-12 text-gray-400 mb-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="1" y="3" width="15" height="13"></rect>
          <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
          <circle cx="5.5" cy="18.5" r="2.5"></circle>
          <circle cx="18.5" cy="18.5" r="2.5"></circle>
        </svg>
        <p class="text-gray-600 text-base">No se encontraron camiones</p>
      </div>
    </div>

    <!-- Resumen -->
    <div class="mt-6 grid grid-cols-3 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Total</div>
        <div class="text-2xl font-bold text-gray-900">{{ camiones.length }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Disponibles</div>
        <div class="text-2xl font-bold text-green-600">{{ camionesDisponibles }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Ocupados</div>
        <div class="text-2xl font-bold text-red-600">{{ camionesOcupados }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const camiones = ref([]);
const busqueda = ref('');
const filtroDisponibilidad = ref('TODOS');
const cargando = ref(true);

onMounted(async () => {
  try {
    // TODO: Reemplazar con llamada al backend
    // const response = await camionService.getCamiones();
    // camiones.value = response.data;
    
    // Datos temporales vacíos
    camiones.value = [];
    cargando.value = false;
  } catch (error) {
    console.error('Error al cargar camiones:', error);
    cargando.value = false;
  }
});

const camionesFiltrados = computed(() => {
  return camiones.value.filter((camion) => {
    const cumpleBusqueda = camion.patente.toLowerCase().includes(busqueda.value.toLowerCase());
    const cumpleDisponibilidad = 
      filtroDisponibilidad.value === 'TODOS' || 
      (filtroDisponibilidad.value === 'DISPONIBLE' && camion.disponible) ||
      (filtroDisponibilidad.value === 'OCUPADO' && !camion.disponible);
    
    return cumpleBusqueda && cumpleDisponibilidad;
  });
});

const camionesDisponibles = computed(() => {
  return camiones.value.filter(c => c.disponible).length;
});

const camionesOcupados = computed(() => {
  return camiones.value.filter(c => !c.disponible).length;
});

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
