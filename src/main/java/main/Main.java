package main;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import controllers.GeneralController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;


/**
 * Created by Francis Cáceres on 13/7/2017.
 */
public class Main extends Application{
    private Stage primaryStage;

    public static void main(String[] args) {

        launch(args);

       /* //Estableciendo conexion con la base de datos
        MongoClient mongoClient = new MongoClient("localhost",27017);

        //Conectando la base de datos
        MongoDatabase database = mongoClient.getDatabase("final");

        //Colecciones con las que trabajar
        MongoCollection<Document> Articulo = database.getCollection("Articulo");
        MongoCollection<Document> ArticuloSuplidor = database.getCollection("ArticuloSuplidor");
        MongoCollection<Document> MovimientoInventario = database.getCollection("MovimientoInventario");
        MongoCollection<Document> OrdenCompra = database.getCollection("OrdenCompra");*/


    }

//    public Stage getPrimaryStage(){
//        return primaryStage;
//    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Final Tecnologias Emergentes");

        //entrada del sistema
        loginLayout();

    }


    public void loginLayout(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            URL url = Main.class.getResource("/fxml/general.fxml");
            System.out.println("La URL: "+url.toString());
            loader.setLocation(url);
            AnchorPane anchorPane = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(anchorPane);
            primaryStage.setScene(scene);

            //recuperando el controlador.
            GeneralController generalController = loader.getController();
            generalController.setMainApp(this);

            //
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
