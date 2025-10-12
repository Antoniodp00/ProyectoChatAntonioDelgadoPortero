module com.dam.adp.proyectochatantoniodelgadoportero {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.xml.bind;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // JavaFX
    opens com.dam.adp.proyectochatantoniodelgadoportero to javafx.fxml;
    opens com.dam.adp.proyectochatantoniodelgadoportero.app to javafx.fxml;
    opens com.dam.adp.proyectochatantoniodelgadoportero.model to javafx.fxml, java.xml.bind; // <-- aquí
    opens com.dam.adp.proyectochatantoniodelgadoportero.controller to javafx.fxml;
    opens com.dam.adp.proyectochatantoniodelgadoportero.DAO to javafx.fxml;
    opens com.dam.adp.proyectochatantoniodelgadoportero.utils to javafx.fxml;

    // exports
    exports com.dam.adp.proyectochatantoniodelgadoportero.app;
    exports com.dam.adp.proyectochatantoniodelgadoportero.controller;
    exports com.dam.adp.proyectochatantoniodelgadoportero.model;
    exports com.dam.adp.proyectochatantoniodelgadoportero.DAO;
    exports com.dam.adp.proyectochatantoniodelgadoportero.utils;
}
