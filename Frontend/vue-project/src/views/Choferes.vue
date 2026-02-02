<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-900 mb-2">Choferes</h2>
      <p class="text-gray-600 text-sm">Gestión y disponibilidad de choferes</p>
    </div>

    <!-- Filtros -->
    <div class="mb-4 flex gap-3">
      <div class="flex-1">
        <input
          v-model="busqueda"
          type="text"
          placeholder="Buscar por nombre o documento..."
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

    <!-- Tabla de choferes -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Documento</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Licencia</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Última Actualización</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr 
            v-for="chofer in choferesFiltrados" 
            :key="chofer.id"
            class="hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <svg class="w-4 h-4 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <span class="font-semibold text-gray-900 text-sm">{{ chofer.nombre }}</span>
              </div>
            </td>
            <td class="px-4 py-3 text-sm text-gray-700 font-mono">{{ chofer.documento }}</td>
            <td class="px-4 py-3 text-sm text-gray-700 font-mono">{{ chofer.numeroLicencia }}</td>
            <td class="px-4 py-3">
              <span
                class="inline-flex px-3 py-1 text-xs font-medium rounded-full"
                :class="chofer.disponible ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
              >
                {{ chofer.disponible ? 'DISPONIBLE' : 'OCUPADO' }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ formatearFecha(chofer.fechaActualizacion) }}</td>
          </tr>
        </tbody>
      </table>

      <!-- Mensaje si no hay choferes -->
      <div v-if="choferesFiltrados.length === 0" class="text-center py-12">
        <svg class="mx-auto w-12 h-12 text-gray-400 mb-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
        <p class="text-gray-600 text-base">No se encontraron choferes</p>
      </div>
    </div>

    <!-- Resumen -->
    <div class="mt-6 grid grid-cols-3 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Total</div>
        <div class="text-2xl font-bold text-gray-900">{{ choferes.length }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Disponibles</div>
        <div class="text-2xl font-bold text-green-600">{{ choferesDisponibles }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Ocupados</div>
        <div class="text-2xl font-bold text-red-600">{{ choferesOcupados }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const choferes = ref([]);
const busqueda = ref('');
const filtroDisponibilidad = ref('TODOS');
const cargando = ref(true);

onMounted(async () => {
  try {
    // TODO: Reemplazar con llamada al backend
    // const response = await choferService.getChoferes();
    // choferes.value = response.data;
    
    // Datos temporales vacíos
    choferes.value = [];
    cargando.value = false;
  } catch (error) {
    console.error('Error al cargar choferes:', error);
    cargando.value = false;
  }
});

const choferesFiltrados = computed(() => {
  return choferes.value.filter((chofer) => {
    const cumpleBusqueda = 
      chofer.nombre.toLowerCase().includes(busqueda.value.toLowerCase()) ||
      chofer.documento.includes(busqueda.value);
    const cumpleDisponibilidad = 
      filtroDisponibilidad.value === 'TODOS' || 
      (filtroDisponibilidad.value === 'DISPONIBLE' && chofer.disponible) ||
      (filtroDisponibilidad.value === 'OCUPADO' && !chofer.disponible);
    
    return cumpleBusqueda && cumpleDisponibilidad;
  });
});

const choferesDisponibles = computed(() => {
  return choferes.value.filter(c => c.disponible).length;
});

const choferesOcupados = computed(() => {
  return choferes.value.filter(c => !c.disponible).length;
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
