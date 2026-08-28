# Splash Screen - ChambAYA

## 📱 Implementación Completada

Se ha implementado un **splash screen profesional** para la app ChambAYA siguiendo las mejores prácticas de Android 12+.

## ✨ Características

- **Fondo rojo vibrante** (#E30613) que coincide con el diseño solicitado
- **Logo personalizado** centrado con el nombre "CHAMBAYA"
- **Sin pantallas en blanco** gracias al uso de la API nativa de Android Splash Screen
- **Animación suave** con duración de 1 segundo
- **Compatible con modo oscuro** (mismo diseño en ambos modos)
- **Compatibilidad con Android 12+** y versiones anteriores mediante la librería androidx.core:core-splashscreen

## 🎨 Diseño

El splash screen muestra:
- Fondo rojo sólido (#E30613)
- Logo circular blanco con el símbolo "C" en rojo
- Texto "CHAMBAYA" debajo del logo en blanco
- Diseño minimalista y profesional

## 🔧 Archivos Modificados/Creados

### 1. **Dependencia agregada** (`app/build.gradle.kts`)
```kotlin
implementation("androidx.core:core-splashscreen:1.0.1")
```

### 2. **MainActivity actualizada**
Se agregó la instalación del splash screen antes de `super.onCreate()`:
```kotlin
val splashScreen = installSplashScreen()
```

### 3. **AndroidManifest.xml**
El tema de la aplicación ahora apunta al tema del splash screen:
```xml
android:theme="@style/Theme.ChambAYA.Splash"
```

La MainActivity usa el tema normal:
```xml
android:theme="@style/Theme.ChambAYA"
```

### 4. **Recursos creados**

**Colores** (`res/values/colors.xml`):
- `splash_red` (#E30613)

**Drawables**:
- `splash_logo.xml` - Logo vectorial de ChambAYA
- `splash_background.xml` - Fondo rojo del splash

**Temas** (`res/values/themes.xml` y `res/values-night/themes.xml`):
- `Theme.ChambAYA.Splash` - Tema del splash screen

## 🚀 Cómo Funciona

1. Cuando el usuario abre la app, Android muestra instantáneamente el splash screen (sin pantalla en blanco)
2. El splash screen se muestra mientras la app inicializa
3. Después de 1 segundo o cuando la app está lista, el splash screen desaparece con una animación suave
4. La MainActivity aparece con la interfaz principal de la app

## ⚙️ Personalización

### Cambiar la duración del splash
En `themes.xml`, modifica:
```xml
<item name="windowSplashScreenAnimationDuration">1000</item> <!-- milisegundos -->
```

### Cambiar el color de fondo
En `colors.xml`, modifica:
```xml
<color name="splash_red">#E30613</color>
```

### Mantener el splash screen más tiempo
En `MainActivity.onCreate()`, puedes agregar:
```kotlin
val splashScreen = installSplashScreen()
splashScreen.setKeepOnScreenCondition { 
    // Retorna true mientras quieras mantener el splash
    false 
}
```

## 📱 Pruebas

Para probar el splash screen:

1. **Compilar la app**:
```bash
./gradlew assembleDebug
```

2. **Instalar en dispositivo/emulador**:
```bash
./gradlew installDebug
```

3. **Cerrar completamente la app** y volver a abrirla desde el launcher

## ✅ Ventajas de esta Implementación

- ✨ **Experiencia profesional**: Sin pantallas en blanco
- 🎯 **API Nativa**: Usa la solución oficial de Android
- 🔄 **Retrocompatible**: Funciona en Android 5+ gracias a androidx
- 🎨 **Personalizable**: Fácil de modificar colores, logos y duración
- ⚡ **Optimizado**: No afecta el tiempo de inicio de la app
- 🌙 **Dark Mode Ready**: Funciona perfectamente en modo oscuro

## 📚 Referencias

- [Android Splash Screen API](https://developer.android.com/guide/topics/ui/splash-screen)
- [androidx.core:core-splashscreen](https://developer.android.com/jetpack/androidx/releases/core)
