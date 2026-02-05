package com.practicas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona los datos de la aplicación (usuarios y empresas)
 * En una aplicación real, esto se conectaría a una base de datos
 */
public class DataService {
    
    private static DataService instance;
    private List<Usuario> usuarios;
    private List<Empresa> empresas;
    private Usuario usuarioActual;

    private DataService() {
        usuarios = new ArrayList<>();
        empresas = new ArrayList<>();
        cargarDatosIniciales();
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    /**
     * Carga datos de ejemplo para demostración
     */
    private void cargarDatosIniciales() {
        // Usuarios de ejemplo
        usuarios.add(new Usuario("alumno1", "1234", TipoUsuario.ALUMNO, "María García López"));
        usuarios.add(new Usuario("alumno2", "1234", TipoUsuario.ALUMNO, "Carlos Rodríguez Pérez"));
        usuarios.add(new Usuario("profesor1", "admin", TipoUsuario.PROFESOR, "Dr. Antonio Martínez"));
        usuarios.add(new Usuario("profesor2", "admin", TipoUsuario.PROFESOR, "Dra. Laura Sánchez"));

        // Empresas de ejemplo
        empresas.add(new Empresa(
            "TechSolutions S.L.",
            "Empresa de desarrollo de software especializada en aplicaciones web y móviles.",
            "Excelente ambiente de trabajo. Los tutores son muy atentos y se aprende mucho. " +
            "Recomendada para estudiantes de DAW y DAM.",
            "Tecnología",
            "Madrid Centro",
            4, 4  // OCUPADA
        ));

        empresas.add(new Empresa(
            "DataAnalytics Corp",
            "Consultora especializada en análisis de datos y Business Intelligence.",
            "Buen lugar para aprender sobre bases de datos y visualización. " +
            "Horario flexible y posibilidad de teletrabajo parcial.",
            "Consultoría IT",
            "Barcelona",
            4, 2  // LIBRE
        ));

        empresas.add(new Empresa(
            "CloudServices Inc",
            "Proveedor de servicios en la nube y hosting para empresas.",
            "Muy tecnológica, trabajan con AWS y Azure. Ideal para quien quiera " +
            "especializarse en infraestructura cloud.",
            "Cloud Computing",
            "Valencia",
            4, 0  // LIBRE
        ));

        empresas.add(new Empresa(
            "CyberSecure Labs",
            "Empresa de ciberseguridad y auditorías de sistemas.",
            "Exigente pero muy formativa. Se trabaja con herramientas profesionales " +
            "de pentesting y análisis de vulnerabilidades.",
            "Ciberseguridad",
            "Sevilla",
            4, 4  // OCUPADA
        ));

        empresas.add(new Empresa(
            "WebDesign Studio",
            "Agencia de diseño web y marketing digital.",
            "Ambiente creativo y dinámico. Perfecta para quien le guste el diseño " +
            "y la experiencia de usuario.",
            "Diseño Web",
            "Bilbao",
            4, 1  // LIBRE
        ));

        empresas.add(new Empresa(
            "GameDev Studios",
            "Estudio de desarrollo de videojuegos indie.",
            "¡Increíble experiencia! Se trabaja con Unity y Unreal Engine. " +
            "Muy recomendada para apasionados de los videojuegos.",
            "Videojuegos",
            "Málaga",
            4, 3  // LIBRE (1 plaza)
        ));
    }

    /**
     * Intenta autenticar un usuario
     */
    public Usuario autenticar(String username, String password, TipoUsuario tipoEsperado) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && 
                u.getPassword().equals(password) && 
                u.getTipo() == tipoEsperado) {
                usuarioActual = u;
                return u;
            }
        }
        return null;
    }

    /**
     * Obtiene el usuario actualmente logueado
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Obtiene todas las empresas
     */
    public List<Empresa> getEmpresas() {
        return new ArrayList<>(empresas);
    }

    /**
     * Busca empresas por nombre o sector
     */
    public List<Empresa> buscarEmpresas(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return getEmpresas();
        }
        String filtroLower = filtro.toLowerCase();
        return empresas.stream()
            .filter(e -> e.getNombre().toLowerCase().contains(filtroLower) ||
                        e.getSector().toLowerCase().contains(filtroLower) ||
                        e.getUbicacion().toLowerCase().contains(filtroLower))
            .collect(Collectors.toList());
    }

    /**
     * Añade una nueva empresa (solo profesores)
     */
    public boolean agregarEmpresa(Empresa empresa) {
        if (usuarioActual != null && usuarioActual.esProfesor()) {
            empresas.add(empresa);
            return true;
        }
        return false;
    }

    /**
     * Elimina una empresa (solo profesores)
     */
    public boolean eliminarEmpresa(Empresa empresa) {
        if (usuarioActual != null && usuarioActual.esProfesor()) {
            return empresas.remove(empresa);
        }
        return false;
    }

    /**
     * Actualiza las plazas de una empresa (solo profesores)
     */
    public boolean actualizarPlazas(Empresa empresa, int nuevasPlazasOcupadas) {
        if (usuarioActual != null && usuarioActual.esProfesor()) {
            empresa.setPlazasOcupadas(nuevasPlazasOcupadas);
            return true;
        }
        return false;
    }

    /**
     * Registra un nuevo usuario
     */
    public boolean registrarUsuario(String username, String password, TipoUsuario tipo, String nombreCompleto) {
        // Verificar si el username ya existe
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username)) {
                return false;
            }
        }
        usuarios.add(new Usuario(username, password, tipo, nombreCompleto));
        return true;
    }
}
