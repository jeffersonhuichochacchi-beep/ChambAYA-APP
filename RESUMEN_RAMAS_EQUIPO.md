# 🎯 ChambAYA APP - Ramas Asignadas por Integrante

## ✅ Configuración Completada

Las ramas han sido creadas y están listas para usar. Cada integrante tiene su propia rama en GitHub.

---

## 👥 Asignación de Ramas

| # | Integrante | Rama en GitHub | Área de Trabajo |
|---|------------|----------------|-----------------|
| 1 | **Leonel Paitan** | `feature/leonel-paitan` | 🔐 Autenticación y Onboarding |
| 2 | **Jefferson Huicho** | `feature/jefferson-huicho` | 👷 Perfil de Trabajador |
| 3 | **Schiang Chavez** | `feature/schiang-chavez` | 👔 Perfil de Empleador |
| 4 | **Suker Llamocca** | `feature/suker-llamocca` | 💬 Sistema de Chat |
| 5 | **Jhoel Quica** | `feature/jhoel-quica` | 🏠 Navegación Principal |
| 6 | **Marco Camana** | `feature/marco-camana` | 🔔 Notificaciones |
| 7 | **Nilton Taboada** | `feature/nilton-taboada` | 💼 Ofertas y Búsqueda |
| 8 | **Carlos Quispe** | `feature/carlos-quispe` | ⭐ Reseñas y Extras |

---

## 🚀 Cómo Empezar (Para cada integrante)

### Paso 1: Clonar el proyecto (si aún no lo tienes)
```bash
git clone https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP.git
cd ChambAYA-APP
```

### Paso 2: Cambiar a tu rama
```bash
# Ejemplo para Leonel:
git checkout feature/leonel-paitan

# Ejemplo para Jefferson:
git checkout feature/jefferson-huicho

# Y así sucesivamente...
```

### Paso 3: Empezar a trabajar
Abre el proyecto en Android Studio y comienza a programar en tu área asignada.

---

## 📝 Comandos Básicos Diarios

### Antes de empezar a trabajar cada día:
```bash
git checkout feature/tu-nombre-apellido
git pull origin develop
```

### Guardar y subir tus cambios:
```bash
git add .
git commit -m "feat: descripción de lo que hiciste"
git push origin feature/tu-nombre-apellido
```

---

## 📚 Documentación Completa

Lee estos archivos para más detalles:

1. **README_EQUIPO.md** - Guía general del equipo
2. **GUIA_RAMAS_GITHUB.md** - Tutorial completo de Git
3. **COMANDOS_RAPIDOS.md** - Comandos que usarás diariamente
4. **ASIGNACION_AREAS.md** - Detalles de qué archivos tocar

---

## ⚠️ Reglas Importantes

### ✅ SÍ Hacer:
- Trabajar **solo** en tu rama
- Hacer commits frecuentes
- Actualizar con develop diariamente
- Subir tus cambios a GitHub

### ❌ NO Hacer:
- **NUNCA** hacer push directo a `main`
- **NUNCA** hacer push directo a `develop`
- No trabajar en la rama de otro compañero
- No hacer commits sin mensaje descriptivo

---

## 🔄 Flujo de Integración

```
Tu rama (feature/tu-nombre)
    ↓
  Trabajas y haces commits
    ↓
  Push a GitHub
    ↓
  Crear Pull Request → develop
    ↓
  Code Review del equipo
    ↓
  Aprobación
    ↓
  Merge a develop
    ↓
  Todos actualizan desde develop
```

---

## 🎯 Responsabilidades por Área

### 🔐 Leonel Paitan - Autenticación
- SplashActivity.kt
- BienvenidaActivity.kt
- LoginActivity.kt
- Pantallas de onboarding

### 👷 Jefferson Huicho - Perfil Trabajador
- ui/trabajador/
- PerfilTrabajador.kt
- Gestión de habilidades
- Historial de trabajos

### 👔 Schiang Chavez - Perfil Empleador
- ui/empleador/
- PerfilEmpleador.kt
- Publicación de ofertas
- Gestión de postulantes

### 💬 Suker Llamocca - Sistema Chat
- ui/chat/
- ModelosChat.kt
- AdaptadorConversacionChat.kt
- AdaptadorMensajeChat.kt

### 🏠 Jhoel Quica - Navegación Principal
- MainActivity.kt
- ui/home/
- Bottom navigation
- Búsqueda general

### 🔔 Marco Camana - Notificaciones
- ui/notifications/
- AdaptadorNotificaciones.kt
- Push notifications
- Preferencias de notificaciones

### 💼 Nilton Taboada - Ofertas y Búsqueda
- AdaptadorTrabajo.kt
- AdaptadorChipCategoria.kt
- Filtros y búsqueda
- Detalles de oferta

### ⭐ Carlos Quispe - Reseñas y Extras
- AdaptadorReseñas.kt
- AdaptadorAnunciosNegocios.kt
- Sistema de calificaciones
- Plan premium

---

## 🔗 Enlaces Útiles

- **Repositorio:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP
- **Ver ramas:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/branches
- **Pull Requests:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/pulls

---

## 📞 Comunicación

### Antes de modificar archivos compartidos:
- Avisar en el grupo
- Verificar que nadie más esté trabajando en eso
- Hacer cambios rápidamente y subir

### Archivos compartidos (requieren coordinación):
- `RepositorioChambaya.kt`
- Modelos en `data/model/`
- Resources en `res/values/`

---

## 🆘 ¿Problemas?

### Olvidé hacer pull antes de trabajar:
```bash
git stash
git pull origin develop
git stash pop
```

### Tengo conflictos:
```bash
# Abrir archivos con conflicto
# Buscar <<<<<<< y resolver manualmente
git add archivo-resuelto.kt
git commit -m "fix: resolver conflictos"
git push origin feature/tu-rama
```

### Quiero descartar mis cambios:
```bash
git checkout -- archivo.kt  # Para un archivo
git reset --hard           # Para todos (¡CUIDADO!)
```

---

## ✅ Checklist Inicial

Cada integrante debe:
- [ ] Clonar el repositorio
- [ ] Hacer checkout a su rama
- [ ] Leer README_EQUIPO.md
- [ ] Leer GUIA_RAMAS_GITHUB.md
- [ ] Configurar Git con su nombre y email
- [ ] Hacer un commit de prueba
- [ ] Verificar que puede hacer push

---

## 📅 Próxima Reunión

**Sugerencia:** Agendar reunión para:
- Definir convenciones de código
- Acordar estructura de base de datos
- Establecer sprints
- Asignar tareas específicas

---

## 🎉 ¡Listos para Empezar!

Todas las ramas están creadas y listas. Cada uno puede empezar a trabajar en su área sin afectar a los demás.

**Recuerda:** La comunicación es clave. Ante cualquier duda, pregunta al equipo.

---

**Fecha:** 28 de Agosto, 2026  
**Repositorio:** ChambAYA-APP  
**Integrantes:** 8  
**Estado:** ✅ Configurado y listo
