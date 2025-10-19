package com.dam.adp.proyectochatantoniodelgadoportero;

import com.dam.adp.proyectochatantoniodelgadoportero.DAO.MensajeDAO;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensajes;
import com.dam.adp.proyectochatantoniodelgadoportero.model.Usuario;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prueba {
    private static final Logger log = LoggerFactory.getLogger(Prueba.class);
    public static void main(String[] args) {
        log.info("Ejecutando Prueba.main()");
        Usuario u1 = new Usuario("antoniodp","1234","antonio","delgado","antonio@gmail.com");
        Usuario u2 = new Usuario("antoniodp00","1234","antonio","delgado","antonio@gmail.com");

        MensajeDAO.enviarMensaje(u1.getNombreUsuario(),u2.getNombreUsuario(),"Hola");

        log.info("Prueba finalizada");
    }
}
