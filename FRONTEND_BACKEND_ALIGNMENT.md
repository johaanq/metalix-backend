# Alineación Frontend-Backend

## Resumen de Cambios Realizados

Se han creado DTOs específicos para las operaciones de creación que se alinean perfectamente con lo que el frontend envía, evitando que se soliciten campos que se generan automáticamente.

## Campos Auto-Generados vs Requeridos

### 1. **Registro de Usuario (RegisterRequest)**
**Frontend envía:**
- `firstName` ✅
- `lastName` ✅
- `email` ✅
- `password` ✅
- `role` ✅ (siempre "CITIZEN")
- `municipalityId` ✅ (requerido para ciudadanos)

**Backend genera automáticamente:**
- `id` (BaseEntity)
- `createdAt` (AuditableModel)
- `updatedAt` (AuditableModel)
- `isActive` (por defecto true)
- `totalPoints` (por defecto 0)

### 2. **Creación de Municipalidad (CreateMunicipalityRequest)**
**Frontend envía:**
- `name` ✅
- `code` ✅
- `region` ✅
- `district` ✅
- `population` ✅
- `area` ✅
- `address` ✅
- `phone` ✅
- `email` ✅
- `website` ✅ (opcional)

**Backend genera automáticamente:**
- `id` (BaseEntity)
- `createdAt` (AuditableModel)
- `updatedAt` (AuditableModel)
- `isActive` (por defecto true)

### 3. **Creación de Punto de Recolección (CreateWasteCollectorRequest)**
**Frontend envía:**
- `name` ✅
- `type` ✅ (CollectorType enum)
- `location` ✅
- `municipalityId` ✅
- `zoneId` ✅ (opcional)
- `capacity` ✅
- `sensorId` ✅ (opcional)

**Backend genera automáticamente:**
- `id` (BaseEntity)
- `createdAt` (AuditableModel)
- `updatedAt` (AuditableModel)
- `currentFill` (por defecto 0.0)
- `status` (por defecto ACTIVE)
- `lastCollection` (null inicialmente)
- `nextScheduledCollection` (null inicialmente)

### 4. **Creación de Recompensa (CreateRewardRequest)**
**Frontend envía:**
- `name` ✅
- `description` ✅ (opcional)
- `pointsCost` ✅
- `category` ✅ (opcional)
- `availability` ✅
- `municipalityId` ✅
- `imageUrl` ✅ (opcional)
- `expirationDate` ✅ (opcional)
- `termsAndConditions` ✅ (opcional)

**Backend genera automáticamente:**
- `id` (BaseEntity)
- `createdAt` (AuditableModel)
- `updatedAt` (AuditableModel)
- `isActive` (por defecto true)

## DTOs Creados

### 1. CreateMunicipalityRequest
```java
@Data
public class CreateMunicipalityRequest {
    @NotBlank private String name;
    @NotBlank private String code;
    @NotBlank private String region;
    @NotBlank private String district;
    @NotNull @Positive private Integer population;
    @NotNull @Positive private Double area;
    @NotBlank private String address;
    @NotBlank private String phone;
    @NotBlank @Email private String email;
    private String website; // opcional
}
```

### 2. CreateWasteCollectorRequest
```java
@Data
public class CreateWasteCollectorRequest {
    @NotBlank private String name;
    @NotNull private CollectorType type;
    @NotBlank private String location;
    @NotNull private Long municipalityId;
    private Long zoneId; // opcional
    @NotNull @Positive private Double capacity;
    private String sensorId; // opcional
}
```

### 3. CreateRewardRequest
```java
@Data
public class CreateRewardRequest {
    @NotBlank private String name;
    private String description; // opcional
    @NotNull @Positive private Integer pointsCost;
    private RewardCategory category; // opcional
    @NotNull @Positive private Integer availability;
    @NotNull private Long municipalityId;
    private String imageUrl; // opcional
    private LocalDate expirationDate; // opcional
    private String termsAndConditions; // opcional
}
```

## Controladores Actualizados

Todos los controladores ahora usan los DTOs específicos en lugar de las entidades directamente:

- `MunicipalityController.createMunicipality()` → `CreateMunicipalityRequest`
- `WasteCollectorController.createCollector()` → `CreateWasteCollectorRequest`
- `RewardController.createReward()` → `CreateRewardRequest`

## Servicios Actualizados

Los servicios ahora mapean los DTOs a las entidades, estableciendo los valores por defecto:

- `MunicipalityService.createMunicipality()` → mapea DTO a entidad
- `WasteCollectorService.createCollector()` → mapea DTO a entidad
- `RewardService.createReward()` → mapea DTO a entidad

## Beneficios

1. **Separación de responsabilidades**: Los DTOs solo contienen campos que el usuario debe proporcionar
2. **Validación específica**: Validaciones apropiadas para cada campo
3. **Seguridad**: No se pueden enviar campos que se generan automáticamente
4. **Claridad**: Es evidente qué campos son requeridos vs opcionales
5. **Mantenibilidad**: Cambios en la entidad no afectan la API

## Campos Especiales

### municipalityId en RegisterRequest
- **Requerido para ciudadanos**: Los ciudadanos deben elegir una municipalidad al registrarse
- **Opcional para otros roles**: Los administradores pueden no tener municipalidad asignada

### Campos de ubicación en WasteCollector
- **zoneId**: Opcional, puede ser asignado después
- **sensorId**: Opcional, para integración con sensores IoT

### Campos de recompensa
- **expirationDate**: Opcional, para recompensas con fecha límite
- **termsAndConditions**: Opcional, para términos específicos
- **imageUrl**: Opcional, para imágenes de la recompensa

## Validaciones Implementadas

- **@NotBlank**: Para campos de texto requeridos
- **@NotNull**: Para campos numéricos requeridos
- **@Positive**: Para números que deben ser positivos
- **@Email**: Para validación de formato de email
- **@Size**: Para límites de longitud (en RegisterRequest)

## Próximos Pasos

1. ✅ DTOs creados y validados
2. ✅ Controladores actualizados
3. ✅ Servicios actualizados
4. ✅ Sin errores de compilación
5. 🔄 Probar endpoints con el frontend
6. 🔄 Documentar en Swagger/OpenAPI
