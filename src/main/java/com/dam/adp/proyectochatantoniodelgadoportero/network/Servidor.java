
package com.dam.adp.proyectochatantoniodelgadoportero.network;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;
import com.dam.adp.proyectochatantoniodelgadoportero.utils.XMLManager; // Reutilizamos para serializar/deserializar

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servidor de chat que gestiona múltiples clientes concurrentemente.
 * Escucha en un puerto, acepta conexiones y reenvía los mensajes recibidos a todos los demás clientes.
 */
public class Servidor {

    private static final int PUERTO = 12345;
    private static final Logger log = LoggerFactory.getLogger(Servidor.class);

    // Un conjunto para almacenar los PrintWriters de todos los clientes conectados.
    // Es thread-safe para añadir y quitar elementos de forma segura desde múltiples hilos.
    private static final Set<PrintWriter> writers = new HashSet<>();

    /**
     * Método principal para iniciar el servidor de chat.
     */
    public static void main(String[] args) {
        log.info("Iniciando el servidor de chat en el puerto {}...", PUERTO);
        // Usamos un pool de hilos para gestionar eficientemente los clientes.
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket listener = new ServerSocket(PUERTO)) {
            log.info("Servidor escuchando. Esperando conexiones de clientes...");
            while (true) {
                // Acepta una nueva conexión de cliente y la asigna a un hilo del pool.
                pool.execute(new ClientHandler(listener.accept()));
            }
        } catch (IOException e) {
            log.error("Error al iniciar o ejecutar el servidor.", e);
        }
    }

    /**
     * Hilo para manejar la comunicación con un único cliente.
     * Cada instancia de esta clase se ejecuta en su propio hilo.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private final Marshaller marshaller;
        private final Unmarshaller unmarshaller;

        /**
         * Constructor que recibe el socket del cliente conectado.
         * @param socket El socket para la comunicación con el cliente.
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                JAXBContext context = JAXBContext.newInstance(Mensaje.class);
                this.marshaller = context.createMarshaller();
                this.unmarshaller = context.createUnmarshaller();
                // No formatear la salida para que sea una sola línea
                this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            } catch (JAXBException e) {
                throw new RuntimeException("No se pudo inicializar JAXB", e);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Añade el PrintWriter del cliente al conjunto global para poder enviarle mensajes.
                synchronized (writers) {
                    writers.add(out);
                }
                log.info("Nuevo cliente conectado. Total de clientes: {}", writers.size());

                String line;
                while ((line = in.readLine()) != null) {
                    // Reenvía el mensaje XML a todos los demás clientes.
                    broadcast(line);
                }
            } catch (IOException e) {
                log.warn("El cliente se ha desconectado o ha ocurrido un error de red.", e);
            } finally {
                // Bloque de limpieza para cuando el cliente se desconecta.
                if (out != null) {
                    synchronized (writers) {
                        writers.remove(out);
                    }
                    log.info("Cliente desconectado. Clientes restantes: {}", writers.size());
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("Error al cerrar el socket del cliente.", e);
                }
            }
        }

        /**
         * Envía un mensaje (en formato XML) a todos los clientes conectados.
         * @param messageXml El mensaje en formato XML a transmitir.
         */
        private void broadcast(String messageXml) {
            log.info("Retransmitiendo mensaje: {}", messageXml);
            synchronized (writers) {
                for (PrintWriter writer : writers) {
                    writer.println(messageXml);
                }
            }
        }
    }
}
