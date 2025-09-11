package co.com.pragma.model.usuario;

import co.com.pragma.model.rol.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private Rol rolAdmin;
    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        rolAdmin = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .build();

        rolCliente = Rol.builder()
                .idRol(3)
                .nombre("CLIENTE")
                .descripcion("Cliente del banco")
                .build();
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyInstance() {
        Usuario usuario = new Usuario();
        
        assertNull(usuario.getIdUsuario());
        assertNull(usuario.getNombre());
        assertNull(usuario.getApellido());
        assertNull(usuario.getEmail());
        assertNull(usuario.getDocumentoIdentidad());
        assertNull(usuario.getTelefono());
        assertNull(usuario.getSalarioBase());
        assertNull(usuario.getRol());
    }

    @Test
    void allArgsConstructor_shouldCreateInstanceWithAllFields() {
        BigDecimal salario = new BigDecimal("2500000.00");
        
        Usuario usuario = new Usuario(
                1, 
                "Juan Carlos", 
                "Pérez González", 
                "juan.perez@email.com", 
                "12345678", 
                "3001234567", 
                salario, 
                rolAdmin
        );
        
        assertEquals(1, usuario.getIdUsuario());
        assertEquals("Juan Carlos", usuario.getNombre());
        assertEquals("Pérez González", usuario.getApellido());
        assertEquals("juan.perez@email.com", usuario.getEmail());
        assertEquals("12345678", usuario.getDocumentoIdentidad());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals(salario, usuario.getSalarioBase());
        assertEquals(rolAdmin, usuario.getRol());
    }

    @Test
    void builder_shouldCreateInstanceWithAllFields() {
        BigDecimal salario = new BigDecimal("1800000.50");
        
        Usuario usuario = Usuario.builder()
                .idUsuario(2)
                .nombre("María")
                .apellido("García López")
                .email("maria.garcia@empresa.com")
                .documentoIdentidad("87654321")
                .telefono("3109876543")
                .salarioBase(salario)
                .rol(rolCliente)
                .build();
        
        assertEquals(2, usuario.getIdUsuario());
        assertEquals("María", usuario.getNombre());
        assertEquals("García López", usuario.getApellido());
        assertEquals("maria.garcia@empresa.com", usuario.getEmail());
        assertEquals("87654321", usuario.getDocumentoIdentidad());
        assertEquals("3109876543", usuario.getTelefono());
        assertEquals(salario, usuario.getSalarioBase());
        assertEquals(rolCliente, usuario.getRol());
    }

    @Test
    void builder_withPartialFields_shouldCreateInstance() {
        Usuario usuario = Usuario.builder()
                .nombre("Carlos")
                .email("carlos@test.com")
                .rol(rolAdmin)
                .build();
        
        assertNull(usuario.getIdUsuario());
        assertEquals("Carlos", usuario.getNombre());
        assertNull(usuario.getApellido());
        assertEquals("carlos@test.com", usuario.getEmail());
        assertNull(usuario.getDocumentoIdentidad());
        assertNull(usuario.getTelefono());
        assertNull(usuario.getSalarioBase());
        assertEquals(rolAdmin, usuario.getRol());
    }

    @Test
    void toBuilder_shouldCreateBuilderFromExistingInstance() {
        Usuario original = Usuario.builder()
                .idUsuario(1)
                .nombre("Ana")
                .apellido("Rodríguez")
                .email("ana.rodriguez@email.com")
                .documentoIdentidad("11223344")
                .telefono("3001112233")
                .salarioBase(new BigDecimal("2000000"))
                .rol(rolCliente)
                .build();
        
        Usuario modified = original.toBuilder()
                .nombre("Ana María")
                .salarioBase(new BigDecimal("2200000"))
                .rol(rolAdmin)
                .build();
        
        assertEquals(original.getIdUsuario(), modified.getIdUsuario());
        assertEquals("Ana María", modified.getNombre());
        assertEquals(original.getApellido(), modified.getApellido());
        assertEquals(original.getEmail(), modified.getEmail());
        assertEquals(original.getDocumentoIdentidad(), modified.getDocumentoIdentidad());
        assertEquals(original.getTelefono(), modified.getTelefono());
        assertEquals(new BigDecimal("2200000"), modified.getSalarioBase());
        assertEquals(rolAdmin, modified.getRol());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        Usuario usuario = new Usuario();
        BigDecimal nuevoSalario = new BigDecimal("3000000.75");
        
        usuario.setIdUsuario(5);
        usuario.setNombre("Pedro");
        usuario.setApellido("Martínez Silva");
        usuario.setEmail("pedro.martinez@correo.com");
        usuario.setDocumentoIdentidad("55667788");
        usuario.setTelefono("3205556677");
        usuario.setSalarioBase(nuevoSalario);
        usuario.setRol(rolAdmin);
        
        assertEquals(5, usuario.getIdUsuario());
        assertEquals("Pedro", usuario.getNombre());
        assertEquals("Martínez Silva", usuario.getApellido());
        assertEquals("pedro.martinez@correo.com", usuario.getEmail());
        assertEquals("55667788", usuario.getDocumentoIdentidad());
        assertEquals("3205556677", usuario.getTelefono());
        assertEquals(nuevoSalario, usuario.getSalarioBase());
        assertEquals(rolAdmin, usuario.getRol());
    }

    @Test
    void setters_withNullValues_shouldAcceptNull() {
        Usuario usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Test")
                .email("test@email.com")
                .rol(rolCliente)
                .build();
        
        usuario.setIdUsuario(null);
        usuario.setNombre(null);
        usuario.setApellido(null);
        usuario.setEmail(null);
        usuario.setDocumentoIdentidad(null);
        usuario.setTelefono(null);
        usuario.setSalarioBase(null);
        usuario.setRol(null);
        
        assertNull(usuario.getIdUsuario());
        assertNull(usuario.getNombre());
        assertNull(usuario.getApellido());
        assertNull(usuario.getEmail());
        assertNull(usuario.getDocumentoIdentidad());
        assertNull(usuario.getTelefono());
        assertNull(usuario.getSalarioBase());
        assertNull(usuario.getRol());
    }

    @Test
    void salarioBase_withDifferentPrecision_shouldMaintainValue() {
        BigDecimal salarioConDecimales = new BigDecimal("1234567.89");
        BigDecimal salarioSinDecimales = new BigDecimal("5000000");
        
        Usuario usuario1 = Usuario.builder()
                .salarioBase(salarioConDecimales)
                .build();
        
        Usuario usuario2 = Usuario.builder()
                .salarioBase(salarioSinDecimales)
                .build();
        
        assertEquals(salarioConDecimales, usuario1.getSalarioBase());
        assertEquals(salarioSinDecimales, usuario2.getSalarioBase());
    }

    @Test
    void rol_assignment_shouldMaintainReference() {
        Usuario usuario = new Usuario();
        
        usuario.setRol(rolAdmin);
        assertSame(rolAdmin, usuario.getRol());
        assertEquals("ADMIN", usuario.getRol().getNombre());
        
        usuario.setRol(rolCliente);
        assertSame(rolCliente, usuario.getRol());
        assertEquals("CLIENTE", usuario.getRol().getNombre());
    }
}
