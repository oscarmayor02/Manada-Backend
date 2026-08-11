# Manada — Backend (Spring Boot + PostgreSQL)

Monolito modular: una sola aplicación desplegable, organizada por dominios de negocio (`modules/`), cada uno con su propia entidad, repositorio, servicio y controlador.

## ⚠️ Léeme primero — qué está probado y qué no

- ✅ **La migración SQL (`V1__init_schema.sql`) está probada de verdad**: la corrí contra un PostgreSQL 16 real en este entorno, sin errores, y confirmé las 20 tablas creadas con sus llaves foráneas.
- ⚠️ **El código Java NO se pudo compilar aquí.** El entorno donde se escribió este proyecto bloquea el acceso a Maven Central (`repo.maven.apache.org`), así que Maven no puede descargar Spring Boot, el driver de Postgres, etc. Hice revisiones de sanidad manuales (paquetes correctos, llaves/paréntesis balanceados, imports) en los 116 archivos, pero **la primera vez que corras `mvn spring-boot:run` en un entorno con internet real, es probable que aparezca algún error de compilación menor para ajustar** (un import que falta, un tipo mal referenciado). Es normal en un proyecto de este tamaño escrito sin poder compilarlo — pero quería que lo supieras de antemano.

## Cómo correrlo

### Opción A — Docker (recomendada, todo incluido)
```bash
docker compose up --build
```
Levanta PostgreSQL y el backend juntos. La API queda en `http://localhost:8080`.

### Opción B — Manual
```bash
# 1. Levantar Postgres (si no usas Docker)
#    crea una base llamada "manada", usuario postgres / contraseña postgres
#    (o ajusta src/main/resources/application.yml con tus propias credenciales)

# 2. Compilar y correr
mvn spring-boot:run
```
Flyway crea las tablas automáticamente al arrancar (no hace falta correr el SQL a mano).

## Arquitectura — monolito modular

```
src/main/java/com/manada/backend/
├── ManadaApplication.java
├── config/              → configuración general (health check, etc.)
├── common/
│   ├── security/         → JWT (JwtService, JwtAuthFilter, SecurityConfig)
│   └── exception/        → manejo de errores centralizado
└── modules/
    ├── users/            → registro, login, JWT (auth)
    ├── foundations/       → perfil de fundación (verificación)
    ├── providers/         → perfil de proveedor: tienda/vet/paseador/etc. (verificación, comisión)
    ├── pets/              → mascotas (varias por dueño)
    ├── sos/               → alertas de mascota perdida + avistamientos
    ├── adoption/          → publicaciones y solicitudes de adopción
    ├── marketplace/       → productos + checkout con cálculo de comisión
    ├── bookings/          → servicios (vet, paseador...) + reservas
    ├── community/         → feed: posts, likes, comentarios
    ├── messaging/         → conversaciones y mensajes
    └── notifications/     → notificaciones in-app (base para push/correo real)
```

Cada módulo es autocontenido: su propia entidad JPA, repositorio, DTOs de entrada/salida, servicio (lógica de negocio) y controlador (HTTP). La comunicación entre módulos es directa (inyección de repositorios/servicios), sin una capa de eventos — suficiente para este tamaño de proyecto; si más adelante crece mucho, ahí sí conviene evaluar eventos de dominio o separar en microservicios.

## Endpoints principales

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Registro (dueño / fundación / proveedor) | No |
| POST | `/api/auth/login` | Login | No |
| GET | `/api/auth/me` | Perfil del usuario autenticado | Sí |
| GET/POST | `/api/pets` | Mascotas del usuario | Sí |
| GET | `/api/sos` | Alertas activas | No |
| POST | `/api/sos` | Publicar alerta SOS | Sí |
| POST | `/api/sos/{id}/sightings` | Reportar avistamiento | Sí |
| GET/POST | `/api/adoption` | Publicaciones de adopción | No/Sí |
| GET/POST | `/api/marketplace/products` | Catálogo / publicar producto | No/Sí |
| POST | `/api/marketplace/orders` | Comprar (con comisión automática) | Sí |
| GET/POST | `/api/services` | Servicios (vet, paseador...) | No/Sí |
| POST | `/api/services/bookings` | Reservar un servicio | Sí |
| GET/POST | `/api/community` | Feed de comunidad | No/Sí |
| GET/POST | `/api/messages/conversations` | Chat | Sí |
| GET | `/api/providers?type=VETERINARIA` | Directorio de proveedores verificados | No |
| PATCH | `/api/providers/{id}/verify` | Aprobar/rechazar proveedor (admin) | Sí* |

*Falta agregar un rol ADMIN real — ver sección "Lo que falta para producción".

## El modelo de comisiones

Cada `ProviderProfile` tiene su propio `commissionRate` (15% por defecto). Al comprar un producto o reservar un servicio, el backend calcula automáticamente cuánto se queda Manada y cuánto le corresponde al proveedor — ver `MarketplaceService.createOrder()` y `BookingService.createBooking()`. El punto exacto para conectar Wompi/Mercado Pago (split payments) está marcado con un comentario `PUNTO DE INTEGRACIÓN DE PAGOS` en ambos archivos.

## Lo que falta para producción real

1. **Compilar y corregir errores** en un entorno con acceso real a Maven Central (tu máquina o Claude Code).
2. **Rol ADMIN real** para proteger `/api/providers/{id}/verify` (hoy cualquier usuario autenticado podría llamarlo).
3. **Pagos reales**: conectar Wompi o Mercado Pago en los puntos ya marcados.
4. **Push/correo reales**: `NotificationService` hoy solo estima el alcance; falta conectar Firebase Cloud Messaging (push) y un proveedor de correo (Resend/SendGrid/SES).
5. **Secretos reales**: el JWT secret en `application.yml` es un ejemplo — en producción debe venir de variables de entorno o un gestor de secretos, nunca commiteado.
6. **Tests automatizados**: el `pom.xml` ya incluye H2 para tests (`application-test.yml`), pero no escribí los tests todavía.
7. **Paginación** en los listados (`/api/community`, `/api/marketplace/products`, etc.) — hoy `/api/community` trae hasta 50, el resto no tiene límite.
8. **HTTPS, rate limiting, monitoreo** — depende de dónde lo despliegues (Railway, Render, AWS, etc.).

## Siguiente paso

Recomiendo abrir esta carpeta en **Claude Code** (o tu IDE con Maven configurado) y correr `mvn spring-boot:run` por primera vez ahí — con acceso real a internet, cualquier ajuste de compilación se resuelve en minutos.
