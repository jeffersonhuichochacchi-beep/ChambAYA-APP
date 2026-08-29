# 👥 Asignación de Áreas de Trabajo - ChambAYA APP

## 📋 Objetivo
Dividir el trabajo entre los 8 integrantes para minimizar conflictos y trabajar eficientemente.

---

## 🗂️ Estructura del Proyecto

```
app/src/main/java/com/example/chambaya/
├── Activities (Pantallas principales)
│   ├── BienvenidaActivity.kt
│   ├── LoginActivity.kt
│   ├── MainActivity.kt
│   └── SplashActivity.kt
│
├── data/ (Modelos y datos)
│   ├── model/
│   └── repository/
│
└── ui/ (Interfaces de usuario)
    ├── adapters/ (Adaptadores RecyclerView)
    ├── chat/ (Sistema de chat)
    ├── dialogs/ (Diálogos)
    ├── empleador/ (Funcionalidad empleador)
    ├── fragments/ (Fragmentos generales)
    ├── home/ (Pantalla inicio)
    ├── notifications/ (Notificaciones)
    └── trabajador/ (Funcionalidad trabajador)
```

---

## 🎯 Distribución Sugerida de Trabajo

### 👤 Integrante 1: Sistema de Autenticación y Onboarding
**Rama:** `feature/integrante-1` → Renombrar a: `feature/auth-onboarding`

**Responsabilidades:**
- `SplashActivity.kt` - Pantalla de inicio
- `BienvenidaActivity.kt` - Pantalla de bienvenida
- `LoginActivity.kt` - Sistema de login
- Integración con Firebase Authentication (si se usa)

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/SplashActivity.kt`
- `app/src/main/java/com/example/chambaya/BienvenidaActivity.kt`
- `app/src/main/java/com/example/chambaya/LoginActivity.kt`
- `app/src/main/res/layout/activity_splash.xml`
- `app/src/main/res/layout/activity_bienvenida.xml`
- `app/src/main/res/layout/activity_login.xml`

---

### 👤 Integrante 2: Perfil y Gestión de Trabajador
**Rama:** `feature/integrante-2` → Renombrar a: `feature/trabajador-profile`

**Responsabilidades:**
- Perfil de trabajador
- Edición de información personal
- Gestión de habilidades
- Historial de trabajos

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/ui/trabajador/`
- `app/src/main/java/com/example/chambaya/data/model/PerfilTrabajador.kt`
- Layouts relacionados con trabajador

---

### 👤 Integrante 3: Perfil y Gestión de Empleador
**Rama:** `feature/integrante-3` → Renombrar a: `feature/empleador-profile`

**Responsabilidades:**
- Perfil de empleador
- Publicación de ofertas
- Gestión de ofertas publicadas
- Revisión de postulantes

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/ui/empleador/`
- `app/src/main/java/com/example/chambaya/data/model/PerfilEmpleador.kt`
- `app/src/main/java/com/example/chambaya/data/model/OfertaTrabajo.kt`
- Layouts relacionados con empleador

---

### 👤 Integrante 4: Sistema de Chat
**Rama:** `feature/integrante-4` → Renombrar a: `feature/chat-system`

**Responsabilidades:**
- Lista de conversaciones
- Pantalla de chat individual
- Envío y recepción de mensajes
- Notificaciones de mensajes

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/ui/chat/`
- `app/src/main/java/com/example/chambaya/data/model/ModelosChat.kt`
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorConversacionChat.kt`
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorMensajeChat.kt`

---

### 👤 Integrante 5: Pantalla Principal y Navegación
**Rama:** `feature/integrante-5` → Renombrar a: `feature/home-navigation`

**Responsabilidades:**
- MainActivity (navegación principal)
- Bottom navigation
- Pantalla de inicio (Home)
- Búsqueda general

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/MainActivity.kt`
- `app/src/main/java/com/example/chambaya/ui/home/`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/menu/bottom_navigation.xml`

---

### 👤 Integrante 6: Sistema de Notificaciones
**Rama:** `feature/integrante-6` → Renombrar a: `feature/notifications`

**Responsabilidades:**
- Pantalla de notificaciones
- Sistema de notificaciones push
- Gestión de preferencias de notificaciones
- Badges y contadores

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/ui/notifications/`
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorNotificaciones.kt`
- `app/src/main/java/com/example/chambaya/data/model/ModelosExtra.kt` (parte de notificaciones)

---

### 👤 Integrante 7: Gestión de Ofertas y Búsqueda
**Rama:** `feature/integrante-7` → Renombrar a: `feature/ofertas-busqueda`

**Responsabilidades:**
- Lista de ofertas de trabajo
- Filtros y búsqueda de ofertas
- Detalles de oferta
- Sistema de postulación

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorTrabajo.kt`
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorChipCategoria.kt`
- Pantallas de búsqueda y filtros

---

