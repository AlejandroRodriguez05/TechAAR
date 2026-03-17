# FCTSeek — Aplicación de Escritorio (JavaFX)

## Descripción del Proyecto

**FCTSeek Desktop** es el cliente de escritorio de la plataforma FCTSeek, construido con **JavaFX 21** y diseñado como herramienta principal para profesores y personal del centro CIFP Villa de Agüimes. Permite gestionar de forma completa las empresas colaboradoras de FCT, incluyendo creación/edición de empresas, gestión de plazas y reservas, listas personalizadas, comentarios, valoraciones y favoritos.

La aplicación se comunica con el backend REST (Spring Boot) mediante peticiones HTTP con autenticación JWT, utilizando el `HttpClient` nativo de Java 21.

---

## Stack Tecnológico

| Categoría | Tecnología | Versión |
|-----------|------------|---------|
| Lenguaje | Java | 21 |
| Framework UI | JavaFX | 21.0.2 |
| Vistas | FXML (Scene Builder) | — |
| Serialización JSON | Gson | 2.11.0 |
| HTTP | java.net.http.HttpClient | JDK 21 |
| Build | Maven | — |
| Modularización | Java Platform Module System (JPMS) | — |

---

## Arquitectura y Patrones de Diseño

La aplicación sigue el patrón **MVC (Model-View-Controller)** adaptado a JavaFX:

```
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│   View (FXML) │◄────►│  Controller   │─────►│    Service    │
│  Declarativa  │      │ (JavaFX FXML) │      │  (HTTP REST)  │
└───────────────┘      └───────┬───────┘      └───────┬───────┘
                               │                      │
                       ┌───────▼───────┐      ┌───────▼───────┐
                       │     Model     │      │   ApiClient   │
                       │  (POJOs/Gson) │      │  (HttpClient) │
                       └───────────────┘      └───────────────┘
```

### Patrones Estructurales Aplicados

- **MVC (Model-View-Controller)**: Las vistas se definen en archivos `.fxml` (editables con Scene Builder), cada una enlazada a un controller Java mediante `fx:controller`. Los modelos son POJOs planos deserializados desde JSON con Gson.

- **Singleton — Session**: La clase `Session` implementa un Singleton clásico (lazy initialization) que mantiene el estado de la sesión activa (token JWT y datos del usuario autenticado). Esto permite que cualquier componente de la aplicación acceda a la información de autenticación sin propagarla manualmente.

- **Facade — ApiClient**: La clase `ApiClient` actúa como fachada centralizada para toda la comunicación HTTP con el backend. Encapsula la construcción de peticiones, la inyección automática del token JWT, la serialización/deserialización con Gson y el manejo de errores HTTP. Ofrece métodos genéricos (`get`, `getList`, `post`, `put`, `delete`) y una variante `postPublic` para endpoints sin autenticación.

- **Navigator / View Manager**: `ViewManager` centraliza la navegación entre pantallas, cargando las vistas FXML dinámicamente y reemplazando la raíz de la escena. Soporta paso de parámetros entre vistas mediante un mapa estático clave-valor (`navigateTo("vista", "key", value)` → `getParam("key")`).

- **Factory — CardFactory**: La clase `CardFactory` construye programáticamente tarjetas de empresa (cards) como nodos JavaFX, encapsulando la creación de componentes visuales complejos. Esto evita duplicación de código en las vistas que muestran listados de empresas.

- **Java Module System (JPMS)**: El proyecto está modularizado con `module-info.java`, declarando explícitamente sus dependencias (`javafx.controls`, `javafx.fxml`, `com.google.gson`, `java.net.http`) y abriendo los paquetes necesarios para reflexión de JavaFX y Gson.

- **Comunicación asíncrona**: `ApiClient` ofrece un método `asyncGet` basado en `CompletableFuture` que ejecuta peticiones HTTP en segundo plano y devuelve el resultado al hilo de JavaFX (`Platform.runLater`), evitando bloqueos de la interfaz durante las llamadas de red.

---

## Estructura del Proyecto

