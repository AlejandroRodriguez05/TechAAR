# FCTSeek — Backend (API REST)

## Descripción del Proyecto

**FCTSeek** es una plataforma de gestión de prácticas FCT (Formación en Centros de Trabajo) desarrollada para el **CIFP Villa de Agüimes**. Este repositorio contiene el **backend**, una API REST que actúa como núcleo central del sistema, dando servicio tanto a la aplicación de escritorio (JavaFX) como a la aplicación móvil (React Native / Expo).

El backend permite a profesores y alumnos consultar, valorar y gestionar empresas colaboradoras, controlar plazas de FCT por curso académico, crear listas personalizadas de empresas y realizar reservas de plazas.

---

## Stack Tecnológico

| Categoría | Tecnología | Versión |
|-----------|------------|---------|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.0.2 |
| Persistencia | Spring Data JPA + Hibernate | — |
| Base de datos | PostgreSQL | — |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) | — |
| Validación | Jakarta Bean Validation | — |
| Build | Maven | — |
| Utilidades | Lombok | — |
| IDE de referencia | Apache NetBeans | — |

---

## Arquitectura y Patrones de Diseño

El proyecto sigue una **arquitectura en capas** (Layered Architecture) clásica de Spring Boot, con separación estricta de responsabilidades:

```
                    ┌──────────────────────────┐
  HTTP Request ───► │     Controller Layer     │  Recibe peticiones, valida entrada
                    ├──────────────────────────┤
                    │      Service Layer       │  Lógica de negocio, transacciones
                    ├──────────────────────────┤
                    │    Repository Layer      │  Acceso a datos (Spring Data JPA)
                    ├──────────────────────────┤
                    │     Model / Entity       │  Entidades JPA mapeadas a PostgreSQL
                    └──────────────────────────┘
```

### Patrones Estructurales Aplicados

- **DTO Pattern (Data Transfer Object)**: Los controladores nunca exponen directamente las entidades JPA. Se emplean objetos `*Request` para la entrada y `*Response` para la salida, desacoplando la API pública del modelo de persistencia. Los DTOs de respuesta incorporan métodos factoría estáticos `fromEntity()` para centralizar la conversión.

- **Repository Pattern**: Cada entidad tiene su interfaz `JpaRepository` con queries derivados y `@Query` personalizados (JPQL). Esto abstrae completamente la capa de acceso a datos.

- **Service Layer Pattern**: Toda la lógica de negocio (validaciones de CIF único, cálculo de plazas disponibles, gestión de favoritos) reside en los servicios, que son los únicos componentes transaccionales (`@Transactional`).

- **Filter Chain (Cadena de Filtros)**: La autenticación JWT se implementa como un `OncePerRequestFilter` (`JwtAuthenticationFilter`) que intercepta cada petición, extrae el token Bearer, lo valida y establece el contexto de seguridad de Spring. El filtro excluye automáticamente los endpoints públicos (`/api/auth/login`, `/api/auth/register`, `/api/health`).

- **Global Exception Handler**: Un `@RestControllerAdvice` (`GlobalExceptionHandler`) captura todas las excepciones del sistema y las convierte en respuestas JSON uniformes con la estructura `ApiResponse`, evitando que se filtren stack traces al cliente.

- **Builder Pattern**: El DTO `ApiResponse` utiliza Lombok `@Builder` para construir respuestas de forma fluida.

- **Soft Delete**: Las empresas y usuarios no se eliminan físicamente; se desactivan mediante un campo booleano (`activa` / `activo`), conservando la integridad referencial.

---

## Estructura del Proyecto

