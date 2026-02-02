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
      <h2 class="text-2xl font-bold text-gray-900 mb-2">Crear Nueva Orden</h2>
      <p class="text-gray-600 text-sm">Complete el formulario para crear una orden de carga</p>
    </div>

    <!-- Formulario -->
    <div class="bg-white rounded-lg shadow p-6 max-w-2xl">
      <form @submit.prevent="crearOrden" class="space-y-6">
        <!-- Camión -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Camión
            <span class="text-red-500">*</span>
          </label>
          <select
            v-model="formData.camionId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">Seleccionar camión</option>
            <option v-for="camion in camionesDisponibles" :key="camion.id" :value="camion.id">
              {{ camion.patente }} - {{ camion.marca }} {{ camion.modelo }}
            </option>
          </select>
        </div>

        <!-- Chofer -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Chofer
            <span class="text-red-500">*</span>
          </label>
          <select
            v-model="formData.choferId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">Seleccionar chofer</option>
            <option v-for="chofer in choferesDisponibles" :key="chofer.id" :value="chofer.id">
              {{ chofer.nombre }} - {{ chofer.documento }}
            </option>
          </select>
        </div>

        <!-- Cisterna -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Cisterna
            <span class="text-red-500">*</span>
          </label>
          <select
            v-model="formData.cisternaId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">Seleccionar cisterna</option>
            <option v-for="cisterna in cisternasDisponibles" :key="cisterna.id" :value="cisterna.id">
              {{ cisterna.codigo }} - Capacidad: {{ formatNumber(cisterna.capacidad) }} L
            </option>
          </select>
        </div>

        <!-- Producto -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Producto
            <span class="text-red-500">*</span>
          </label>
          <select
            v-model="formData.productoId"
            required
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">Seleccionar producto</option>
            <option v-for="producto in productos" :key="producto.id" :value="producto.id">
              {{ producto.nombre }} - {{ producto.codigo }}
            </option>
          </select>
        </div>

        <!-- Preset -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Preset (Litros)
            <span class="text-red-500">*</span>
          </label>
          <input
            v-model.number="formData.preset"
            type="number"
            required
            min="1"
            step="0.01"
            placeholder="Ejemplo: 30000"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- Temperatura Crítica -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Temperatura Crítica (°C)
            <span class="text-gray-500 text-xs font-normal">(Opcional - Para alarmas)</span>
          </label>
          <input
            v-model.number="formData.temperaturaCritica"
            type="number"
            step="0.1"
            placeholder="Ejemplo: 25.0"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <p class="mt-1 text-xs text-gray-500">
            Se generará una alarma si la temperatura supera este valor
          </p>
        </div>

        <!-- Error message -->
        <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
          <p class="text-red-800 text-sm">{{ error }}</p>
        </div>

        <!-- Botones -->
        <div class="flex gap-3 pt-4">
          <button
            type="submit"
            :disabled="enviando"
            class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors font-medium"
          >
            <span v-if="!enviando">Crear Orden</span>
            <span v-else class="flex items-center justify-center gap-2">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Creando...
            </span>
          </button>
          <button
            type="button"
            @click="volver"
            :disabled="enviando"
            class="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 disabled:bg-gray-100 disabled:cursor-not-allowed transition-colors font-medium"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const camionesDisponibles = ref([]);
const choferesDisponibles = ref([]);
const cisternasDisponibles = ref([]);
const productos = ref([]);

const formData = ref({
  camionId: '',
  choferId: '',
  cisternaId: '',
  productoId: '',
  preset: '',
  temperaturaCritica: ''
});

const enviando = ref(false);
const error = ref('');

onMounted(async () => {
  try {
    // TODO: Reemplazar con llamadas al backend
    // const [camionesRes, choferesRes, cisternasRes, productosRes] = await Promise.all([
    //   camionService.getCamionesDisponibles(),
    //   choferService.getChoferesDisponibles(),
    //   cisternaService.getCisternasDisponibles(),
    //   productoService.getProductos()
    // ]);
    // camionesDisponibles.value = camionesRes.data;
    // choferesDisponibles.value = choferesRes.data;
    // cisternasDisponibles.value = cisternasRes.data;
    // productos.value = productosRes.data;
    
    // Datos temporales vacíos
    camionesDisponibles.value = [];
    choferesDisponibles.value = [];
    cisternasDisponibles.value = [];
    productos.value = [];
  } catch (err) {
    console.error('Error al cargar datos:', err);
    error.value = 'Error al cargar los datos necesarios para crear la orden';
  }
});

const crearOrden = async () => {
  error.value = '';
  enviando.value = true;

  try {
    // TODO: Reemplazar con llamada al backend
    // await ordenService.crearOrden(formData.value);
    
    console.log('Creando orden con datos:', formData.value);
    
    // Simular creación
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Redirigir a la lista de órdenes
    router.push({ name: 'Ordenes' });
  } catch (err) {
    console.error('Error al crear orden:', err);
    error.value = 'Error al crear la orden. Por favor, intenta nuevamente.';
  } finally {
    enviando.value = false;
  }
};

const volver = () => {
  router.push({ name: 'Ordenes' });
};

const formatNumber = (num) => {
  return new Intl.NumberFormat('es-ES').format(num);
};
</script>
