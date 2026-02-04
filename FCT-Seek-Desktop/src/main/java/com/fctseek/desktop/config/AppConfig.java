/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fctseek.desktop.config;

/**
 *
 * @author AlejandroR
 */

/*
Configuracion centrar y para la conexion con la api 
*/
public class AppConfig {
    
    // URL base del backend (Spring Boot)
    // Cambiarla cuando tengas el backend en producción
    public static final String API_BASE_URL = "http://localhost:8080/api";
    
    // Endpoints
    public static final String LOGIN_ENDPOINT = API_BASE_URL + "/auth/login";
    public static final String EMPRESAS_ENDPOINT = API_BASE_URL + "/empresas";
    public static final String DEPARTAMENTOS_ENDPOINT = API_BASE_URL + "/departamentos";
    public static final String CURSOS_ENDPOINT = API_BASE_URL + "/cursos";
    
    
    // Año académico actual
    public static final String ANIO_ACADEMICO_ACTUAL = "2024-2025";
}