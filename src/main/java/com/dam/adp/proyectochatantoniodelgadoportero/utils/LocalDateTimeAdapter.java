package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Adaptador JAXB para serializar y deserializar LocalDateTime a/desde String.
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter STORAGE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final DateTimeFormatter LEGACY_TIME_ONLY = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Convierte un String a LocalDateTime, soportando varios formatos.
     * @param v cadena de fecha/hora.
     * @return LocalDateTime equivalente o null si no es válido.
     */
    @Override
    public LocalDateTime unmarshal(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        String val = v.trim();

        try {
            return LocalDateTime.parse(val);
        } catch (DateTimeParseException ignored) { }
        try {
            return LocalDateTime.parse(val, STORAGE_FORMAT);
        } catch (DateTimeParseException ignored) { }

        try {
            LocalTime t = LocalTime.parse(val, LEGACY_TIME_ONLY);
            return LocalDateTime.of(LocalDate.now(), t);
        } catch (DateTimeParseException ignored) { }

        return null;
    }

    /**
     * Convierte un LocalDateTime a su representación String para almacenamiento XML.
     * @param v fecha/hora a formatear.
     * @return cadena formateada o null si v es null.
     */
    @Override
    public String marshal(LocalDateTime v) {
        return v == null ? null : v.format(STORAGE_FORMAT);
    }
}