```
fctseek-backend/
├── pom.xml                                    # Configuración Maven y dependencias
├── src/main/
│   ├── java/com/fctseek/
│   │   ├── FctseekBackendApplication.java     # Punto de entrada Spring Boot
│   │   ├── config/
│   │   │   ├── CorsConfig.java                # Política CORS (permite clientes desktop y móvil)
│   │   │   └── SecurityConfig.java            # Cadena de seguridad, endpoints públicos/protegidos
│   │   ├── controller/                        # Controladores REST (capa de presentación)
│   │   │   ├── AuthController.java            # Login, registro, verificación de token
│   │   │   ├── EmpresaController.java         # CRUD de empresas, búsqueda, favoritas
│   │   │   ├── PlazaController.java           # Gestión de plazas FCT
│   │   │   ├── ReservaController.java         # Reservas de plazas por profesores
│   │   │   ├── ComentarioController.java      # Comentarios en empresas
│   │   │   ├── ValoracionController.java      # Puntuaciones de empresas
│   │   │   ├── FavoritoController.java        # Marcar/desmarcar empresas favoritas
│   │   │   ├── ListaController.java           # Listas personalizadas de empresas
│   │   │   ├── CursoController.java           # Consulta de ciclos formativos
│   │   │   ├── DepartamentoController.java    # Consulta de departamentos
│   │   │   ├── EmpresaContactadaController.java # Registro de contactos con empresas
│   │   │   ├── UsuarioController.java         # Gestión de usuarios
│   │   │   └── HealthController.java          # Endpoint de health check
│   │   ├── dto/
│   │   │   ├── request/                       # DTOs de entrada (validados con Jakarta)
│   │   │   │   ├── EmpresaRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── PlazaRequest.java
│   │   │   │   ├── ReservaRequest.java
│   │   │   │   └── ...
│   │   │   └── response/                      # DTOs de salida
│   │   │       ├── ApiResponse.java           # Respuesta genérica (success, message, timestamp)
│   │   │       ├── AuthResponse.java          # Token + datos de usuario
│   │   │       ├── EmpresaResponse.java       # Resumen de empresa (para listados)
│   │   │       ├── EmpresaDetailResponse.java # Detalle completo (con plazas, favorito, valoración)
│   │   │       ├── PlazaResponse.java
│   │   │       └── ...
│   │   ├── exception/                         # Excepciones personalizadas
│   │   │   ├── GlobalExceptionHandler.java    # @RestControllerAdvice centralizado
│   │   │   ├── ResourceNotFoundException.java # 404
│   │   │   ├── BadRequestException.java       # 400
│   │   │   └── UnauthorizedException.java     # 401
│   │   ├── model/                             # Entidades JPA
│   │   │   ├── Usuario.java                   # Usuarios (PROFESOR / ALUMNO)
│   │   │   ├── Empresa.java                   # Empresas colaboradoras
│   │   │   ├── Departamento.java              # Departamentos del centro
│   │   │   ├── Curso.java                     # Ciclos formativos (grado medio/superior)
│   │   │   ├── EmpresaCurso.java              # Relación N:M empresa ↔ curso
│   │   │   ├── Plaza.java                     # Plazas FCT ofertadas (generales o por curso)
│   │   │   ├── Reserva.java                   # Reservas de plazas (PENDIENTE/CONFIRMADA/CANCELADA)
│   │   │   ├── Comentario.java                # Comentarios (públicos o privados)
│   │   │   ├── Valoracion.java                # Puntuación numérica por usuario
│   │   │   ├── Favorito.java                  # Marcador de favorito
│   │   │   ├── Lista.java                     # Listas personalizadas
│   │   │   ├── ListaEmpresa.java              # Relación N:M lista ↔ empresa
│   │   │   └── EmpresaContactada.java         # Registro de contacto departamento → empresa
│   │   ├── repository/                        # Interfaces Spring Data JPA
│   │   │   ├── EmpresaRepository.java         # Queries JPQL personalizados
│   │   │   └── ...                            # Un repositorio por entidad
│   │   ├── security/                          # Infraestructura JWT
│   │   │   ├── JwtTokenProvider.java          # Generación, validación y extracción de claims
│   │   │   ├── JwtAuthenticationFilter.java   # Filtro HTTP que intercepta peticiones
│   │   │   └── CustomUserDetailsService.java  # Carga de usuarios desde BD para Spring Security
│   │   ├── service/                           # Capa de lógica de negocio
│   │   │   ├── AuthService.java               # Login, registro, hashing BCrypt
│   │   │   ├── EmpresaService.java            # CRUD, búsquedas, asignación de cursos
│   │   │   ├── PlazaService.java              # Gestión de plazas y disponibilidad
│   │   │   ├── ReservaService.java            # Flujo de reservas con control de estado
│   │   │   └── ...
│   │   └── util/
│   │       └── RolEnum.java                   # Enum de roles (PROFESOR, ALUMNO)
│   └── resources/
│       ├── application.properties             # Configuración principal (BD, JWT, logging)
│       └── application-dev.properties         # Perfil de desarrollo (SQL verbose, devtools)
└── Script_DB/
    └── fctseek_BD.sql                         # Script DDL completo para PostgreSQL
```

---

## Modelo de Datos

La base de datos consta de **12 tablas** con las siguientes relaciones principales:

```
departamentos ──1:N──► cursos ──N:M──► empresas (via empresa_cursos)
      │                                    │
      │                                    ├──1:N──► plazas ──1:N──► reservas
      │                                    ├──1:N──► comentarios
      │                                    ├──1:N──► valoraciones
      │                                    ├──1:N──► favoritos
      │                                    └──N:M──► listas (via lista_empresas)
      │
usuarios ──1:N──► comentarios, valoraciones, favoritos, listas, reservas
      └──────────► empresa_contactada (registro de contacto por departamento)
```

