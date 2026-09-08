package ni.edu.uam.facturacion.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FacturacionApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                FacturacionApplication.class.getResource("/ni/edu/uam/facturacion/fxml/menu-principal.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 600);
        stage.setTitle("Sistema de Facturacion");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
