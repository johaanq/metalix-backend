# 🔧 Cambios Requeridos en el Backend

## 1. Permitir acceso público a municipalidades (GET) ⚠️ URGENTE

### Problema
El endpoint `/api/v1/municipalities` requiere autenticación, pero los usuarios necesitan ver la lista de municipalidades **antes** de registrarse.

### Solución
Modificar `SecurityConfiguration.java` para permitir acceso público al GET de municipalidades:

**Archivo:** `src/main/java/com/metalix/metalixbackend/iam/infrastructure/security/SecurityConfiguration.java`

**Cambio en el método `securityFilterChain`:**

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/",
                            "/api/v1/auth/**",
                            "/api/v1/municipalities",              // ✅ AGREGAR ESTA LÍNEA
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/v3/api-docs.yaml",
                            "/swagger-resources/**",
                            "/webjars/**",
                            "/configuration/**",
                            "/actuator/**",
                            "/actuator/health/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

### Alternativa (más específica)
Si quieres permitir solo GET y restringir POST/PUT/DELETE:

```java
.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.GET, "/api/v1/municipalities").permitAll()  // Solo GET público
        .requestMatchers(
                "/",
                "/api/v1/auth/**",
                "/swagger-ui/**",
                // ... resto de endpoints públicos
        ).permitAll()
        .anyRequest().authenticated()
)
```

### Verificación
Después del cambio, probar:
```bash
curl -X GET http://localhost:8080/api/v1/municipalities
```

Debería retornar la lista de municipalidades sin requerir el header `Authorization`.

---

## 2. Convertir IDs de string a number (Opcional - Ya manejado en frontend)

### Nota
El backend usa `Long` (números) para los IDs, pero el frontend Angular los maneja como strings. La conversión se hace automáticamente al enviar las peticiones HTTP, así que **no se requiere cambio**.

---

## 3. Configurar CORS (Ya está configurado ✅)

El CORS ya está correctamente configurado en `SecurityConfiguration.java`:
- ✅ Permite `http://localhost:4200`
- ✅ Permite `https://*.vercel.app`
- ✅ Permite métodos: GET, POST, PUT, DELETE, PATCH, OPTIONS
- ✅ Permite headers: Authorization, Content-Type, Accept

---

## Prioridad de Cambios

| Cambio | Prioridad | Estado |
|--------|-----------|--------|
| Permitir GET `/api/v1/municipalities` sin auth | 🔴 ALTA | Pendiente |
| CORS configurado | ✅ Completo | ✅ |
| Endpoints auth funcionando | ✅ Completo | ✅ |

---

## Después de aplicar los cambios

1. **Reiniciar el backend**:
   ```bash
   mvn spring-boot:run
   ```

2. **Probar el endpoint**:
   ```bash
   curl http://localhost:8080/api/v1/municipalities
   ```

3. **Probar el registro** desde el frontend:
   - Ir a `http://localhost:4200/auth/register`
   - El dropdown de municipalidades debería cargar sin problemas
   - Completar el formulario y registrarse como CITIZEN

---

## Contacto
Si necesitas ayuda con estos cambios, avísame.