Aspectos destacados del esquema:

- **Plazas generales vs. específicas**: Una plaza puede ser general (para cualquier ciclo del departamento, `es_general = true`, `curso_id = NULL`) o específica para un ciclo concreto.
- **Constraint de unicidad compuesto** en `plazas`: `(empresa_id, departamento_id, curso_id, curso_academico)` evita duplicados de oferta.
- **Valoración única por usuario/empresa**: Restricción `UNIQUE(empresa_id, usuario_id)` en `valoraciones`.
- **Índices parciales**: Se usan índices condicionales (`WHERE activa = true`, `WHERE activo = true`) para acelerar las consultas más frecuentes.
- **Índices sobre funciones**: `LOWER(nombre)` y `LOWER(ciudad)` para búsquedas case-insensitive eficientes.

---

## Seguridad y Autenticación

El sistema utiliza **JWT (JSON Web Tokens)** con el flujo siguiente:

1. El cliente envía `POST /api/auth/login` con email y contraseña.
2. El `AuthService` verifica las credenciales contra BCrypt.
3. `JwtTokenProvider` genera un token firmado con HS256 (expiración: 24h).
4. El cliente almacena el token y lo envía en la cabecera `Authorization: Bearer <token>` en cada petición posterior.
5. `JwtAuthenticationFilter` valida el token, extrae el email y establece el `SecurityContext`.

**Autorización basada en roles**:
- `PROFESOR`: Puede crear, editar y eliminar empresas, gestionar plazas y reservas.
- `ALUMNO`: Acceso de lectura, puede comentar, valorar y crear listas.
- Los endpoints protegidos usan `@PreAuthorize("hasRole('PROFESOR')")`.

---

## API REST — Endpoints Principales

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/auth/login` | Iniciar sesión | No |
| `POST` | `/api/auth/register` | Registrar usuario | No |
| `GET` | `/api/auth/verify` | Verificar token | No |
| `GET` | `/api/health` | Health check | No |
| `GET` | `/api/empresas` | Listar empresas activas | Sí |
| `GET` | `/api/empresas/{id}` | Detalle completo de empresa | Sí |
| `GET` | `/api/empresas/buscar?q=` | Búsqueda por nombre o ciudad | Sí |
| `POST` | `/api/empresas` | Crear empresa | PROFESOR |
| `PUT` | `/api/empresas/{id}` | Actualizar empresa | PROFESOR |
| `DELETE` | `/api/empresas/{id}` | Desactivar empresa (soft delete) | PROFESOR |
| `GET` | `/api/empresas/favoritas` | Empresas favoritas del usuario | Sí |
| `POST` | `/api/plazas` | Crear plaza de FCT | PROFESOR |
| `POST` | `/api/reservas` | Reservar plazas | PROFESOR |
| `POST` | `/api/comentarios` | Añadir comentario | Sí |
| `POST` | `/api/valoraciones` | Valorar empresa | Sí |
| `POST/DELETE` | `/api/favoritos` | Toggle favorito | Sí |
| `GET/POST` | `/api/listas` | Gestión de listas personalizadas | Sí |

---

## Configuración y Puesta en Marcha

### Prerrequisitos

- **Java 21** (JDK)
- **PostgreSQL** instalado y corriendo
- **Maven 3.8+**

### 1. Crear la base de datos

```bash
# Crear la BD en PostgreSQL y ejecutar el script
psql -U postgres -c "CREATE DATABASE fctseek;"
psql -U postgres -d fctseek -f Script_DB/fctseek_BD.sql
```

O desde pgAdmin: crear la BD `fctseek` y ejecutar el script `fctseek_BD.sql` con el Query Tool.

### 2. Configurar la conexión

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fctseek
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA
```

### 3. Ejecutar

```bash
./mvnw spring-boot:run
```

El servidor arrancará en `http://localhost:8080`.

### 4. Verificar

```bash
curl http://localhost:8080/api/health
```

---

## Perfil de Desarrollo

Activando el perfil `dev` se habilita:
- Logging SQL detallado (sentencias + parámetros bindeados)
- Spring DevTools con live reload
- Debug de Spring Security

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Autores

- **AlejandroR** — Desarrollo principal
- **Agustin** — Desarrollo principal
- **Raul** — Desarrollo secundario

---

## Licencia

Proyecto académico — CIFP Villa de Agüimes.