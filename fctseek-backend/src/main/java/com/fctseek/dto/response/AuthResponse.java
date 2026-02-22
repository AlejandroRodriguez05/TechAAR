package com.fctseek.dto.response;

/**
 * DTO para la respuesta de autenticación.
 */
public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String email;
    private String nombre;
    private String apellidos;
    private String nif;
    private String rol;
    private Long departamentoId;
    private String departamentoNombre;

    // Constructores
    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String email, String nombre, 
                        String apellidos, String nif, String rol) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nif = nif;
        this.rol = rol;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private Long id;
        private String email;
        private String nombre;
        private String apellidos;
        private String nif;
        private String rol;
        private Long departamentoId;
        private String departamentoNombre;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder apellidos(String apellidos) {
            this.apellidos = apellidos;
            return this;
        }

        public Builder nif(String nif) {
            this.nif = nif;
            return this;
        }

        public Builder rol(String rol) {
            this.rol = rol;
            return this;
        }

        public Builder departamentoId(Long departamentoId) {
            this.departamentoId = departamentoId;
            return this;
        }

        public Builder departamentoNombre(String departamentoNombre) {
            this.departamentoNombre = departamentoNombre;
            return this;
        }

        public AuthResponse build() {
            AuthResponse response = new AuthResponse();
            response.token = this.token;
            response.id = this.id;
            response.email = this.email;
            response.nombre = this.nombre;
            response.apellidos = this.apellidos;
            response.nif = this.nif;
            response.rol = this.rol;
            response.departamentoId = this.departamentoId;
            response.departamentoNombre = this.departamentoNombre;
            return response;
        }
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getDepartamentoNombre() {
        return departamentoNombre;
    }

    public void setDepartamentoNombre(String departamentoNombre) {
        this.departamentoNombre = departamentoNombre;
    }
}
