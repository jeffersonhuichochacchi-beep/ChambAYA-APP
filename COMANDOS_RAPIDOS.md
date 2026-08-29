# ⚡ Comandos Rápidos Git - ChambAYA APP

## 🔥 Los 5 Comandos que Usarás Todos los Días

```bash
# 1. Cambiar a tu rama
git checkout feature/tu-nombre-apellido

# 2. Actualizar con develop
git pull origin develop

# 3. Ver tus cambios
git status

# 4. Guardar tus cambios
git add .
git commit -m "feat: descripción de tu cambio"

# 5. Subir a GitHub
git push origin feature/tu-nombre-apellido
```

---

## 📋 Secuencia Completa de Trabajo

```bash
# Mañana (inicio del día)
git checkout feature/leonel-paitan           # Ir a tu rama (ejemplo)
git pull origin develop                      # Actualizar

# Durante el trabajo
git status                                   # Ver cambios
git add .                                    # Agregar todo
git commit -m "feat: agregar nueva función"  # Guardar cambios
git push origin feature/tu-rama         # Subir a GitHub

# Tarde (antes de irte)
git push origin feature/tu-rama         # Asegurar que todo está en GitHub
```

---

## 🎨 Templates de Commits

```bash
# Nueva funcionalidad
git commit -m "feat: agregar pantalla de login"

# Corrección de error
git commit -m "fix: corregir error al cargar ofertas"

# Mejora de diseño
git commit -m "style: mejorar diseño de botones"

# Refactorización
git commit -m "refactor: optimizar código de adaptadores"

# Documentación
git commit -m "docs: agregar comentarios en RepositorioChambaya"
```

---

## 🚨 Comandos de Emergencia

```bash
# Descartar TODOS los cambios locales (¡CUIDADO!)
git reset --hard origin/feature/tu-rama

# Descartar cambios de un archivo específico
git checkout -- archivo.kt

# Ver qué cambiará antes de hacer pull
git fetch origin
git diff feature/tu-rama origin/develop

# Guardar cambios temporalmente (sin commit)
git stash                    # Guardar
git stash pop                # Recuperar

# Ver últimos 10 commits
git log --oneline -10
```

---

## 🔄 Actualizar tu Rama con Develop

```bash
# Opción 1: Pull (recomendado)
git checkout feature/tu-rama
git pull origin develop

# Opción 2: Fetch + Merge
git fetch origin
git merge origin/develop
```

---

## 🌿 Ver y Cambiar Ramas

```bash
# Ver rama actual
git branch

# Ver todas las ramas
git branch -a

# Cambiar de rama
git checkout nombre-rama

# Crear nueva rama (si necesitas)
git checkout -b feature/nueva-funcionalidad
```

---

## 📤 Subir Cambios a GitHub

```bash
# Primera vez (establecer tracking)
git push -u origin feature/tu-rama

# Después de la primera vez
git push
```

---

## 🔍 Ver Información

```bash
# Ver archivos modificados
git status

# Ver diferencias antes de commit
git diff

# Ver diferencias después de add
git diff --staged

# Ver historial completo
git log

# Ver historial resumido
git log --oneline --graph --all
```

---

## 👥 Trabajar con Pull Requests

```bash
# 1. Asegurar que tu rama está actualizada
git checkout feature/tu-rama
git pull origin develop
git push origin feature/tu-rama

# 2. Ir a GitHub y crear PR desde tu rama hacia develop

# 3. Después del merge, actualizar tu rama
git pull origin develop
```

---

## 🎯 Checklist Antes de Push

```bash
# 1. ¿Estoy en mi rama?
git branch

# 2. ¿Qué archivos cambié?
git status

# 3. ¿Los cambios son correctos?
git diff

# 4. ¿Mi código compila?
# Ejecutar app en Android Studio

# 5. Todo bien → Push
git add .
git commit -m "feat: descripción"
git push origin feature/integrante-X
```

---

## 🆘 Resolver Conflictos

```bash
# 1. Intentar actualizar
git pull origin develop

# 2. Si hay conflictos, Git te dirá qué archivos
git status

# 3. Abrir cada archivo con conflicto y buscar:
#    <<<<<<< HEAD (tus cambios)
#    ======= (separador)
#    >>>>>>> (cambios de develop)

# 4. Editar el archivo, eliminar las marcas, quedarte con el código correcto

# 5. Marcar como resuelto
git add archivo-resuelto.kt

# 6. Completar el merge
git commit -m "fix: resolver conflictos con develop"

# 7. Subir
git push origin feature/tu-rama
```

---

## 📱 Configuración Inicial (Solo Primera Vez)

```bash
# Configurar tu nombre y email
git config --global user.name "Tu Nombre"
git config --global user.email "tuemail@ejemplo.com"

# Clonar el proyecto
git clone https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP.git

# Entrar al proyecto
cd ChambAYA-APP

# Cambiar a tu rama
git checkout feature/integrante-X
```

---

## 💡 Tips Útiles

```bash
# Alias útiles (agregar a .gitconfig)
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.st status

# Ahora puedes usar:
git co feature/integrante-1   # En vez de checkout
git st                         # En vez de status
git ci -m "mensaje"           # En vez de commit
```

---

## 📊 Verificar Estado del Proyecto

```bash
# Ver todas las ramas y quién está trabajando en qué
git branch -a

# Ver últimos cambios en develop
git log origin/develop --oneline -10

# Ver quién modificó un archivo
git log --follow archivo.kt

# Ver cambios en un archivo específico
git log -p archivo.kt
```

---

## 🎓 Para Principiantes

Si es tu primera vez con Git:

```bash
# Paso 1: Configurar Git
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"

# Paso 2: Clonar proyecto
git clone https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP.git
cd ChambAYA-APP

# Paso 3: Ir a tu rama
git checkout feature/tu-nombre-apellido

# Paso 4: Trabajar normalmente
# (modificar archivos en Android Studio)

# Paso 5: Guardar cambios
git add .
git commit -m "feat: mi primer cambio"
git push origin feature/integrante-X
```

---

## 🔗 Enlaces Útiles

- **Repositorio:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP
- **Crear Pull Request:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/pulls
- **Ver ramas:** https://github.com/jeffersonhuichochacchi-beep/ChambAYA-APP/branches

---

**💾 Guarda este archivo en tus marcadores para acceso rápido!**
