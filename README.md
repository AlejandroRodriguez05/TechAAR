# FCT-Seek

> Plataforma de gestión de prácticas FCT para centros educativos de Formación Profesional.

---

## ¿Qué es FCT-Seek?

FCT-Seek es una aplicación multiplataforma diseñada para facilitar la búsqueda, gestión y seguimiento de empresas colaboradoras en las prácticas de Formación en Centros de Trabajo (FCT). Está pensada para los departamentos y profesores de centros de FP que necesitan organizar y compartir información sobre las empresas donde sus alumnos realizan las prácticas.

La plataforma permite a los profesores explorar un catálogo de empresas, consultar las plazas disponibles, hacer reservas para sus alumnos, valorar empresas, dejar comentarios y organizar sus empresas favoritas en listas personalizadas.

---

## Plataformas

FCT-Seek está disponible en dos plataformas:

- **Aplicación móvil** — desarrollada con React Native, disponible para Android e iOS.
- **Aplicación de escritorio** — desarrollada con JavaFX, disponible para Windows, macOS y Linux.

Ambas aplicaciones consumen la misma API REST y comparten toda la funcionalidad.

---

## Tecnologías utilizadas

### Aplicación móvil
![React Native](https://img.shields.io/badge/React_Native-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![Expo](https://img.shields.io/badge/Expo-000020?style=for-the-badge&logo=expo&logoColor=white)

### Aplicación de escritorio
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

### Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

---

## Funcionalidades principales

### Exploración de empresas
- Listado completo de empresas colaboradoras con filtros rápidos (nuevas, top valoradas, cercanas).
- Buscador por nombre con filtro adicional por departamento.
- Tarjeta de empresa con nombre, valoración media, ciudad, departamentos que acepta y estado de contacto.

### Detalle de empresa
- Información completa: dirección, teléfono, email, persona de contacto y descripción.
- Departamentos y ciclos formativos que acepta la empresa.
- Plazas disponibles con número de plazas ofertadas, reservadas y libres.
- Historial de reservas realizadas por otros profesores.
- Sección de contactados: qué departamentos han contactado ya con la empresa.
- Comentarios generales y privados (solo para profesores).
- Valoración con sistema de estrellas del 1 al 5.
- Añadir a favoritos y a listas personalizadas.

### Gestión de plazas y reservas
- Los profesores pueden reservar plazas para sus alumnos especificando el ciclo formativo, la cantidad y el grupo/clase.
- Cada profesor puede eliminar sus propias reservas.
- Los administradores pueden añadir y eliminar plazas de empresas.

### Listas personalizadas
- Cada profesor tiene una lista de Favoritos automática.
- Se pueden crear listas personalizadas para organizar empresas por cualquier criterio.
- Las empresas se pueden añadir o quitar de múltiples listas a la vez desde el detalle de empresa.

### Comentarios
- Comentarios generales visibles para todos los usuarios.
- Comentarios privados visibles solo para profesores del centro.
- Cada usuario puede eliminar sus propios comentarios.

### Perfil
- Visualización de datos del usuario: nombre, email, NIF y centro educativo.
- Cierre de sesión.

---

## Instalación

### Aplicación móvil

```bash
# Clona el repositorio
git clone https://github.com/tu-usuario/fct-seek-mobile

# Instala dependencias
npm install

# Ejecuta en Android
npx react-native run-android

# Ejecuta en iOS
npx react-native run-ios
```

### Aplicación de escritorio

Requisitos previos: Java 21 y Maven.

```bash
# Clona el repositorio
git clone https://github.com/tu-usuario/fct-seek-desktop

# Ejecuta la aplicación
mvn javafx:run
```

---
