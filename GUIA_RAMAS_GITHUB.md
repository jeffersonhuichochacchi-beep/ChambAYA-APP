# 📋 Guía de Trabajo con Ramas - ChambAYA APP

## 🌳 Estructura de Ramas del Proyecto

### Ramas Principales
- **`main`** - Rama de producción (código estable y listo para release)
- **`develop`** - Rama de desarrollo (integración de todas las características)

### Ramas de Trabajo Individual (8 integrantes)
- `feature/leonel-paitan` - Leonel Paitan
- `feature/jefferson-huicho` - Jefferson Huicho
- `feature/schiang-chavez` - Schiang Chavez
- `feature/suker-llamocca` - Suker Llamocca
- `feature/jhoel-quica` - Jhoel Quica
- `feature/marco-camana` - Marco Camana
- `feature/nilton-taboada` - Nilton Taboada
- `feature/carlos-quispe` - Carlos Quispe

---

## 👥 Asignación de Ramas

Cada integrante debe trabajar en su rama asignada. Recomendamos renombrar las ramas con nombres descriptivos:

| Integrante | Rama Actual | Área de Trabajo |
|------------|-------------|-----------------|
| Leonel Paitan | feature/leonel-paitan | Autenticación y Onboarding |
| Jefferson Huicho | feature/jefferson-huicho | Perfil Trabajador |
| Schiang Chavez | feature/schiang-chavez | Perfil Empleador |
| Suker Llamocca | feature/suker-llamocca | Sistema Chat |
| Jhoel Quica | feature/jhoel-quica | Navegación Principal |
| Marco Camana | feature/marco-camana | Notificaciones |
| Nilton Taboada | feature/nilton-taboada | Ofertas/Búsqueda |
| Carlos Quispe | feature/carlos-quispe | Reseñas/Extras |

---

## 🚀 Flujo de Trabajo Git

### 1️⃣ Configuración Inicial (Solo la primera vez)

```bash
# Clonar el repositorio
git clone https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP.git
cd ChambAYA-APP

# Ver todas las ramas disponibles
git branch -a

# Cambiar a tu rama asignada (ejemplo: leonel-paitan)
git checkout feature/leonel-paitan
```

### 2️⃣ Antes de Empezar a Trabajar (Todos los días)

```bash
# Asegurarte de estar en tu rama
git checkout feature/leonel-paitan

# Actualizar tu rama con los últimos cambios de develop
git pull origin develop

# Si hay conflictos, resuélvelos antes de continuar
```

### 3️⃣ Mientras Trabajas

```bash
# Ver archivos modificados
git status

# Agregar archivos específicos
git add archivo1.kt archivo2.xml

# O agregar todos los cambios
git add .

# Hacer commit con mensaje descriptivo
git commit -m "feat: descripción clara de lo que hiciste"

# Subir cambios a tu rama en GitHub
git push origin feature/tu-rama
```

### 4️⃣ Crear Pull Request (Cuando termines una funcionalidad)

1. Ve a GitHub: https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP
2. Haz clic en "Pull requests" → "New pull request"
3. Selecciona:
   - Base: `develop`
   - Compare: `feature/tu-rama` (tu rama)
4. Escribe un título y descripción clara
5. Solicita revisión de al menos 1 compañero
6. Espera aprobación antes de hacer merge

### 5️⃣ Después del Merge

```bash
# Volver a tu rama
git checkout feature/tu-rama

# Actualizar con los cambios integrados en develop
git pull origin develop
```

---

## 📝 Convención de Commits

Usa estos prefijos para mensajes de commit:

- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de errores
- `style:` - Cambios de formato, estilos
- `refactor:` - Refactorización de código
- `docs:` - Documentación
- `test:` - Agregar o modificar tests

**Ejemplos:**
```bash
git commit -m "feat: agregar pantalla de perfil de trabajador"
git commit -m "fix: corregir error en login"
git commit -m "style: mejorar diseño de botones"
```

---

## ⚠️ Reglas Importantes

### ✅ SÍ HACER:
- Trabajar SOLO en tu rama asignada
- Hacer commits frecuentes con mensajes claros
- Hacer pull de develop antes de empezar a trabajar
- Crear Pull Request para integrar tu código
- Pedir revisión de código antes del merge
- Resolver conflictos antes de hacer push

### ❌ NO HACER:
- **NUNCA** hacer push directamente a `main`
- **NUNCA** hacer push directamente a `develop`
- No trabajar en la rama de otro integrante
- No hacer commits con mensajes vagos como "fix" o "update"
- No hacer merge sin revisión

---

## 🔧 Comandos Útiles

```bash
# Ver en qué rama estás
git branch

# Ver todas las ramas (incluyendo remotas)
git branch -a

# Cambiar de rama
git checkout nombre-rama

# Ver el estado de tus cambios
git status

# Ver historial de commits
git log --oneline

# Descartar cambios locales (¡CUIDADO!)
git checkout -- archivo.kt

# Ver diferencias antes de commit
git diff

# Actualizar lista de ramas remotas
git fetch origin
```

---

## 🆘 Solución de Problemas Comunes

### Problema: Tengo conflictos al hacer pull

```bash
# 1. Ver los archivos con conflicto
git status

# 2. Abrir los archivos marcados y resolver conflictos manualmente
# Busca las marcas: <<<<<<< HEAD, =======, >>>>>>>

# 3. Después de resolver, agregar los archivos
git add archivo-resuelto.kt

# 4. Hacer commit
git commit -m "fix: resolver conflictos con develop"

# 5. Subir cambios
git push origin feature/integrante-X
```

### Problema: Hice commit en la rama equivocada

```bash
# 1. Copiar el hash del commit que hiciste mal
git log --oneline

# 2. Cambiar a tu rama correcta
git checkout feature/integrante-X

# 3. Aplicar el commit
git cherry-pick HASH_DEL_COMMIT

# 4. Volver a la rama equivocada y revertir
git checkout rama-equivocada
git revert HASH_DEL_COMMIT
```

### Problema: Olvidé hacer pull antes de trabajar

```bash
# 1. Guardar tus cambios actuales
git stash

# 2. Actualizar tu rama
git pull origin develop

# 3. Recuperar tus cambios
git stash pop

# 4. Resolver conflictos si los hay
```

---

## 📊 Flujo Visual del Proyecto

```
main (producción)
  ↑
  └── develop (integración)
       ↑
       ├── feature/integrante-1
       ├── feature/integrante-2
       ├── feature/integrante-3
       ├── feature/integrante-4
       ├── feature/integrante-5
       ├── feature/integrante-6
       ├── feature/integrante-7
       └── feature/integrante-8
```

---

## 📞 Contacto y Coordinación

Para evitar conflictos mayores:
- Comunica en qué archivos estás trabajando
- Coordina con el equipo si necesitas modificar archivos compartidos
- Revisa los Pull Requests de tus compañeros

---

## 🎯 Checklist Diario

Antes de empezar a trabajar:
- [ ] `git checkout feature/integrante-X`
- [ ] `git pull origin develop`
- [ ] Verificar que no haya conflictos

Durante el trabajo:
- [ ] Commits frecuentes con mensajes claros
- [ ] `git push origin feature/integrante-X` regularmente

Al terminar el día:
- [ ] `git push origin feature/integrante-X`
- [ ] Crear Pull Request si completaste una funcionalidad

---

**Fecha de creación:** 28/08/2026  
**Repositorio:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP.git

**¡Recuerda:** La comunicación con el equipo es clave para evitar conflictos! 🚀
