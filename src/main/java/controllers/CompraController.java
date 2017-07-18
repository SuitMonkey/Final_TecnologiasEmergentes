package controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.ZoneId;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Francis Cáceres on 13/7/2017.
 */
public class CompraController {
    CompraController cc;
    ObservableList<TablaCompra> data = FXCollections.observableArrayList();
    List<TablaCompra> objetivo = new ArrayList<>();
    List<modelo.Articulo> todosArticulos = new ArrayList<>();


    //Estableciendo conexion con la base de datos
    MongoClient mongoClient = new MongoClient("localhost",27017);

    //Conectando la base de datos
    MongoDatabase database = mongoClient.getDatabase("final");

    //Colecciones con las que trabajar
    FindIterable<Document> Articulo = database.getCollection("Articulo").find();

    @FXML
    ComboBox artCB;

    @FXML
    TextField cantTF;

    @FXML
    DatePicker fecDP;

    @FXML
    TableColumn <TablaCompra, String> clCod,clArt,clCantidad,clFec;

    @FXML
    TableView tCompra;

    @FXML
    public void initialize(){
        ObservableList<String> opciones = FXCollections.observableArrayList();

//        clCod.setCellValueFactory( new PropertyValueFactory<>("codigo"));
        clArt.setCellValueFactory( new PropertyValueFactory<>("articulo"));
        clCantidad.setCellValueFactory( new PropertyValueFactory<>("cantidad"));
        clFec.setCellValueFactory( new PropertyValueFactory<>("fecDeseada"));

        Double totalCantidad = 0.0;

        for(Document d : Articulo){
            List<Document> cantidad = d.get("almacen",List.class);
            for(Document c : cantidad){
                totalCantidad += c.getDouble("balanceActual");
            }

            System.out.println(d);
            todosArticulos.add(new Articulo((Double)d.get("codigoArticulo"),d.get("descripcion").toString(),totalCantidad));
            opciones.add(d.get("descripcion").toString());
            totalCantidad = 0.0;
        }

        artCB.setItems(opciones);

    }

    public void inicializarApp() {
        System.out.println("Inicializando CompraController...");

    }


    public void agregarArt(){

        TablaCompra tabla = new TablaCompra();

        tabla.setArticulo(artCB.getValue().toString());
        tabla.setCantidad(cantTF.getText());
        tabla.setFecDeseada(fecDP.getValue().toString());

        data.add(tabla);
        objetivo.add(tabla);

        tCompra.setItems(data);
    }

