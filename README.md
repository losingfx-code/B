# 🎮 Tetris Android

Juego de Tetris completo para Android escrito en **Kotlin**, listo para compilar con Android Studio.

---

## 📋 Requisitos

| Herramienta | Versión mínima |
|---|---|
| Android Studio | Hedgehog (2023.1.1) o superior |
| JDK | 8+ (incluido en Android Studio) |
| Android SDK | API 24 (Android 7.0) |
| Gradle | 8.2 (se descarga automáticamente) |

---

## 🚀 Cómo compilar y generar el APK

### Paso 1 — Abrir el proyecto
1. Abre **Android Studio**
2. Selecciona **File → Open**
3. Navega hasta la carpeta `TetrisAndroid` y haz clic en **OK**
4. Espera a que Gradle sincronice (puede tardar 1-2 minutos la primera vez)

### Paso 2 — Compilar
- **APK de debug** (para probar rápido, no necesita firma):
  - Menú: `Build → Build Bundle(s) / APK(s) → Build APK(s)`
  - El APK se genera en: `app/build/outputs/apk/debug/app-debug.apk`

- **APK de release** (optimizado):
  - Menú: `Build → Generate Signed Bundle / APK`
  - Sigue el asistente para crear o usar un keystore

### Paso 3 — Instalar en el dispositivo
```bash
# Conecta tu teléfono por USB con depuración USB activada
adb install app/build/outputs/apk/debug/app-debug.apk
```
O arrastra el APK directamente al teléfono y ábrelo desde el gestor de archivos
(necesitas permitir "instalar desde fuentes desconocidas" en los ajustes).

---

## 🎮 Controles

### Botones en pantalla (parte inferior)
| Botón | Acción |
|---|---|
| ◀ | Mover izquierda |
| ▶ | Mover derecha |
| ↺ | Rotar pieza |
| ▼ | Caída suave (soft drop) |
| ⚡ | Caída instantánea (hard drop) |
| ⏸ | Pausar / Reanudar |

### Gestos táctiles
| Gesto | Acción |
|---|---|
| Deslizar ← → | Mover pieza |
| Deslizar ↑ | Rotar |
| Deslizar ↓ lento | Caída suave |
| Deslizar ↓ rápido | Caída instantánea |
| Tap rápido | Pausar / Reanudar |

---

## 🏆 Sistema de puntuación

| Líneas eliminadas | Puntos (×nivel) |
|---|---|
| 1 línea | 100 |
| 2 líneas | 300 |
| 3 líneas | 500 |
| 4 líneas (Tetris) | 800 |

- **Soft drop**: +1 punto por celda
- **Hard drop**: +2 puntos por celda
- El **nivel** sube cada 10 líneas
- La velocidad de caída aumenta con cada nivel

---

## 🏗️ Estructura del proyecto

```
TetrisAndroid/
├── app/src/main/
│   ├── java/com/tetris/game/
│   │   ├── MainActivity.kt     ← Actividad principal, botones, UI
│   │   ├── TetrisView.kt       ← SurfaceView, renderizado completo
│   │   ├── GameEngine.kt       ← Lógica del juego (board, colisiones, puntos)
│   │   ├── GameThread.kt       ← Hilo del juego a 60 fps
│   │   ├── Tetromino.kt        ← Las 7 piezas con todas sus rotaciones
│   │   └── GestureHandler.kt  ← Detección de gestos táctiles
│   └── res/
│       ├── layout/activity_main.xml
│       └── values/{strings, themes, colors}.xml
└── README.md
```

---

## ✨ Características

- ✅ Las 7 piezas estándar del Tetris (I, O, T, S, Z, J, L)
- ✅ Rotación con wall-kick
- ✅ Pieza fantasma (ghost piece) semitransparente
- ✅ Vista previa de la siguiente pieza
- ✅ Puntuación, líneas y nivel
- ✅ Velocidad progresiva por nivel
- ✅ Pausa automática al minimizar la app
- ✅ Botones táctiles + gestos de deslizamiento
- ✅ Diseño oscuro con efecto de brillo en las piezas
- ✅ Pantalla completa (fullscreen inmersivo)
