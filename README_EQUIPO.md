# 🚀 ChambAYA APP - Guía para el Equipo de Desarrollo

## 👥 Equipo de 8 Desarrolladores

Este proyecto está configurado para que **8 integrantes trabajen simultáneamente** sin afectar el código de los demás.

---

## 📚 Documentación Disponible

Hemos creado 3 guías completas para ti:

### 1. 📖 [GUIA_RAMAS_GITHUB.md](./GUIA_RAMAS_GITHUB.md)
**Lee esto PRIMERO si es tu primera vez con Git**
- Explicación completa del flujo de trabajo
- Convención de commits
- Solución de problemas comunes
- Reglas importantes del equipo

### 2. ⚡ [COMANDOS_RAPIDOS.md](./COMANDOS_RAPIDOS.md)
**Guarda este archivo en favoritos - lo usarás todos los días**
- Los 5 comandos esenciales
- Templates de commits
- Comandos de emergencia
- Checklist diario

### 3. 🎯 [ASIGNACION_AREAS.md](./ASIGNACION_AREAS.md)
**Revisa tu área de trabajo asignada**
- División de responsabilidades
- Archivos asignados a cada integrante
- Protocolo para archivos compartidos
- Cómo renombrar tu rama

---

## 🌳 Estructura de Ramas

```
📦 ChambAYA-APP (Repositorio)
│
├── 🔴 main (PRODUCCIÓN - NO TOCAR)
│   └── Solo para releases finales
│
├── 🟢 develop (INTEGRACIÓN)
│   └── Aquí se integra el trabajo de todos
│
└── 🔵 Ramas de Trabajo Individual:
    ├── feature/leonel-paitan → Leonel (Autenticación)
    ├── feature/jefferson-huicho → Jefferson (Perfil Trabajador)
    ├── feature/schiang-chavez → Schiang (Perfil Empleador)
    ├── feature/suker-llamocca → Suker (Sistema Chat)
    ├── feature/jhoel-quica → Jhoel (Navegación Principal)
    ├── feature/marco-camana → Marco (Notificaciones)
    ├── feature/nilton-taboada → Nilton (Ofertas/Búsqueda)
    └── feature/carlos-quispe → Carlos (Reseñas/Extras)
```

---

## 🚦 Inicio Rápido (Para Principiantes)

### Primera vez trabajando en el proyecto:

```bash
# 1. Clonar el proyecto (solo la primera vez)
git clone https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP.git
cd ChambAYA-APP

# 2. Ver qué ramas hay disponibles
git branch -a

# 3. Cambiar a TU rama asignada (ejemplo: Leonel)
git checkout feature/leonel-paitan

# 4. Abrir el proyecto en Android Studio
# File > Open > Seleccionar la carpeta del proyecto
```

### Cada día de trabajo:

```bash
# ANTES de empezar a trabajar:
git checkout feature/tu-nombre-apellido
git pull origin develop

# Trabajar normalmente en Android Studio...

# DESPUÉS de hacer cambios:
git status                                    # Ver qué cambió
git add .                                     # Agregar todos los cambios
git commit -m "feat: descripción del cambio"  # Guardar cambios
git push origin feature/tu-nombre-apellido          # Subir a GitHub
```

---

## 📋 Reglas de Oro

### ✅ SÍ PUEDES:
- Trabajar libremente en TU rama asignada
- Hacer commits y push cuando quieras
- Probar y experimentar en tu rama
- Pedir ayuda a tus compañeros

### ❌ NUNCA HAGAS:
- Push directo a `main` ← **PROHIBIDO**
- Push directo a `develop` ← **PROHIBIDO**
- Trabajar en la rama de otro compañero sin avisar
- Hacer merge sin aprobación del equipo

---

## 🔄 Flujo de Trabajo Semanal

```
Lunes: 
├── Pull de develop
├── Planificar tareas de la semana
└── Empezar a trabajar en tu rama

Martes-Jueves:
├── Trabajar en tu rama
├── Commits frecuentes
└── Push diario

Viernes:
├── Completar funcionalidades
├── Crear Pull Request
├── Code Review en equipo
└── Merge a develop (si está aprobado)
```

---

## 🆘 ¿Necesitas Ayuda?

### Problemas Comunes:

**❓ No sé en qué rama estoy**
```bash
git branch
# La rama con * es tu rama actual
```

**❓ Tengo conflictos al hacer pull**
```bash
# Ver archivos con conflicto
git status

# Abrir archivo, resolver conflictos manualmente
# Buscar las marcas: <<<<<<< HEAD

# Después de resolver
git add archivo-resuelto.kt
git commit -m "fix: resolver conflictos"
git push origin feature/integrante-X
```

**❓ Quiero descartar mis cambios locales**
```bash
# CUIDADO: Esto borra tus cambios
git checkout -- archivo.kt  # Para un archivo específico
git reset --hard            # Para todos los archivos
```