    public void generar(){
        List<Target> sup1 = new ArrayList<>();
        Double total1 = 0.0;
        List<Target> sup2 = new ArrayList<>();
        Double total2 = 0.0;
        List<Target> sup3 = new ArrayList<>();
        Double total3 = 0.0;

        List<Orden> ordens = new ArrayList<>();

        List<Articulos> estos1 = new ArrayList<>();
        List<Articulos> estos2 = new ArrayList<>();
        List<Articulos> estos3 = new ArrayList<>();

        Orden orden1 = null;
        Orden orden2 = null;
        Orden orden3 = null;


        String suplidor = null;
        Double dias = 0.0;
        Double precio = 0.0;
        Double codArtt = 0.0;


        for(int i = 0; i<objetivo.size();i++){
            TablaCompra choose = objetivo.get(i);
            int con = 0;

            for(int k = 0; k < todosArticulos.size(); k++){
                if(choose.getArticulo().equals(todosArticulos.get(k).getDescripcion())){
                    Bson[] query = {eq("codigoArticulo",todosArticulos.get(k).getCodigoArticulo())};
                    FindIterable<Document> ArticuloSuplidor = database.getCollection("ArticuloSuplidor").find(and(query));

                    System.out.println("Cantidad total del articulo: "+ todosArticulos.get(k).getCantidadTotal().toString());

                    if(todosArticulos.get(k).getCantidadTotal() >= Double.parseDouble(cantTF.getText())){
                        System.out.println("No es necesario, ya existe esa cantidad");
                    }else{

                        //conseguir el mejor suplidor y en cuantos dias
                        for (Document a : ArticuloSuplidor){

                            if(con == 0){
                                codArtt = a.getDouble("codigoArticulo");
                                suplidor = a.get("codigoSuplidor").toString();
                                dias = Double.parseDouble(a.get("tiempoEntrega").toString());
                                precio = a.getDouble("precioCompra");
                            }else{
                                if(dias > Double.parseDouble(a.get("tiempoEntrega").toString())){
                                    codArtt = a.getDouble("codigoArticulo");
                                    suplidor = a.get("codigoSuplidor").toString();
                                    dias = Double.parseDouble(a.get("tiempoEntrega").toString());
                                    precio = a.getDouble("precioCompra");
                                }
                            }

                            con++;
                        }
                        Date hoy = new Date();
                        Date date = Date.from(fecDP.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                        int diasLleguen = ((int)( (date.getTime() - hoy.getTime()) / (1000 * 60 * 60 * 24)))+1;

                        if(diasLleguen < dias){
                            System.out.println("Orden no se puede cumplir, pocos dias");
                        }else {
                            Target target = new Target(codArtt,choose.getArticulo(),Double.parseDouble(objetivo.get(i).getCantidad()),precio,date,todosArticulos.get(k).getCantidadTotal(),dias);
                            if(suplidor.equals("2001")){
                                sup1.add(target);
                                total1 += precio*Double.parseDouble(objetivo.get(i).getCantidad());
                            }else if(suplidor.equals("2002")){
                                sup2.add(target);
                                total2 += precio*Double.parseDouble(objetivo.get(i).getCantidad());
                            }else if(suplidor.equals("2003")){
                                sup3.add(target);
                                total3 += precio*Double.parseDouble(objetivo.get(i).getCantidad());
                            }

                        }

                    }

                }

            }

        }


       if(sup1.size() > 0){
           for(Target t : sup1){
               Articulos articulos = new Articulos(t.getCodArt(),t.getCantOrdenada(),t.getPrecio(),t.getCantidadTotal(),t.getBestSupli());
               estos1.add(articulos);
           }
       }

        if(sup2.size()>0){
            for (Target ta : sup2){
                Articulos arti = new Articulos(ta.getCodArt(), ta.getCantOrdenada(), ta.getPrecio(), ta.getCantidadTotal(), ta.getBestSupli());
                estos2.add(arti);
            }
        }

        if(sup3.size()>0){
            for (Target tar : sup3){
                Articulos artic = new Articulos(tar.getCodArt(),tar.getCantOrdenada(),tar.getPrecio(), tar.getCantidadTotal(), tar.getBestSupli());
                estos3.add(artic);
            }
        }



        MongoCollection<Document> movimientosArticulos = database.getCollection("MovimientoInventario");

        AggregateIterable<Document> movimient = movimientosArticulos.aggregate(Arrays.asList(
                new Document("$match",new Document("tipoMovimiento","Salida")),
                new Document("$group",new Document("_id",new Document("codigoArticulo","$codigoArticulo")
                ).append("count",new Document("$sum",1)).append("cantidad",new Document("$sum","$cantidad")))

        ));

        Date fech1;
        Date fech2;
        Date fech3;

        int trap = 0;

        int fin1;
        int fin2;
        int fin3;

        Double menor1 = -1.0;
        Double menor2 = -1.0;
        Double menor3 = -1.0;

        for(Document as : movimient){
            Document artis = (Document) as.get("_id");
            System.out.println("Articulo: " + artis.get("codigoArticulo"));
            System.out.println("Cantidad total: " + as.get("cantidad"));
            System.out.println("Total de salidas: " + as.get("count"));
            trap++;

            if(estos1.size() > 0){
                for(int uno = 0; uno < estos1.size(); uno++){
                    if(artis.get("codigoArticulo").equals(estos1.get(uno).getCodigoArticulo())){
                        Double consumo = Math.floor((Double) as.get("cantidad")/(Double) as.get("count"));
                        System.out.println("\nconsumo para "+ artis.get("codigoArticulo")+": " + String.valueOf(consumo)+"\n");

                        Double diasFaltantes = Math.floor(estos1.get(uno).getCantExist()/consumo);

                        if(uno == 0){
                            menor1 = diasFaltantes;
                        }else {
                            if(menor1 > diasFaltantes){
                                menor1 = diasFaltantes;
                            }
                        }

                        if((menor1 - estos1.get(uno).getDiasSupli()) > 0){
                            //TODO: A partir de aqui sacar fecha que debe de pedir, entrarla a la orden, guardarla y presentarla. Repetir para las demas listas de articulos
                        }
                    }
                }



            }



        }
        System.out.println("\nCantidad total de todo: " + String.valueOf(trap));






        orden1 = new Orden("2001",total1,estos1);

        orden2 = new Orden("2002",total2,estos2);

        orden3 = new Orden("2003",total3,estos3);



        Document ordenCompra = new Document();
        Document articuloOrden = new Document();

        Document ordenesMongo = database.getCollection("OrdenCompra").find().sort(new BasicDBObject("codigoOrdenCompra",-1)).first();

        System.out.println(orden1.getArticulos().get(orden1.getArticulos().size()-1));

//        if(ordenesMongo == null){
//            ordenCompra.append("codigoOrdenCompra",1);
//        }else{
//            System.out.println(ordenesMongo);
//        }
//
//        if(orden1.getMontoTotal()>0){
//            ordenCompra.append("codigoSuplidor","2001").append("fechaOrden",orden1.getArticulos().get());
//        }
    }

}
