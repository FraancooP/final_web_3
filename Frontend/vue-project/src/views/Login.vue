<template>
  <div class="login-container">
    <div class="animated-bg">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-card">
      <div class="login-header">
        <div class="icon-container">
          <svg class="fuel-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 22v-7c0-1.1.9-2 2-2h7a2 2 0 0 1 2 2v7"></path>
            <path d="M6 8V4c0-1.1.9-2 2-2h3c1.1 0 2 .9 2 2v4"></path>
            <line x1="6" y1="8" x2="13" y2="8"></line>
            <path d="M18 7v13"></path>
            <path d="M18 10l4-2v6l-4-2"></path>
          </svg>
        </div>
        <h1 class="title">Tankers</h1>
        <p class="subtitle">Sistema de Gestión de Carga de Combustible</p>
      </div>

      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label for="username">Usuario o Correo Electronico</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="admin"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="admin"
            class="form-input"
          />
          <a href="#" class="forgot-password" @click.prevent="handleForgotPassword">
            ¿Olvidaste tu contraseña?
          </a>
        </div>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          <span>{{ loading ? 'Iniciando...' : 'Iniciar Sesión' }}</span>
          <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"></path>
          </svg>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import authService from '@/services/authService';

const router = useRouter();
const username = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

const handleSubmit = async () => {
  error.value = '';

  if (!username.value || !password.value) {
    error.value = 'Por favor ingresa usuario y contraseña';
    return;
  }

  loading.value = true;

  try {
    console.log('🔐 Intentando login con:', { username: username.value, url: 'http://localhost:8081/api/v1/auth/login' });
    await authService.login(username.value, password.value);
    console.log('✅ Login exitoso! Redirigiendo al dashboard...');
    // Redirigir al dashboard después del login exitoso
    router.push({ path: '/dashboard' });
  } catch (err) {
    console.error('❌ Error al iniciar sesión:', err);
    console.error('Error completo:', { 
      response: err.response, 
      request: err.request, 
      message: err.message,
      config: err.config 
    });
    
    if (err.response) {
      // El servidor respondió con un error
      if (err.response.status === 401) {
        error.value = 'Usuario o contraseña incorrectos. Intenta con: admin / admin';
      } else {
        error.value = `Error del servidor (${err.response.status}): ${err.response.data || err.message}`;
      }
    } else if (err.request) {
      // La petición se hizo pero no hubo respuesta
      error.value = 'No se puede conectar con el servidor. ¿Está el backend corriendo en http://localhost:8081?';
      console.error('La petición fue enviada pero no hubo respuesta. Verifica CORS o que el backend esté corriendo.');
    } else {
      // Error al configurar la petición
      error.value = 'Error al enviar la petición: ' + err.message;
    }
  } finally {
    loading.value = false;
  }
};

const handleForgotPassword = () => {
  alert('Funcionalidad de recuperar contraseña - Próximamente');
};
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: linear-gradient(135deg, #1e3a8a 0%, #0f172a 50%, #ea580c 100%);
  position: relative;
  overflow: hidden;
}

.animated-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  background: rgba(59, 130, 246, 0.1);
  animation-delay: 0s;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -150px;
  right: -150px;
  background: rgba(249, 115, 22, 0.1);
  animation-delay: 5s;
}

.circle-3 {
  width: 250px;
  height: 250px;
  top: 50%;
  right: 10%;
  background: rgba(59, 130, 246, 0.08);
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 30px) scale(0.9);
  }
}

.login-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 3rem;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3),
              0 0 0 1px rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 10;
  animation: slideIn 0.6s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.icon-container {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #3b82f6 0%, #f97316 100%);
  border-radius: 20px;
  margin-bottom: 1.5rem;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
  }
  50% {
    box-shadow: 0 12px 30px rgba(249, 115, 22, 0.4);
  }
}

.fuel-icon {
  width: 40px;
  height: 40px;
  color: white;
  stroke-width: 2.5;
}

.title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #1e40af 0%, #f97316 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
  letter-spacing: -0.02em;
}

.subtitle {
  color: #64748b;
  font-size: 0.95rem;
  font-weight: 500;
}

.login-form {
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: #334155;
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.forgot-password {
  display: inline-block;
  margin-top: 0.5rem;
  font-size: 0.875rem;
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
}

.forgot-password:hover {
  color: #f97316;
  text-decoration: underline;
}

.error-message {
  background: #fee2e2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 0.875rem 1rem;
  border-radius: 12px;
  font-size: 0.9rem;
  margin-bottom: 1.5rem;
  animation: shake 0.4s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10px); }
  75% { transform: translateX(10px); }
}

.submit-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #3b82f6 0%, #f97316 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

.arrow-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.submit-btn:hover .arrow-icon {
  transform: translateX(5px);
}

.demo-credentials {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(249, 115, 22, 0.1) 100%);
  padding: 1.25rem;
  border-radius: 12px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.demo-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: #1e40af;
  margin-bottom: 0.5rem;
}

.demo-item {
  font-size: 0.875rem;
  color: #475569;
  margin-bottom: 0.25rem;
}

.demo-value {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  color: #f97316;
  background: rgba(249, 115, 22, 0.1);
  padding: 0.15rem 0.5rem;
  border-radius: 6px;
}

/* Responsive para tablets y pantallas más pequeñas */
@media (max-width: 640px) {
  .login-card {
    padding: 2rem;
    max-width: 100%;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .icon-container {
    width: 70px;
    height: 70px;
  }
  
  .fuel-icon {
    width: 35px;
    height: 35px;
  }
}
</style>
