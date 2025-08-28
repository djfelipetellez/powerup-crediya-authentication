# Análisis de Tests - Casos de Uso

## Problema Identificado

Los tests de `UsuarioUseCase` fallan porque el **flujo reactivo Mono se construye completamente** antes de ejecutarse,
incluso cuando las validaciones individuales fallan.

### Flujo Real del Caso de Uso:

```java
return validarSalario(usuario)
    .then(validarEmailDuplicado(usuario.getEmail()))
    .then(rolUseCase.validarRolExiste(roleId))
    .then(usuarioRepository.registrarUsuarioCompleto(usuario, roleId));
```

### ¿Qué ocurre?

1. **Pipeline Construction**: Reactor construye todo el pipeline antes de ejecutar
2. **Mock Requirements**: TODOS los métodos en el pipeline necesitan mocks configurados
3. **Execution Flow**: El error ocurre durante la ejecución, no durante la construcción

## ✅ Tests Correctos vs ❌ Tests Incorrectos

### ❌ **Enfoque Actual (Incorrecto):**

```java
// Solo configura mocks parciales, pensando que el flujo se detiene antes
@Test
void testSalaryValidation() {
    // NO configuramos mocks para findByEmail, validarRolExiste, registrarUsuarioCompleto
    // RESULTADO: NullPointerException durante construcción del pipeline
}
```

### ✅ **Enfoque Correcto:**

```java
// Configura TODOS los mocks necesarios para el pipeline completo
@Test 
void testSalaryValidation() {
    when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
    when(rolUseCase.validarRolExiste(anyInt())).thenReturn(Mono.empty());
    when(usuarioRepository.registrarUsuarioCompleto(any(), anyInt())).thenReturn(Mono.just(usuario));
    
    // El test pasa porque el pipeline se construye correctamente
    // El error ocurre durante la EJECUCIÓN en validarSalario()
}
```

## 🔧 Recomendación

**TODOS los tests de flujo reactivo deben configurar TODOS los mocks del pipeline**, incluso si esperan que falle en un
paso anterior.

Los tests están bien diseñados conceptualmente, solo necesitan la configuración completa de mocks.