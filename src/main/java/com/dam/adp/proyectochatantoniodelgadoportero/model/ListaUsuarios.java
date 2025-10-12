package com.dam.adp.proyectochatantoniodelgadoportero.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "usuarios")
public class ListaUsuarios {
    private ArrayList<Usuario> listaUsuarios = new ArrayList<>();

    @XmlElement(name = "usuario")
    public List<Usuario> getLista(){
        return listaUsuarios;
    }

    public void setLista(ArrayList<Usuario> lista){
        this.listaUsuarios = lista;
    }
}
