# Sistema de Gestión de Prácticas - JavaFX

## Descripción

Aplicación JavaFX para la gestión de prácticas de alumnos en empresas. 
Cuenta con dos roles de usuario con diferentes permisos.

## Características

### 🎓 Rol Alumno
- Ver lista de empresas de prácticas
- Filtrar empresas por nombre, sector o ubicación
- Ver descripción y reseñas de empresas **con plazas disponibles**
- Las empresas **OCUPADAS** muestran información restringida
- Modo solo lectura (no puede modificar datos)

### 👨‍🏫 Rol Profesor
- Ver **todas** las empresas (ocupadas y libres)
- Ver descripción y reseñas completas de todas las empresas
- **Añadir nuevas empresas** al sistema
- **Eliminar empresas** existentes
- **Gestionar plazas**: cambiar de 0/4 a 4/4 (ocupada/libre)

## Usuarios de prueba

| Tipo | Usuario | Contraseña |
|------|---------|------------|
| Alumno | alumno1 | 1234 |
| Alumno | alumno2 | 1234 |
| Profesor | profesor1 | admin |
| Profesor | profesor2 | admin |

## Requisitos

- Java 21 o superior
- Maven 3.6+
- JavaFX 21

## Instalación y Ejecución

### Opción 1: Con Maven (recomendado)

```bash
# Clonar o descargar el proyecto
cd practicas-app

# Compilar
mvn clean compile

# Ejecutar
mvn javafx:run
```

### Opción 2: Desde VS Code

1. Instalar "Extension Pack for Java"
2. Abrir la carpeta `practicas-app`
3. Maven descargará las dependencias automáticamente
4. Ejecutar `Main.java`

## Estructura del Proyecto

```
practicas-app/
├── pom.xml                          # Configuración Maven
├── src/main/java/
│   ├── module-info.java             # Módulo Java
│   └── com/practicas/
│       ├── Main.java                # Clase principal
│       ├── model/
│       │   ├── TipoUsuario.java     # Enum de roles
│       │   ├── Usuario.java         # Modelo de usuario
│       │   ├── Empresa.java         # Modelo de empresa
│       │   └── DataService.java     # Servicio de datos
│       └── view/
│           ├── LoginView.java       # Vista de login
│           └── EmpresasView.java    # Vista principal
└── README.md
```

## Flujo de la Aplicación

```
┌─────────────────────────────────────────────────────────┐
│                    PANTALLA LOGIN                        │
│  ┌─────────────┐          ┌─────────────┐               │
│  │   ALUMNO    │          │  PROFESOR   │               │
│  └─────────────┘          └─────────────┘               │
│         │                        │                       │
│         ▼                        ▼                       │
│  ┌─────────────────────────────────┐                    │
│  │    Formulario de Login          │                    │
│  │    Usuario: ________            │                    │
│  │    Contraseña: ______           │                    │
│  └─────────────────────────────────┘                    │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                PANTALLA PRINCIPAL                        │
│  ┌────────────────────┐  ┌────────────────────────────┐ │
│  │ 🔍 Buscar          │  │ 📄 Detalles de Empresa     │ │
│  │ [_____________]    │  │                            │ │
│  │                    │  │ Nombre: TechSolutions      │ │
│  │ Lista de Empresas: │  │ Estado: 🟢 LIBRE 2/4      │ │
│  │ ┌────────────────┐ │  │ Sector: Tecnología         │ │
│  │ │🟢 TechSol 2/4  │ │  │ Ubicación: Madrid          │ │
│  │ │🔴 DataCorp 4/4 │ │  │                            │ │
│  │ │🟢 CloudInc 1/4 │ │  │ Descripción: ...           │ │
│  │ └────────────────┘ │  │ Reseña: ...                │ │
│  │                    │  │                            │ │
│  │ [+ Añadir] (prof)  │  │ [Actualizar] [Eliminar]   │ │
│  └────────────────────┘  └────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Estados de Empresa

- **🟢 LIBRE**: Tiene plazas disponibles (ej: 2/4)
- **🔴 OCUPADA**: No tiene plazas (ej: 4/4)

## Reglas de Negocio

1. Un alumno **NO puede** ver la información completa de empresas ocupadas
2. Un alumno **NO puede** iniciar sesión como profesor
3. Solo los profesores pueden añadir, eliminar y modificar empresas
4. Las plazas van de 0 a N (configurable por empresa, por defecto 4)

## Tecnologías Utilizadas

- Java 21
- JavaFX 21
- Maven
- Patrón Singleton (DataService)
- MVC (Model-View-Controller simplificado)

## Posibles Mejoras

- [ ] Persistencia en base de datos (SQLite/MySQL)
- [ ] Sistema de registro de nuevos usuarios
- [ ] Historial de cambios
- [ ] Exportar lista a PDF/Excel
- [ ] Añadir fotos de empresas
- [ ] Sistema de valoraciones con estrellas

## Autor

Proyecto educativo para prácticas de JavaFX

## Licencia

MIT License