**❓ Olvidé hacer pull antes de trabajar**
```bash
git stash              # Guardar cambios temporalmente
git pull origin develop # Actualizar
git stash pop          # Recuperar cambios
```

---

## 📊 Estado de las Ramas

Puedes ver todas las ramas en GitHub:
👉 https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/branches

O desde terminal:
```bash
git branch -a
```

---

## 🔗 Enlaces Importantes

- **📦 Repositorio:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP
- **🔀 Pull Requests:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/pulls
- **🌿 Ramas:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/branches
- **📝 Issues:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/issues

---

## 📱 Herramientas Recomendadas

- **Android Studio** - IDE principal
- **Git** - Control de versiones
- **GitHub Desktop** (opcional) - Si prefieres interfaz gráfica
- **GitKraken** (opcional) - Visualización de ramas

---

## 🎓 Recursos de Aprendizaje

Si eres nuevo en Git:
- 📺 [Git en 15 minutos](https://www.youtube.com/results?search_query=git+tutorial+español)
- 📖 [Git - La guía sencilla](https://rogerdudler.github.io/git-guide/index.es.html)
- 🎮 [Learn Git Branching](https://learngitbranching.js.org/?locale=es_ES) (interactivo)

---

## 💬 Comunicación del Equipo

### Canales:
- **WhatsApp/Telegram:** Comunicación diaria
- **GitHub Issues:** Reportar bugs y solicitudes
- **Pull Requests:** Code review y discusión técnica

### Buenas prácticas:
- Avisar cuando vas a modificar archivos compartidos
- Compartir avances semanalmente
- Pedir code review antes de hacer merge
- Documentar decisiones técnicas importantes

---

## 📅 Próximos Pasos

1. **Cada integrante:**
   - [ ] Clonar el repositorio
   - [ ] Checkout a su rama asignada
   - [ ] Leer las 3 guías de documentación
   - [ ] Configurar Android Studio
   - [ ] Hacer un commit de prueba

2. **Como equipo:**
   - [ ] Reunión para definir naming conventions
   - [ ] Acordar estructura de base de datos
   - [ ] Definir sprints de 2 semanas
   - [ ] Establecer horarios de code review

---

## 📈 Progreso del Proyecto

| Rama | Responsable | Estado | Última Actualización |
|------|-------------|--------|---------------------|
| feature/leonel-paitan | Leonel Paitan | 🟡 En progreso | - |
| feature/jefferson-huicho | Jefferson Huicho | 🟡 En progreso | - |
| feature/schiang-chavez | Schiang Chavez | 🟡 En progreso | - |
| feature/suker-llamocca | Suker Llamocca | 🟡 En progreso | - |
| feature/jhoel-quica | Jhoel Quica | 🟡 En progreso | - |
| feature/marco-camana | Marco Camana | 🟡 En progreso | - |
| feature/nilton-taboada | Nilton Taboada | 🟡 En progreso | - |
| feature/carlos-quispe | Carlos Quispe | 🟡 En progreso | - |

**Leyenda:**
- 🟢 Completado
- 🟡 En progreso
- 🔴 Bloqueado
- ⚪ No iniciado

---

## 🎯 Metas del Proyecto

### Sprint 1 (Semanas 1-2):
- [ ] Configuración del proyecto
- [ ] Estructura básica de navegación
- [ ] Sistema de autenticación
- [ ] Perfiles básicos (trabajador y empleador)

### Sprint 2 (Semanas 3-4):
- [ ] Sistema de chat funcional
- [ ] Publicación y búsqueda de ofertas
- [ ] Sistema de notificaciones
- [ ] Integración de componentes

### Sprint 3 (Semanas 5-6):
- [ ] Sistema de reseñas y calificaciones
- [ ] Funcionalidades premium
- [ ] Optimizaciones
- [ ] Testing y bug fixes

---

## 🏆 Créditos

**Equipo de Desarrollo ChambAYA:**
1. Leonel Paitan - Autenticación
2. Jefferson Huicho - Perfil Trabajador
3. Schiang Chavez - Perfil Empleador
4. Suker Llamocca - Sistema Chat
5. Jhoel Quica - Navegación
6. Marco Camana - Notificaciones
7. Nilton Taboada - Ofertas/Búsqueda
8. Carlos Quispe - Reseñas/Extras

---

## 📞 Contacto

Para dudas o problemas técnicos, contactar al líder del proyecto o abrir un issue en GitHub.

---

**Fecha de inicio:** 28 de Agosto, 2026  
**Última actualización:** 28 de Agosto, 2026

---

## 🎉 ¡Bienvenido al equipo!

Recuerda: **La comunicación es clave para el éxito del proyecto**. 

Ante cualquier duda, pregunta. Es mejor preguntar que romper el código. 😊

**¡Vamos a crear una app increíble juntos! 🚀**
