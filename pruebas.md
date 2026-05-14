# Plan de Pruebas — productService-AI

## Stack de pruebas

| Librería | Para qué | ¿Incluida? |
|---|---|---|
| JUnit 5 (Jupiter) | Motor de pruebas | ✅ vía `spring-boot-starter-webmvc-test` |
| Mockito | Crear mocks y verificar comportamiento | ✅ vía `spring-boot-starter-webmvc-test` |
| AssertJ | Aserciones fluidas y legibles | ✅ vía `spring-boot-starter-webmvc-test` |
| MockMvc | Simular requests HTTP en pruebas de controller | ✅ vía `spring-boot-starter-webmvc-test` |

---

## Capa Service — `ProductServiceImplTest`

| # | Método | Escenario | Qué verifica |
|---|---|---|---|
| 1 | `findById` | El producto existe | Retorna el DTO con todos los campos correctos |
| 2 | `findById` | El producto no existe | Lanza `ProductNotFoundException` |
| 3 | `findAll` | Hay productos en BD | Retorna lista de DTOs mapeados correctamente |
| 4 | `findAll` | No hay productos | Retorna lista vacía sin errores |
| 5 | `save` | DTO válido | Persiste y retorna DTO con id generado |
| 6 | `update` | El producto existe | Actualiza solo los campos enviados |
| 7 | `update` | El producto no existe | Lanza `ProductNotFoundException` |
| 8 | `deleteById` | El producto existe | Elimina sin lanzar excepción |
| 9 | `deleteById` | El producto no existe | Lanza `ProductNotFoundException` |

---

## Capa Controller — `ProductControllerTest`

| # | Endpoint | Escenario | Qué verifica |
|---|---|---|---|
| 10 | `GET /api/products` | Hay productos | HTTP 200 + lista JSON |
| 11 | `GET /api/products/{id}` | El producto existe | HTTP 200 + body correcto |
| 12 | `GET /api/products/{id}` | No existe | HTTP 404 + `ErrorResponse` |
| 13 | `POST /api/products` | DTO válido | HTTP 201 + producto creado |
| 14 | `PUT /api/products/{id}` | El producto existe | HTTP 200 + producto actualizado |
| 15 | `DELETE /api/products/{id}` | El producto existe | HTTP 204 sin body |
| 16 | `GET /api/products/metrics` | App corriendo | HTTP 200 + contadores |

---

## Capa Repository — `ProductRepositoryTest`

| # | Método | Escenario | Qué verifica |
|---|---|---|---|
| 17 | `save` + `findById` | Flujo completo | Persistencia real con H2 |
| 18 | `existsById` | El producto no existe | Retorna `false` |
| 19 | `deleteById` + `findById` | Después de eliminar | El producto ya no existe |

---

## Manejo de excepciones — `GlobalExceptionHandlerTest`

| # | Escenario | Qué verifica |
|---|---|---|
| 20 | Request con id inexistente | Formato exacto del `ErrorResponse` (status, error, message, timestamp) |
| 21 | Error inesperado del servidor | HTTP 500 con mensaje genérico |

---

## Casos borde

| # | Escenario | Qué verifica |
|---|---|---|
| 22 | `save` con campos `null` | Comportamiento con datos incompletos |
| 23 | `findAll` con 1000 productos | El mapeo con stream no falla en volumen |
| 24 | `deleteById` con id negativo | No explota con id inválido |
| 25 | `ProductMetricsCollector.reset()` | Los contadores vuelven a cero |

---

## Orden de implementación recomendado

```
1. ProductServiceImplTest      ← lógica de negocio, sin Spring, rápido
2. ProductControllerTest       ← endpoints HTTP con MockMvc
3. GlobalExceptionHandlerTest  ← formato de errores
4. ProductRepositoryTest       ← persistencia con H2 real
5. Casos borde                 ← escenarios extremos
```

---

## Cómo ejecutar las pruebas

```bash
# Todas las pruebas
./mvnw test

# Una clase específica
./mvnw test -Dtest=ProductServiceImplTest

# Un método específico
./mvnw test -Dtest=ProductServiceImplTest#findById_WhenProductExists_ReturnsProductDTO
```

---

## Estado de implementación

| Clase de prueba | Estado |
|---|---|
| `ProductServiceImplTest` | ✅ Pruebas 1 y 2 implementadas |
| `ProductControllerTest` | ⬜ Pendiente |
| `GlobalExceptionHandlerTest` | ⬜ Pendiente |
| `ProductRepositoryTest` | ⬜ Pendiente |
| Casos borde | ⬜ Pendiente |
