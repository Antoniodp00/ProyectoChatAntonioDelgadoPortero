package com.dam.adp.proyectochatantoniodelgadoportero.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "usuarios")
public class ListaUsuarios {
    private static final Logger log = LoggerFactory.getLogger(ListaUsuarios.class);
    private ArrayList<Usuario> listaUsuarios = new ArrayList<>();

    @XmlElement(name = "usuario")
    /**
     * Obtiene la lista de usuarios.
     * @return lista interna de usuarios.
     */
    public List<Usuario> getLista(){
        return listaUsuarios;
    }

    /**
     * Establece la lista de usuarios.
     * @param lista nueva lista a asignar.
     */
    public void setLista(ArrayList<Usuario> lista){
        this.listaUsuarios = lista;
        log.debug("ListaUsuarios actualizada. Tamaño: {}", lista != null ? lista.size() : 0);
    }
}
