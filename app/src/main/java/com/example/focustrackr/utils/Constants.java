package com.example.focustrackr.utils;

/**
 * Clase de constantes utilizadas para preferencias y paso de datos entre actividades.
 */
public class Constants {

    // Nombre del archivo de preferencias compartidas
    public static final String PREFS_NAME = "app_prefs";

    // Claves para control de sesión de usuario
    public static final String PREF_LOGGED_IN = "logged_in";
    public static final String PREF_USER_EMAIL = "user_email";

    // Clave para envío de email entre actividades
    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";

    // Objetivo semanal de enfoque (minutos)
    public static final String PREF_WEEKLY_GOAL = "weekly_goal";
}