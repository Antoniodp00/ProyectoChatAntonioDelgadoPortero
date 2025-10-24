package com.dam.adp.proyectochatantoniodelgadoportero.network;

import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeración para representar los posibles estados de la conexión de red.
 */
enum EstadoRed {
    CONECTADO,
    DESCONECTADO,
    ERROR
}

/**
 * Cliente de red para la aplicación de chat.
 * Se conecta a un servidor, envía mensajes y escucha los mensajes entrantes en un hilo separado.
 */
public class Cliente {

    private static final String HOST = "localhost";
    private static final int PUERTO = 12345;
    private static final Logger log = LoggerFactory.getLogger(Cliente.class);

    private Socket socket;
    private PrintWriter out;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    // Un "Consumer" para pasar los mensajes recibidos a la UI de forma segura.
    private final Consumer<Mensaje> onMessageReceived;
    // Un nuevo Consumer para notificar cambios en el estado de la red a la UI.
    private final Consumer<EstadoRed> onNetworkStateChanged;

    /**
     * Constructor del cliente.
     * @param onMessageReceived Un callback (función) que se ejecutará en el hilo de la UI
     *                          cada vez que se reciba un mensaje del servidor.
     * @param onNetworkStateChanged Un callback que se ejecutará cuando el estado de la conexión cambie.
     */
    public Cliente(Consumer<Mensaje> onMessageReceived, Consumer<EstadoRed> onNetworkStateChanged) {
        this.onMessageReceived = onMessageReceived;
        this.onNetworkStateChanged = onNetworkStateChanged;
        try {
            JAXBContext context = JAXBContext.newInstance(Mensaje.class);
            this.marshaller = context.createMarshaller();
            this.unmarshaller = context.createUnmarshaller();
            // Queremos que el XML sea una sola línea para enviarlo fácilmente.
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        } catch (JAXBException e) {
            log.error("Error al inicializar JAXB en el cliente", e);
            // Notificar error al inicializar JAXB
            if (onNetworkStateChanged != null) {
                onNetworkStateChanged.accept(EstadoRed.ERROR);
            }
            throw new RuntimeException("No se pudo inicializar JAXB", e);
        }
    }

    /**
     * Conecta el cliente al servidor e inicia un hilo para escuchar mensajes.
     */
    public void conectar() {
        try {
            socket = new Socket(HOST, PUERTO);
            out = new PrintWriter(socket.getOutputStream(), true);

            // Inicia un nuevo hilo para escuchar constantemente los mensajes del servidor.
            Thread listenerThread = new Thread(this::escucharMensajes);
            listenerThread.setDaemon(true); // El hilo terminará si la aplicación principal se cierra.
            listenerThread.start();
            log.info("Cliente conectado al servidor en {}:{}.", HOST, PUERTO);
            // Notificar que la conexión fue exitosa.
            if (onNetworkStateChanged != null) {
                onNetworkStateChanged.accept(EstadoRed.CONECTADO);
            }

        } catch (UnknownHostException e) {
            log.error("Host del servidor no encontrado: {}", HOST, e);
            if (onNetworkStateChanged != null) {
                onNetworkStateChanged.accept(EstadoRed.ERROR);
            }
        } catch (IOException e) {
            log.error("No se pudo conectar al servidor en {}:{}. Asegúrate de que el servidor está en ejecución.", HOST, PUERTO, e);
            if (onNetworkStateChanged != null) {
                onNetworkStateChanged.accept(EstadoRed.ERROR);
            }
        }
    }

    /**
     * Tarea que se ejecuta en un hilo de fondo para leer mensajes del servidor.
     */
    private void escucharMensajes() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String lineaXml;
            while ((lineaXml = in.readLine()) != null) {
                try {
                    // Deserializa el XML a un objeto Mensaje.
                    StringReader reader = new StringReader(lineaXml);
                    Mensaje mensaje = (Mensaje) unmarshaller.unmarshal(reader);

                    // Usa el callback para pasar el mensaje al hilo de la UI.
                    if (onMessageReceived != null) {
                        // Platform.runLater se usará en el Controller para asegurar que la UI se actualice de forma segura.
                        onMessageReceived.accept(mensaje);
                    }
                } catch (JAXBException e) {
                    log.warn("Error al deserializar XML del servidor: {}", lineaXml, e);
                }
            }
        } catch (IOException e) {
            log.warn("Se perdió la conexión con el servidor.", e);
            // Notificar que la conexión se ha perdido.
            if (onNetworkStateChanged != null) {
                onNetworkStateChanged.accept(EstadoRed.DESCONECTADO);
            }
        } finally {
            desconectar();
        }
    }

    /**
     * Envía un objeto Mensaje al servidor, serializándolo a XML.
     * @param mensaje El objeto Mensaje a enviar.
     */
    public void enviarMensaje(Mensaje mensaje) {
        if (out == null) {
            log.error("No se puede enviar mensaje, el cliente no está conectado.");
            return;
        }
        try {
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(mensaje, stringWriter);
            String lineaXml = stringWriter.toString();

            // Envía la línea de XML al servidor.
            out.println(lineaXml);
        } catch (JAXBException e) {
            log.error("Error al serializar mensaje a XML", e);
        }
    }

    /**
     * Cierra la conexión del cliente con el servidor.
     */
    public void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                log.info("Cliente desconectado.");
                // Notificar que la conexión se ha cerrado.
                if (onNetworkStateChanged != null) {
                    onNetworkStateChanged.accept(EstadoRed.DESCONECTADO);
                }
            }
        } catch (IOException e) {
            log.error("Error al cerrar el socket del cliente.", e);
        }
    }
}
