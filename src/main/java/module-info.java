module ni.edu.uam.facturacion {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens ni.edu.uam.facturacion.controller to javafx.fxml;
    opens ni.edu.uam.facturacion.model to javafx.base;
    exports ni.edu.uam.facturacion.application;
    exports ni.edu.uam.facturacion.controller;
    exports ni.edu.uam.facturacion.model;
    exports ni.edu.uam.facturacion.util;
}
