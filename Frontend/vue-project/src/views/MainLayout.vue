<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header con usuario -->
    <header class="bg-white border-b border-gray-200 shadow-sm">
      <div class="max-w-[1800px] mx-auto px-4 py-3">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="bg-blue-600 p-2 rounded-lg">
              <svg class="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 22v-7c0-1.1.9-2 2-2h7a2 2 0 0 1 2 2v7"></path>
                <path d="M6 8V4c0-1.1.9-2 2-2h3c1.1 0 2 .9 2 2v4"></path>
                <line x1="6" y1="8" x2="13" y2="8"></line>
                <path d="M18 7v13"></path>
                <path d="M18 10l4-2v6l-4-2"></path>
              </svg>
            </div>
            <div>
              <h1 class="text-lg font-bold text-gray-900">Sistema de Carga de Cisternas</h1>
              <p class="text-xs text-gray-600">Monitoreo en tiempo real</p>
            </div>
          </div>

          <div class="flex items-center gap-3">
            <div class="flex items-center gap-2 px-3 py-2 bg-gray-100 rounded-lg">
              <svg class="w-4 h-4 text-gray-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              <div>
                <p class="text-sm font-medium text-gray-900">{{ user?.username }}</p>
                <p class="text-xs text-gray-600">{{ user?.email }}</p>
              </div>
            </div>

            <button
              @click="handleLogout"
              class="flex items-center gap-2 px-3 py-2 bg-red-50 text-red-700 rounded-lg hover:bg-red-100 transition-colors text-sm"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                <polyline points="16 17 21 12 16 7"></polyline>
                <line x1="21" y1="12" x2="9" y2="12"></line>
              </svg>
              <span class="font-medium">Cerrar Sesión</span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Menú de navegación -->
    <nav class="bg-white border-b border-gray-200 shadow-sm">
      <div class="max-w-[1800px] mx-auto px-4">
        <div class="flex gap-1">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="px-4 py-3 text-sm font-medium transition-colors border-b-2"
            :class="isActive(item.path) 
              ? 'text-blue-600 border-blue-600' 
              : 'text-gray-600 hover:text-gray-900 border-transparent hover:border-gray-300'"
          >
            <div class="flex items-center gap-2">
              <component :is="item.icon" class="w-4 h-4" />
              <span>{{ item.label }}</span>
            </div>
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Contenido -->
    <div class="max-w-[1800px] mx-auto px-4 py-6">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import authService from '@/services/authService';

const router = useRouter();
const route = useRoute();
const user = ref(null);

const menuItems = [
  {
    label: 'Camiones',
    path: '/dashboard/camiones',
    icon: 'TruckIcon'
  },
  {
    label: 'Choferes',
    path: '/dashboard/choferes',
    icon: 'UserIcon'
  },
  {
    label: 'Cisternas',
    path: '/dashboard/cisternas',
    icon: 'CylinderIcon'
  },
  {
    label: 'Productos',
    path: '/dashboard/productos',
    icon: 'PackageIcon'
  },
  {
    label: 'Órdenes',
    path: '/dashboard/ordenes',
    icon: 'ListIcon'
  }
];

onMounted(() => {
  user.value = authService.getCurrentUser();
});

const isActive = (path) => {
  return route.path.startsWith(path);
};

const handleLogout = () => {
  authService.logout();
  router.push({ name: 'Login' });
};
</script>

<script>
// Iconos SVG como componentes
const TruckIcon = {
  template: `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <rect x="1" y="3" width="15" height="13"></rect>
      <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
      <circle cx="5.5" cy="18.5" r="2.5"></circle>
      <circle cx="18.5" cy="18.5" r="2.5"></circle>
    </svg>
  `
};

const UserIcon = {
  template: `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
      <circle cx="12" cy="7" r="4"></circle>
    </svg>
  `
};

const CylinderIcon = {
  template: `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M3 22v-7c0-1.1.9-2 2-2h7a2 2 0 0 1 2 2v7"></path>
      <path d="M6 8V4c0-1.1.9-2 2-2h3c1.1 0 2 .9 2 2v4"></path>
      <line x1="6" y1="8" x2="13" y2="8"></line>
      <path d="M18 7v13"></path>
    </svg>
  `
};

const PackageIcon = {
  template: `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
      <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
      <line x1="12" y1="22.08" x2="12" y2="12"></line>
    </svg>
  `
};

const ListIcon = {
  template: `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <line x1="8" y1="6" x2="21" y2="6"></line>
      <line x1="8" y1="12" x2="21" y2="12"></line>
      <line x1="8" y1="18" x2="21" y2="18"></line>
      <line x1="3" y1="6" x2="3.01" y2="6"></line>
      <line x1="3" y1="12" x2="3.01" y2="12"></line>
      <line x1="3" y1="18" x2="3.01" y2="18"></line>
    </svg>
  `
};

export default {
  components: {
    TruckIcon,
    UserIcon,
    CylinderIcon,
    PackageIcon,
    ListIcon
  }
};
</script>
