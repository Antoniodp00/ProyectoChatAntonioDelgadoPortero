module com.dam.adp.proyectochatantoniodelgadoportero {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.xml.bind;

    opens com.dam.adp.proyectochatantoniodelgadoportero to javafx.fxml;
    exports com.dam.adp.proyectochatantoniodelgadoportero;
    exports com.dam.adp.proyectochatantoniodelgadoportero.app;
    opens com.dam.adp.proyectochatantoniodelgadoportero.app to javafx.fxml;
}