# Manejo de Excepciones Global

## Estructura de archivos

```
exception/
├── ProductNotFoundException.java   ← extiende RuntimeException
├── ErrorResponse.java              ← DTO con status, error, message, timestamp
└── GlobalExceptionHandler.java     ← @RestControllerAdvice
```

## Comportamiento por endpoint

| Situación | Excepción | HTTP |
|---|---|---|
| `GET /api/products/{id}` con id inexistente | `ProductNotFoundException` | `404` |
| `PUT /api/products/{id}` con id inexistente | `ProductNotFoundException` | `404` |
| `DELETE /api/products/{id}` con id inexistente | `ProductNotFoundException` | `404` |
| Cualquier error inesperado | `Exception` | `500` |

## Ejemplo de respuesta de error

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 99",
  "timestamp": "2026-05-03T10:25:00"
}
```
