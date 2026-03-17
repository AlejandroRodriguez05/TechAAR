# FCTSeek — Aplicación Móvil (React Native / Expo)

## Descripción del Proyecto

**FCTSeek Mobile** es el cliente móvil de la plataforma FCTSeek, construido con **React Native** y **Expo SDK 54**. Está orientado tanto a profesores como a alumnos del CIFP Villa de Agüimes, proporcionando acceso desde el móvil a las funcionalidades principales: consultar empresas de FCT, ver detalles y valoraciones, gestionar listas personalizadas, crear empresas y administrar plazas.

La aplicación se comunica con el backend REST (Spring Boot) mediante un cliente HTTP propio con autenticación JWT y almacenamiento seguro del token mediante `expo-secure-store`.

---

## Stack Tecnológico

| Categoría | Tecnología | Versión |
|-----------|------------|---------|
| Framework | React Native | 0.81.5 |
| Plataforma | Expo | SDK 54 |
| Lenguaje | TypeScript | 5.9.2 |
| Navegación | React Navigation 7 (Native Stack + Bottom Tabs) | 7.x |
| Estado global | React Context API | — |
| Almacenamiento seguro | expo-secure-store | 14.0.1 |
| Iconos | @expo/vector-icons (Ionicons) | 15.0.3 |
| UI | expo-linear-gradient, react-native-gesture-handler | — |
| Tipado | TypeScript (strict) | — |

---

## Arquitectura y Patrones de Diseño

La aplicación sigue una arquitectura basada en **componentes funcionales con hooks**, organizada por responsabilidad:

```
┌─────────────────────────────────────────────────────┐
│                   App.tsx                            │
│         AuthProvider → SafeAreaProvider              │
│                   ↓                                  │
│              AppNavigator                            │
│         (condicional: login vs tabs)                 │
├─────────────────────────────────────────────────────┤
│    Screens         │    Services      │   Context    │
│  (UI + lógica      │  (HTTP calls     │  (estado     │
│   de pantalla)     │   al backend)    │   global)    │
├─────────────────────────────────────────────────────┤
│    Components      │    Config        │   Types      │
│  (reutilizables)   │  (api client,    │  (interfaces │
│                    │   constantes)    │   TS)        │
└─────────────────────────────────────────────────────┘
```

### Patrones Estructurales Aplicados

- **Context + Provider (Estado Global)**: `AuthContext` encapsula todo el estado de autenticación (usuario, token, flags de carga) y expone funciones de login, registro y logout. Utiliza `useCallback` para memoizar las acciones y evitar re-renders innecesarios. El contexto se consume en cualquier parte de la app con el hook personalizado `useAuth()`.

- **Service Layer (Capa de Servicios)**: Cada entidad del dominio tiene su propio módulo de servicio (`empresaService.ts`, `plazaService.ts`, etc.) que encapsula las llamadas HTTP al backend. Los servicios no contienen estado; son funciones puras que reciben parámetros y devuelven promesas tipadas.

- **Centralized HTTP Client (Cliente HTTP Centralizado)**: El módulo `api.ts` actúa como capa de abstracción sobre `fetch`, proporcionando:
  - Inyección automática del token JWT desde `expo-secure-store`.
  - Gestión unificada de errores con la clase personalizada `ApiError`.
  - Shortcuts tipados: `api.get<T>()`, `api.post<T>()`, `api.put<T>()`, `api.delete<T>()`, `api.postPublic<T>()`.
  - Manejo de respuestas vacías (204 No Content).

- **Conditional Navigation (Navegación Condicional)**: `AppNavigator` renderiza condicionalmente el stack de autenticación (LoginScreen) o el stack principal (MainTabs + pantallas de detalle) según el estado de `isAuthenticated` del contexto. Esto implementa el patrón de "auth gate" habitual en apps móviles.

- **Typed Navigation**: Se definen `RootStackParamList` y `TabParamList` como tipos de TypeScript para parametrizar la navegación, garantizando que los parámetros de ruta (como `empresaId`) estén tipados en compilación.

- **Secure Token Storage**: El token JWT se almacena en `expo-secure-store` (cifrado en el Keychain de iOS / Keystore de Android), no en AsyncStorage, proporcionando seguridad real para las credenciales.

- **Resilient Auth Flow**: El `AuthContext` implementa un flujo robusto:
  1. Al iniciar, comprueba si hay un token almacenado.
  2. Si lo hay, intenta validarlo contra `/auth/me`.
  3. Si falla, limpia el token y redirige al login.
  4. Tras el login, si la respuesta no incluye los datos completos del usuario, hace una petición adicional a `/auth/me`.
  5. El `refreshUser` solo hace logout en errores 401, manteniendo los datos en memoria ante errores de red.

- **Auto-discovery del Backend**: En modo desarrollo, la app detecta automáticamente la IP del PC de desarrollo a través de `Constants.expoConfig.hostUri`, construyendo la URL del backend sin necesidad de configuración manual.

---

## Estructura del Proyecto