```
FCTSeek-desktop/
├── pom.xml                                     # Configuración Maven (JavaFX 21 + Gson)
├── src/main/
│   ├── java/
│   │   ├── module-info.java                    # Declaración de módulo JPMS
│   │   └── com/practicas/
│   │       ├── App.java                        # Punto de entrada (Application.start)
│   │       ├── controller/                     # Controladores FXML
│   │       │   ├── LoginController.java        # Pantalla de autenticación
│   │       │   ├── EmpresasController.java     # Listado principal de empresas
│   │       │   ├── EmpresaDetalleController.java # Vista detalle de empresa completa
│   │       │   ├── AnadirEmpresaController.java  # Formulario de creación/edición
│   │       │   ├── BusquedaController.java     # Pantalla de búsqueda
│   │       │   ├── ListasController.java       # Gestión de listas personalizadas
│   │       │   ├── ListaDetalleController.java # Contenido de una lista
│   │       │   ├── AnadirAListaDialogController.java # Diálogo modal para añadir a lista
│   │       │   └── PerfilController.java       # Perfil del usuario
│   │       ├── model/                          # POJOs (deserializados desde JSON)
│   │       │   ├── Empresa.java                # Modelo de empresa
│   │       │   ├── Usuario.java                # Modelo de usuario
│   │       │   ├── Plaza.java                  # Modelo de plaza FCT
│   │       │   ├── Reserva.java                # Modelo de reserva
│   │       │   ├── Comentario.java             # Modelo de comentario
│   │       │   ├── Curso.java                  # Modelo de ciclo formativo
│   │       │   ├── Departamento.java           # Modelo de departamento
│   │       │   ├── Lista.java                  # Modelo de lista personalizada
│   │       │   └── EmpresaContactada.java      # Modelo de contacto
│   │       ├── service/                        # Servicios (llamadas a la API REST)
│   │       │   ├── AuthService.java            # Login, parsing de respuesta JWT
│   │       │   ├── EmpresaService.java         # CRUD y búsquedas de empresas
│   │       │   ├── PlazaService.java           # Operaciones de plazas
│   │       │   ├── ReservaService.java         # Operaciones de reservas
│   │       │   ├── ListaService.java           # Gestión de listas
│   │       │   ├── FavoritoService.java        # Toggle de favoritos
│   │       │   ├── ComentarioService.java      # Comentarios
│   │       │   ├── ValoracionService.java      # Valoraciones
│   │       │   ├── DepartamentoService.java    # Consulta de departamentos
│   │       │   └── EmpresaContactadaService.java # Registro de contactos
│   │       └── util/
│   │           ├── ApiClient.java              # Cliente HTTP centralizado (Facade)
│   │           ├── CardFactory.java            # Fábrica de tarjetas de empresa (Factory)
│   │           ├── Session.java                # Sesión del usuario (Singleton)
│   │           └── ViewManager.java            # Navegación entre vistas
│   └── resources/
│       └── fxml/                               # Vistas declarativas
│           ├── login.fxml                      # Pantalla de login
│           ├── empresas.fxml                   # Listado de empresas
│           ├── empresa_detalle.fxml            # Detalle de empresa
│           ├── anadir_empresa.fxml             # Formulario crear/editar empresa
│           ├── busqueda.fxml                   # Pantalla de búsqueda
│           ├── listas.fxml                     # Gestión de listas
│           ├── lista_detalle.fxml              # Detalle de lista
│           ├── anadir_a_lista_dialog.fxml      # Modal de selección de lista
│           └── perfil.fxml                     # Perfil del usuario
```

---

## Flujo de Navegación

```
Login ──► Empresas (listado principal)
              │
              ├──► Detalle de Empresa ──► Editar Empresa
              │         │
              │         ├── Comentarios, Valoraciones, Favoritos
              │         ├── Plazas y Reservas
              │         └── Añadir a Lista (modal)
              │
              ├──► Búsqueda de Empresas
              ├──► Mis Listas ──► Detalle de Lista
              ├──► Añadir Empresa (formulario)
              └──► Perfil
```

La navegación se gestiona centralmente a través de `ViewManager.navigateTo("nombre_vista")`. Los parámetros entre pantallas (como el ID de empresa para el detalle) se pasan mediante `ViewManager.navigateTo("vista", "key", value)` y se recuperan con `ViewManager.getParam("key")`.

---

## Comunicación con el Backend

Toda la comunicación HTTP pasa por `ApiClient`, que:

1. **Construye las peticiones** con `java.net.http.HttpClient` (timeout de 10 segundos).
2. **Inyecta el token JWT** automáticamente desde `Session.get().getToken()` en la cabecera `Authorization: Bearer`.
3. **Serializa/deserializa** con Gson (tanto objetos individuales como listas genéricas mediante `TypeToken`).
4. **Gestiona errores HTTP**: Lanza `ApiException` con el código de estado y el mensaje del servidor para que los controllers puedan mostrar feedback al usuario.
5. **Soporta llamadas asíncronas**: El método `asyncGet` ejecuta en `CompletableFuture` y devuelve al hilo de FX con `Platform.runLater`.

Ejemplo de uso típico en un servicio:

```java
// Obtener lista de empresas
List<Empresa> empresas = ApiClient.getList("/empresas", Empresa.class);

// Crear empresa (requiere autenticación)
Empresa nueva = ApiClient.post("/empresas", empresaData, Empresa.class);

// Login (sin autenticación)
String json = ApiClient.postPublic("/auth/login", credenciales);
```

---

## Configuración y Puesta en Marcha

### Prerrequisitos

- **Java 21** (JDK con JavaFX incluido, o JDK + JavaFX SDK)
- **Maven 3.8+**
- Backend FCTSeek corriendo en `http://localhost:8080`

### Ejecutar

```bash
cd FCTSeek-desktop
mvn javafx:run
```

La aplicación arrancará con una ventana de 1200×800 píxeles mostrando la pantalla de login.

### Configurar URL del backend

Si el backend no está en `localhost:8080`, editar la constante `BASE_URL` en `ApiClient.java`:

```java
private static final String BASE_URL = "http://IP_DEL_BACKEND:8080/api";
```

---

## Autores

- **AlejandroR** — Desarrollo principal
- **Raul** — Desarrollo principal
---

## Licencia

Proyecto académico — CIFP Villa de Agüimes.