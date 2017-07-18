package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Francis Cáceres on 13/7/2017.
 */
public class GeneralController {

    @FXML
    Button generarOrden,registrar,suplidores;

    private Main mainApp;
    public Stage window = new Stage();

    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }


    public void compraAutomatica(){
        seleccion("/fxml/compra.fxml","comp");
    }

    public void registrarMov(){
        seleccion("/fxml/registrar.fxml","reg");
    }



    public void seleccion(String ruta,String elect){
        try {
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader();
            URL url = GeneralController.class.getResource(ruta);
            loader.setLocation(url);
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            window.setScene(scene);

            if (elect.equals("comp")){
                CompraController cc = new CompraController();
                cc.inicializarApp();
            }else if (elect.equals("reg")){
                RegistrarController rc = new RegistrarController();
                rc.inicializarApp();
            }

            window.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
        }
    }




}
