import { createRouter, createWebHistory } from 'vue-router';
import authService from '@/services/authService';
import Login from '@/views/Login.vue';
import MainLayout from '@/views/MainLayout.vue';
import Camiones from '@/views/Camiones.vue';
import Choferes from '@/views/Choferes.vue';
import Cisternas from '@/views/Cisternas.vue';
import Productos from '@/views/Productos.vue';
import TankersMain from '@/views/TankersMain.vue';
import DetalleOrden from '@/views/DetalleOrden.vue';
import CrearOrden from '@/views/CrearOrden.vue';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard/ordenes'
      },
      {
        path: 'camiones',
        name: 'Camiones',
        component: Camiones,
        meta: { requiresAuth: true }
      },
      {
        path: 'choferes',
        name: 'Choferes',
        component: Choferes,
        meta: { requiresAuth: true }
      },
      {
        path: 'cisternas',
        name: 'Cisternas',
        component: Cisternas,
        meta: { requiresAuth: true }
      },
      {
        path: 'productos',
        name: 'Productos',
        component: Productos,
        meta: { requiresAuth: true }
      },
      {
        path: 'ordenes',
        name: 'Ordenes',
        component: TankersMain,
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/detalle-orden/:id',
    name: 'DetalleOrden',
    component: DetalleOrden,
    meta: { requiresAuth: true }
  },
  {
    path: '/crear-orden',
    name: 'CrearOrden',
    component: CrearOrden,
    meta: { requiresAuth: true }
  },
  // Mantener compatibilidad con ruta anterior
  {
    path: '/tankers-main',
    redirect: '/dashboard/ordenes'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Guardia de navegación para proteger rutas
router.beforeEach((to, from, next) => {
  const isAuthenticated = authService.isAuthenticated();
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    // Si la ruta requiere autenticación y el usuario no está autenticado, redirigir a login
    next({ name: 'Login' });
  } else if (to.name === 'Login' && isAuthenticated) {
    // Si el usuario ya está autenticado y trata de ir al login, redirigir al dashboard
    next({ path: '/dashboard' });
  } else {
    next();
  }
});

export default router;
