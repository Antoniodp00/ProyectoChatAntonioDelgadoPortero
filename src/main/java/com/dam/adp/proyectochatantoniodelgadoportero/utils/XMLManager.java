package com.dam.adp.proyectochatantoniodelgadoportero.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLManager {
    private static final Logger log = LoggerFactory.getLogger(XMLManager.class);

    /**
     * Constructor privado para evitar la instanciación de la clase de utilidad.
     */
    private XMLManager() {}

    /**
     * Escribe un objeto en un archivo XML.
     *
     * @param objeto   El objeto a serializar en XML.
     * @param fileName El nombre del archivo XML donde se guardará el objeto.
     * @param <T>      Tipo del objeto a serializar.
     * @return true si la escritura fue exitosa, false en caso contrario.
     */
    public static <T> boolean writeXML(T objeto, String fileName) {
        boolean result = false;
        try {
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(objeto,new File(fileName));
            result = true;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Lee un objeto desde un archivo XML.
     *
     * @param objeto   Un objeto de referencia para obtener su clase.
     * @param fileName El nombre del archivo XML a leer.
     * @param <T>      Tipo del objeto a deserializar.
     * @return El objeto deserializado desde el XML.
     */
    public static <T> T readXML(T objeto, String fileName) {
        File file = new File(fileName);


        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                writeXML(objeto, fileName);
            } catch (IOException e) {
                throw new RuntimeException("Error al crear el archivo XML: " + e.getMessage(), e);
            }
        }

        T result = objeto;
        try {
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            result = (T) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            log.warn("Archivo XML vacío o corrupto, usando objeto vacío.");
        }

        return result;
    }


}