```
FCTSeek-frontend-movil/
├── App.tsx                           # Raíz: AuthProvider + SafeAreaProvider + AppNavigator
├── index.ts                          # Punto de entrada para Expo
├── app.json                          # Configuración de Expo (nombre, iconos, splash)
├── package.json                      # Dependencias (Expo SDK 54, RN 0.81.5, TS 5.9)
├── tsconfig.json                     # Configuración TypeScript
└── src/
    ├── components/                   # Componentes reutilizables
    │   ├── EmpresaCard.tsx           # Tarjeta de empresa para listados
    │   └── GradientBackground.tsx    # Fondo degradado reutilizable
    ├── config/
    │   ├── api.ts                    # Cliente HTTP centralizado + gestión de token
    │   └── constants.ts              # URL del backend (auto-detecta IP en desarrollo)
    ├── context/
    │   └── AuthContext.tsx            # Context de autenticación global (login, logout, estado)
    ├── navigation/
    │   └── AppNavigator.tsx           # Definición de rutas (Bottom Tabs + Native Stack)
    ├── screens/                       # Pantallas de la aplicación
    │   ├── LoginScreen.tsx            # Pantalla de autenticación
    │   ├── HomeScreen.tsx             # Inicio (resumen, accesos rápidos)
    │   ├── SearchScreen.tsx           # Búsqueda de empresas
    │   ├── EmpresaDetailScreen.tsx    # Detalle completo de empresa
    │   ├── AddEmpresaScreen.tsx       # Formulario crear/editar empresa
    │   ├── ListsScreen.tsx            # Gestión de listas personalizadas
    │   ├── ListDetailScreen.tsx       # Contenido de una lista
    │   └── ProfileScreen.tsx          # Perfil del usuario
    ├── services/                      # Capa de servicios (llamadas al backend)
    │   ├── empresaService.ts          # CRUD y búsquedas de empresas
    │   ├── plazaService.ts            # Operaciones de plazas FCT
    │   ├── reservaService.ts          # Gestión de reservas
    │   ├── comentarioService.ts       # Comentarios
    │   ├── valoracionService.ts       # Valoraciones
    │   ├── favoritoService.ts         # Toggle de favoritos
    │   ├── listaService.ts            # Listas personalizadas
    │   ├── departamentoService.ts     # Consulta de departamentos
    │   └── contactadaService.ts       # Registro de contactos
    ├── theme/
    │   └── colors.ts                  # Paleta de colores centralizada
    └── types/
        └── index.ts                   # Interfaces TypeScript de todo el dominio
```

---

## Flujo de Navegación

La app utiliza React Navigation 7 con dos niveles de navegación:

```
AppNavigator (Native Stack)
│
├── [No autenticado]
│   └── LoginScreen
│
└── [Autenticado]
    ├── MainTabs (Bottom Tab Navigator)
    │   ├── Inicio (HomeScreen)
    │   ├── Buscar (SearchScreen)
    │   ├── Listas (ListsScreen)
    │   └── Perfil (ProfileScreen)
    │
    └── Stack Screens (sobre las tabs)
        ├── EmpresaDetail ──► params: { empresaId: number }
        ├── AddEmpresa
        ├── EditEmpresa ──► params: { empresaId: number }
        └── ListDetail ──► params: { listaId: number }
```

Las pantallas de detalle se abren como screens del stack principal (sobre las tabs), con un header con degradado y botón de retroceso. La navegación condicional entre login y tabs principales se gestiona automáticamente según el estado de `isAuthenticated` del `AuthContext`.

---

## Tipado del Dominio

El archivo `src/types/index.ts` define todas las interfaces TypeScript del dominio, garantizando consistencia con el backend:

- `Usuario` — datos del usuario con rol (`PROFESOR` | `ALUMNO`)
- `Empresa` — modelo completo de empresa con departamentos, cursos y valoraciones
- `Departamento`, `Curso` — catálogos del centro
- `Plaza`, `Reserva` — gestión de plazas FCT
- `Comentario`, `Valoracion` — feedback sobre empresas
- `Lista` — listas personalizadas con empresas asociadas
- `ContactadaPor` — registro de contacto con detalles de plazas

---

## Diseño Visual

La aplicación utiliza un esquema de colores con degradado azul-cian como identidad visual:

- **Header degradado**: `#3730a3` (indigo) → `#0ea5e9` (sky) → `#67e8f9` (cyan)
- **Tab activa**: `#3730a3`
- **Paleta**: Definida centralmente en `src/theme/colors.ts`

---

## Configuración y Puesta en Marcha

### Prerrequisitos

- **Node.js 18+**
- **Expo CLI** (`npx expo` o `npm install -g expo-cli`)
- **Expo Go** instalado en el dispositivo móvil (para desarrollo)
- Backend FCTSeek corriendo en la misma red local

### 1. Instalar dependencias

```bash
cd FCTSeek-frontend-movil
npm install
```

### 2. Ejecutar en modo desarrollo

```bash
npx expo start
```

Escanear el QR con Expo Go. La app detectará automáticamente la IP del PC y construirá la URL del backend (`http://<tu-ip>:8080/api`).

### 3. Ejecutar en dispositivo específico

```bash
npx expo start --android    # Emulador o dispositivo Android
npx expo start --ios        # Simulador iOS (solo macOS)
```

### Configurar URL del backend (producción)

Editar `src/config/constants.ts`:

```typescript
const PRODUCTION_URL = 'https://tu-servidor.com/api';
```

---

## Autores

- **Agustin Delgado Estevez** — Desarrollo principal
- **Raul** — Desarrollo principal

---

## Licencia

Proyecto académico — CIFP Villa de Agüimes.