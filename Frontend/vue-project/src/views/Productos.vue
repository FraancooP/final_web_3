<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-900 mb-2">Productos</h2>
      <p class="text-gray-600 text-sm">Catálogo de productos disponibles</p>
    </div>

    <!-- Búsqueda -->
    <div class="mb-4">
      <input
        v-model="busqueda"
        type="text"
        placeholder="Buscar producto..."
        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
      />
    </div>

    <!-- Tabla de productos -->
    <div class="bg-white rounded-lg shadow overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Código</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Descripción</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Densidad</th>
            <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200">
          <tr 
            v-for="producto in productosFiltrados" 
            :key="producto.id"
            class="hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3">
              <div class="flex items-center gap-2">
                <svg class="w-4 h-4 text-gray-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="12" y1="8" x2="12" y2="16"></line>
                  <line x1="8" y1="12" x2="16" y2="12"></line>
                </svg>
                <span class="font-semibold text-gray-900 text-sm">{{ producto.nombre }}</span>
              </div>
            </td>
            <td class="px-4 py-3 text-sm text-gray-700 font-mono">{{ producto.codigo }}</td>
            <td class="px-4 py-3 text-sm text-gray-600">{{ producto.descripcion || '-' }}</td>
            <td class="px-4 py-3 text-sm text-gray-700 text-right">
              {{ producto.densidad ? producto.densidad.toFixed(2) + ' kg/L' : '-' }}
            </td>
            <td class="px-4 py-3">
              <span
                class="inline-flex px-3 py-1 text-xs font-medium rounded-full"
                :class="producto.activo ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'"
              >
                {{ producto.activo ? 'ACTIVO' : 'INACTIVO' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Mensaje si no hay productos -->
      <div v-if="productosFiltrados.length === 0" class="text-center py-12">
        <svg class="mx-auto w-12 h-12 text-gray-400 mb-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="12" y1="8" x2="12" y2="16"></line>
          <line x1="8" y1="12" x2="16" y2="12"></line>
        </svg>
        <p class="text-gray-600 text-base">No se encontraron productos</p>
      </div>
    </div>

    <!-- Resumen -->
    <div class="mt-6 grid grid-cols-2 gap-4">
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Total</div>
        <div class="text-2xl font-bold text-gray-900">{{ productos.length }}</div>
      </div>
      <div class="bg-white p-4 rounded-lg shadow">
        <div class="text-sm text-gray-600">Activos</div>
        <div class="text-2xl font-bold text-green-600">{{ productosActivos }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const productos = ref([]);
const busqueda = ref('');
const cargando = ref(true);

onMounted(async () => {
  try {
    // TODO: Reemplazar con llamada al backend
    // const response = await productoService.getProductos();
    // productos.value = response.data;
    
    // Datos temporales vacíos
    productos.value = [];
    cargando.value = false;
  } catch (error) {
    console.error('Error al cargar productos:', error);
    cargando.value = false;
  }
});

const productosFiltrados = computed(() => {
  return productos.value.filter((producto) => {
    return producto.nombre.toLowerCase().includes(busqueda.value.toLowerCase()) ||
           producto.codigo.toLowerCase().includes(busqueda.value.toLowerCase());
  });
});

const productosActivos = computed(() => {
  return productos.value.filter(p => p.activo).length;
});
</script>