### 👤 Integrante 8: Sistema de Reseñas y Funcionalidades Extra
**Rama:** `feature/integrante-8` → Renombrar a: `feature/reviews-extras`

**Responsabilidades:**
- Sistema de calificaciones y reseñas
- Anuncios de negocios
- Plan premium
- Configuraciones adicionales

**Archivos principales:**
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorReseñas.kt`
- `app/src/main/java/com/example/chambaya/ui/adapters/AdaptadorAnunciosNegocios.kt`
- `app/src/main/java/com/example/chambaya/ui/dialogs/DialogoFragmentoCalificarTrabajo.kt`
- `app/src/main/java/com/example/chambaya/ui/dialogs/DialogoFragmentoPlanPremium.kt`
- `app/src/main/java/com/example/chambaya/ui/dialogs/DialogoFragmentoAnunciosNegocios.kt`

---

## 🔄 Áreas Compartidas (Requieren Coordinación)

### ⚠️ Repositorio de Datos
**Archivo:** `app/src/main/java/com/example/chambaya/data/repository/RepositorioChambaya.kt`

**Regla:** Antes de modificar, avisar al equipo en el grupo.

### ⚠️ Modelos de Datos
**Archivos en:** `app/src/main/java/com/example/chambaya/data/model/`

**Regla:** Coordinar cambios en los modelos para no romper código de otros.

### ⚠️ Resources (strings, colors, styles)
**Archivos en:** `app/src/main/res/values/`

**Regla:** Usar prefijos para evitar conflictos:
- Integrante 1: `auth_`, `onboarding_`
- Integrante 2: `trabajador_`
- Integrante 3: `empleador_`
- Integrante 4: `chat_`
- Integrante 5: `home_`, `nav_`
- Integrante 6: `notif_`
- Integrante 7: `oferta_`, `busqueda_`
- Integrante 8: `review_`, `premium_`

---

## 📞 Protocolo de Comunicación

### Antes de modificar archivos compartidos:
1. Avisar en el grupo de WhatsApp/Telegram
2. Verificar que nadie más esté trabajando en ese archivo
3. Hacer los cambios rápidamente
4. Commit y push inmediato
5. Avisar cuando terminaste

### Si necesitas cambiar código de otro integrante:
1. Preguntar primero
2. Crear un issue en GitHub explicando el cambio
3. Coordinar en qué momento hacer el cambio

---

## 🔄 Comandos para Renombrar tu Rama

```bash
# Paso 1: Ir a tu rama actual
git checkout feature/integrante-X

# Paso 2: Renombrar localmente
git branch -m feature/nuevo-nombre

# Paso 3: Eliminar rama antigua en GitHub
git push origin --delete feature/integrante-X

# Paso 4: Subir rama con nuevo nombre
git push origin -u feature/nuevo-nombre
```

**Ejemplo para Integrante 1:**
```bash
git checkout feature/integrante-1
git branch -m feature/auth-onboarding
git push origin --delete feature/integrante-1
git push origin -u feature/auth-onboarding
```

---

## 📊 Tabla de Ramas Actualizadas

| Integrante | Rama Original | Rama Sugerida | Área |
|------------|---------------|---------------|------|
| 1 | feature/integrante-1 | feature/auth-onboarding | Autenticación |
| 2 | feature/integrante-2 | feature/trabajador-profile | Perfil Trabajador |
| 3 | feature/integrante-3 | feature/empleador-profile | Perfil Empleador |
| 4 | feature/integrante-4 | feature/chat-system | Sistema Chat |
| 5 | feature/integrante-5 | feature/home-navigation | Navegación |
| 6 | feature/integrante-6 | feature/notifications | Notificaciones |
| 7 | feature/integrante-7 | feature/ofertas-busqueda | Ofertas/Búsqueda |
| 8 | feature/integrante-8 | feature/reviews-extras | Reseñas/Extras |

---

## ✅ Checklist de Coordinación

Cada semana:
- [ ] Revisar en grupo qué está haciendo cada uno
- [ ] Identificar archivos compartidos que necesitan cambios
- [ ] Planificar el orden de los merges a develop
- [ ] Hacer code review entre compañeros

Antes de cada Pull Request:
- [ ] Verificar que no hay conflictos con develop
- [ ] Probar la app completa
- [ ] Documentar cambios importantes
- [ ] Solicitar revisión de al menos 2 compañeros

---

## 🎯 Metas por Sprint (Sugerencia)

### Sprint 1 (Semana 1-2):
- Integrante 1: Login funcional
- Integrante 2 y 3: Perfiles básicos
- Integrante 4: Chat básico
- Integrante 5: Navegación funcional

### Sprint 2 (Semana 3-4):
- Integrante 6: Notificaciones básicas
- Integrante 7: Lista de ofertas
- Integrante 8: Sistema de reseñas
- Todos: Integración y pruebas

---

**Actualizado:** 28/08/2026  
**Recuerda:** La comunicación constante evita conflictos! 💬
