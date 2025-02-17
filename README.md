# MiBlog API

Este proyecto consiste en el backend para un blog personal sin una temática en particular, desarrollado para ser la base de una página web de solo lectura. El sistema está diseñado para que los posts del blog sean gestionados de manera eficiente, permitiendo la creación, edición y consulta de temas, con funcionalidades básicas de búsqueda y filtrado.

El blog es de **solo lectura** por el momento, pero se tiene previsto expandir sus funcionalidades en el futuro, incluyendo comentarios de los lectores y más opciones interactivas.

**Estado actual:**  
La versión actual se encuentra en desarrollo y se han implementado funcionalidades básicas de gestión de posts. Se planean futuras mejoras como la seguridad de los endpoints, validación de usuarios, documentación Swagger y un frontend interactivo.

---

## Tecnologías utilizadas

- **Java**: Lenguaje de programación principal.
- **Spring Boot**: Framework utilizado para desarrollar la API RESTful.
- **MongoDB**: Base de datos NoSQL utilizada para almacenar los posts.
- **MongoDB Atlas**: Servicio de MongoDB en la nube.
- **PostMan**: Herramienta utilizada para realizar las pruebas funcionales de la API.
- **Eclipse IDE**: Entorno de desarrollo utilizado.

---

## Descripción del Proyecto

Este backend de blog permite a un escritor cargar sus temas en formato de documento `.TXT` o `.PDF`, subiéndolos a un almacenamiento en la nube (como Google Drive, AWS S3, etc.), y luego simplemente introduciendo la URL del archivo en la plataforma. Al mostrar el post en el frontend, la aplicación recupera el contenido del archivo y lo muestra al lector sin que este note que lo que está leyendo proviene de un archivo de texto o PDF.

**Características actuales:**
- Creación, lectura, actualización y eliminación (CRUD) de posts.
- Búsqueda de posts por palabras clave, etiquetas y fecha.
- Visibilidad del post (se puede activar o desactivar la visibilidad de cada tema).
- Todos los posts están almacenados en MongoDB y pueden ser consultados mediante la API.

**Funcionalidades pendientes:**
- **Seguridad**: Implementación de seguridad para proteger los endpoints.
- **Validación de usuarios**: Implementación de autenticación y autorización para los usuarios.
- **Swagger**: Documentación de la API utilizando Swagger.
- **Frontend**: Desarrollo de la interfaz de usuario para el blog.

---

## Instrucciones para el Desarrollo Local

### Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado lo siguiente:

- **JDK 11 o superior**
- **Maven**
- **MongoDB Atlas** (acceso a un cluster de MongoDB en la nube)

### Configuración del Proyecto

1. **Clona este repositorio**:

   ```bash
   git clone https://github.com/MasBytesDev/miblog-api.git
   ```

2. **Configura tu archivo `application.properties`** con tu propia conexión a MongoDB Atlas.

   ```properties
   spring.data.mongodb.uri=mongodb+srv://<usuario>:<contraseña>@clusterblog.b2egu.mongodb.net/miblog_db?retryWrites=true&w=majority
   ```

3. **Ejecuta el proyecto**:

   Desde Eclipse o tu IDE favorito, puedes ejecutar la clase principal `MiBlogApiApplication.java` para iniciar el servidor.

4. **Pruebas con Postman**:
   Se pueden realizar las pruebas funcionales de la API mediante Postman. Asegúrate de tener configurados los endpoints correctos de la API según los métodos CRUD implementados.

---

## Funcionalidades de la API

La API actualmente ofrece los siguientes endpoints:

- **POST /api/posts**: Crear un nuevo post.
- **GET /api/posts/{id}**: Obtener un post por su ID.
- **GET /api/posts/search**: Buscar posts por palabra clave.
- **GET /api/posts/tags**: Buscar posts por etiquetas.
- **GET /api/posts/recent**: Obtener posts recientes dentro de un rango de fechas.
- **PUT /api/posts/{id}**: Actualizar un post existente.
- **PATCH /api/posts/{id}/visibility**: Actualizar la visibilidad de un post.

---

## Contribución

Si deseas contribuir a este proyecto, por favor sigue estos pasos:

1. **Fork** el repositorio.
2. Crea una nueva rama para tus cambios.
3. Realiza tus cambios y haz commit.
4. Envía un **Pull Request**.

---

## Notas importantes

Este proyecto se encuentra en desarrollo y no es la versión final. Las funcionalidades futuras incluyen:

- Seguridad para los endpoints.
- Validación de usuarios y roles.
- Documentación completa con Swagger.
- Desarrollo de la interfaz frontend.

---

## Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, consulta el archivo [LICENSE](LICENSE).

---
