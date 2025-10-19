package com.dam.adp.proyectochatantoniodelgadoportero.utils;


import com.dam.adp.proyectochatantoniodelgadoportero.model.Mensaje;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de utilidad que proporciona métodos estáticos para realizar análisis estadísticos
 * sobre una lista de objetos Mensaje, utilizando la API de Java Streams.
 */
public class StreamUtils {
    private static final Logger log = LoggerFactory.getLogger(StreamUtils.class);

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private StreamUtils() {}

    /**
     * Cuenta el número total de mensajes en una lista.
     * * @param mensajes La lista de mensajes a contar.
     * @return El número total de mensajes, o 0 si la lista es null.
     */
    public static int contarMensajes(List<Mensaje> mensajes) {
        return mensajes == null ? 0 : mensajes.size();
    }

    /**
     * Cuenta la cantidad de mensajes enviados por cada usuario (remitente).
     * * @param mensajes La lista de mensajes a analizar.
     * @return Un Map donde la clave es el nombre del remitente (String) y el valor
     * es el número de mensajes enviados (Long).
     */
    public static Map<String, Long> contarMensajesPorUsuario(List<Mensaje> mensajes) {
        // Manejo de la lista nula
        if (mensajes == null) return Collections.emptyMap();

        return mensajes.stream()
                // 1. Asegura que ningún objeto Mensaje en la lista sea null.
                .filter(Objects::nonNull)
                // 2. Mapea cada mensaje a su remitente (String).
                //    Utiliza Optional para manejar un remitente que pueda ser null, reemplazándolo con ""
                //    para evitar NullPointerExceptions en la agrupación.
                .map(m -> Optional.ofNullable(m.getRemitente()).orElse(""))
                // 3. Recolecta los resultados agrupando por el nombre del remitente (r -> r)
                //    y contando las ocurrencias de cada grupo (Collectors.counting()).
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));
    }

    /**
     * Identifica las palabras más comunes en la conversación, filtrando las palabras cortas.
     * * @param mensajes La lista de mensajes a analizar.
     * @param topN El número de palabras más comunes que se desea obtener.
     * @return Un Map ordenado donde la clave es la palabra (String) y el valor es
     * la frecuencia de aparición (Long).
     */
    public static Map<String, Long> palabraMasComun(List<Mensaje> mensajes, int topN) {
        // Manejo de la lista nula o topN inválido
        if (mensajes == null || topN <= 0) return Collections.emptyMap();

        //Transformación a Stream de Palabras y Conteo ---
        Map<String, Long> wordCounts = mensajes.stream()
                // 1. Filtra mensajes nulos.
                .filter(Objects::nonNull)
                // 2. Mapea cada mensaje a su contenido de texto.
                .map(Mensaje::getMensaje)
                // 3. Filtra contenidos de mensaje nulos.
                .filter(Objects::nonNull)
                // 4. DESPLIEGUE (FlatMap): Convierte el stream de Strings (mensajes)
                //    en un stream de Strings (palabras).
                //    text.split("\\W+"): Divide el texto usando cualquier no-palabra (puntuación, espacio) como delimitador.
                .flatMap(text -> Arrays.stream(text.split("\\W+")))
                // 5. Normaliza: Convierte todas las palabras a minúsculas para un conteo uniforme.
                .map(String::toLowerCase)
                // 6. Filtra: Descarta palabras muy cortas (ej. "a", "el", "de") que no suelen ser informativas.
                .filter(token -> token.length() > 2)
                // 7. Agrupa y Cuenta: Recolecta todas las palabras y cuenta su frecuencia de aparición.
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        //Ordenación y Limitación del Top N ---
        return wordCounts.entrySet().stream()
                // 8. Ordena: Ordena las entradas del Map por el valor (conteo) en orden descendente.
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                // 9. Limita: Conserva solo las N primeras entradas (las más frecuentes).
                .limit(topN)
                // 10. Recolecta: Convierte el Stream ordenado de nuevo en un Map.
                //     Se usa LinkedHashMap para asegurar que el orden de clasificación se mantenga.
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  // Clave: la palabra
                        Map.Entry::getValue, // Valor: el conteo
                        (a, b) -> a, // Regla de fusión (no debería ser necesaria aquí)
                        LinkedHashMap::new // El tipo de Map para preservar el orden
                ));
    }

    /**
     * Formatea el Map de conteo (ej. mensajes por usuario) en una cadena legible.
     * Formato: "Clave: Valor, Clave2: Valor2"
     */
    public static String formatearConteoUsuario(Map<String, Long> conteo) {
        if (conteo == null || conteo.isEmpty()) {
            return "-";
        }
        return conteo.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * Formatea el Map de top palabras en una cadena legible con la frecuencia entre paréntesis.
     * Formato: "palabra (conteo), palabra2 (conteo2)"
     */
    public static String formatearTopPalabras(Map<String, Long> topPalabras) {
        if (topPalabras == null || topPalabras.isEmpty()) {
            return "-";
        }
        return topPalabras.entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "));
    }
}