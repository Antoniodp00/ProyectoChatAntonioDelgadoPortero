package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordManager {
    private static final Logger log = LoggerFactory.getLogger(PasswordManager.class);

    /**
     * Constructor privado para evitar la instanciación de la clase de utilidad.
     */
    private PasswordManager() {}

    /**
     * Este método toma una contraseña en texto plano y devuelve su hash.
     * El salt se genera automáticamente.
     *
     * @param plainPassword La contraseña sin encriptar que escribió el usuario.
     * @return Un string con el hash de la contraseña para guardar en la base de datos.
     */
    public static String hashPassword(String plainPassword) {
        // El método hashpw se encarga de todo: genera un salt y crea el hash.
        // El segundo argumento es el salt generado por gensalt().
        // El número 12 es el "costo" o "factor de trabajo". Un número más alto
        // hace que el hash sea más lento de calcular y, por lo tanto, más seguro.
        // 12 es un buen punto de partida.
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }


    /**
     * Verifica si una contraseña en texto plano coincide con un hash guardado.
     *
     * @param plainPassword La contraseña que el usuario ingresa en el login.
     * @param hashedPassword El hash que recuperaste de la base de datos.
     * @return `true` si las contraseñas coinciden, `false` en caso contrario.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // BCrypt extrae el salt del 'hashedPassword' y lo usa para comparar.
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
