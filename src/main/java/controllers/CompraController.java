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
    ObservableList<VerOrden> data1 = FXCollections.observableArrayList();

    List<TablaCompra> objetivo = new ArrayList<>();
    List<modelo.Articulo> todosArticulos = new ArrayList<>();

    Date fech1 = null;
    Date fech2 = null;
    Date fech3 = null;

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
    TableColumn <TablaCompra, String> clArt,clCantidad,clFec,clSuplir,clFec2;

    @FXML
    TableView tCompra,tCompra2;

    @FXML
    public void initialize(){
        ObservableList<String> opciones = FXCollections.observableArrayList();

//        clCod.setCellValueFactory( new PropertyValueFactory<>("codigo"));
        clArt.setCellValueFactory( new PropertyValueFactory<>("articulo"));
        clCantidad.setCellValueFactory( new PropertyValueFactory<>("cantidad"));
        clFec.setCellValueFactory( new PropertyValueFactory<>("fecDeseada"));
        clSuplir.setCellValueFactory( new PropertyValueFactory<>("supli"));
        clFec2.setCellValueFactory( new PropertyValueFactory<>("fecha"));

        Double totalCantidad = 0.0;

        for(Document d : Articulo){
            List<Document> cantidad = d.get("almacen",List.class);
            for(Document c : cantidad){
                totalCantidad += c.getDouble("balanceActual");
            }

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
                        System.out.println("No es necesario, ya existe esa cantidad para el Articulo: " + todosArticulos.get(k).getDescripcion());
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



        int trap = 0;


        Double menor1 = -1.0;
        Double menor2 = -1.0;
        Double menor3 = -1.0;

        for(Document as : movimient){
            Document artis = (Document) as.get("_id");
//            System.out.println("Articulo: " + artis.get("codigoArticulo"));
//            System.out.println("Cantidad total: " + as.get("cantidad"));
//            System.out.println("Total de salidas: " + as.get("count"));
            trap++;

            if(estos1.size() > 0){
                if(orden1 != null && orden1.getFecPedir() != null && fech1 == null)
                    fech1 = orden1.getFecPedir();

                orden1 = crearOrden("2001",total1,estos1,as,artis,menor1,fech1);
            }

            if(estos2.size()>0){
                if(orden2 != null && orden2.getFecPedir() != null && fech2 == null)
                    fech2 = orden2.getFecPedir();

                orden2 = crearOrden("2002",total2,estos2,as,artis,menor2,fech2);
            }

            if(estos3.size()>0){
                if(orden3 != null && orden3.getFecPedir() != null && fech3 == null)
                    fech3 = orden3.getFecPedir();

                orden3 = crearOrden("2003",total3,estos3,as,artis,menor3,fech3);
            }

        }
        System.out.println("Cantidad total de articulos procesados: " + String.valueOf(trap));

        Document ordenCompra = new Document();

        List<Document> artiss = new ArrayList<>();
        Map<String,Object> neet = new HashMap<>();

        Document ordenesMongo = database.getCollection("OrdenCompra").find().sort(new BasicDBObject("codigoOrdenCompra",-1)).first();

        MongoCollection<Document> OrdenCompraMongo = database.getCollection("OrdenCompra");

        VerOrden ver = null;

        if(orden1 != null){
            if(ordenesMongo == null){
                ordenCompra.append("codigoOrdenCompra",1);
            }else{
                System.out.println("Orden1 de compra generada #"+ordenesMongo.get("codigoOrdenCompra"));
                ordenCompra.append("codigoOrdenCompra",(int)ordenesMongo.get("codigoOrdenCompra")+1);
            }

            for(Articulos arts : orden1.getArticulos()){
                neet.put("codigoArticulo",arts.getCodigoArticulo());
                neet.put("cantidadOrdenada",arts.getCantidadOrdenada());
                neet.put("precioCompra",arts.getPrecioCompra());
                Document nuevo = new Document(neet);
                artiss.add(nuevo);
            }

            ordenCompra.append("codigoSuplidor","2001")
                    .append("fechaOrden",orden1.getFecPedir())
                    .append("montoTotal",orden1.getMontoTotal())
                    .append("articulos",artiss);



            OrdenCompraMongo.insertOne(ordenCompra);

            ver = new VerOrden(orden1.getCodigoSuplidor(),orden1.getFecPedir());
        }

        ordenCompra = new Document();

        artiss = new ArrayList<>();
        neet = new HashMap<>();

        ordenesMongo = database.getCollection("OrdenCompra").find().sort(new BasicDBObject("codigoOrdenCompra",-1)).first();

        OrdenCompraMongo = database.getCollection("OrdenCompra");

        VerOrden ver2 = null;

        if(orden2 != null){
            if(ordenesMongo == null){
                ordenCompra.append("codigoOrdenCompra",1);
            }else{
                System.out.println("Orden2 de compra generada #"+ordenesMongo.get("codigoOrdenCompra"));
                ordenCompra.append("codigoOrdenCompra",(int)ordenesMongo.get("codigoOrdenCompra")+1);
            }

            for(Articulos arts : orden2.getArticulos()){
                neet.put("codigoArticulo",arts.getCodigoArticulo());
                neet.put("cantidadOrdenada",arts.getCantidadOrdenada());
                neet.put("precioCompra",arts.getPrecioCompra());
                Document nuevo = new Document(neet);
                artiss.add(nuevo);
            }

            ordenCompra.append("codigoSuplidor","2002")
                    .append("fechaOrden",orden1.getFecPedir())
                    .append("montoTotal",orden1.getMontoTotal())
                    .append("articulos",artiss);



            OrdenCompraMongo.insertOne(ordenCompra);

            ver2 = new VerOrden(orden2.getCodigoSuplidor(),orden2.getFecPedir());
        }

        ordenCompra = new Document();

        artiss = new ArrayList<>();
        neet = new HashMap<>();

        ordenesMongo = database.getCollection("OrdenCompra").find().sort(new BasicDBObject("codigoOrdenCompra",-1)).first();

        OrdenCompraMongo = database.getCollection("OrdenCompra");

        VerOrden ver3 = null;

        if(orden3 != null){
            if(ordenesMongo == null){
                ordenCompra.append("codigoOrdenCompra",1);
            }else{
                System.out.println("Orden3 de compra generada #"+ordenesMongo.get("codigoOrdenCompra"));
                ordenCompra.append("codigoOrdenCompra",(int)ordenesMongo.get("codigoOrdenCompra")+1);
            }

            for(Articulos arts : orden3.getArticulos()){
                neet.put("codigoArticulo",arts.getCodigoArticulo());
                neet.put("cantidadOrdenada",arts.getCantidadOrdenada());
                neet.put("precioCompra",arts.getPrecioCompra());
                Document nuevo = new Document(neet);
                artiss.add(nuevo);
            }

            ordenCompra.append("codigoSuplidor","2003")
                    .append("fechaOrden",orden1.getFecPedir())
                    .append("montoTotal",orden1.getMontoTotal())
                    .append("articulos",artiss);



            OrdenCompraMongo.insertOne(ordenCompra);
            ver3 = new VerOrden(orden3.getCodigoSuplidor(),orden3.getFecPedir());
        }

        if(ver != null){
            data1.add(ver);
        }
        if(ver2 != null){
            data1.add(ver2);
        }
        if (ver3 != null){
            data1.add(ver3);
        }

        if(data1 != null){
            tCompra2.setItems(data1);
        }

    }


    public Orden crearOrden(String suplidor,Double total,List<Articulos> estos1,Document as, Document artis, Double menor, Date fech){

        for(int uno = 0; uno < estos1.size(); uno++){
            if(artis.get("codigoArticulo").equals(estos1.get(uno).getCodigoArticulo())){
                Double consumo = Math.floor((Double) as.get("cantidad")/Double.parseDouble(as.get("count").toString()));
                System.out.println("Consumo para articulo: "+ artis.get("codigoArticulo")+": " + String.valueOf(consumo)+"\n");

                Double diasFaltantes = Math.floor(estos1.get(uno).getCantExist()/consumo);

                if(uno == 0){
                    menor = diasFaltantes;
                }else {
                    if(menor > diasFaltantes){
                        menor = diasFaltantes;
                    }
                }

                if((menor - estos1.get(uno).getDiasSupli()) > 0){
                    Double diaPorPedir = menor - estos1.get(uno).getDiasSupli();
                    System.out.println("Dias par fecha deseada: " + diaPorPedir);

                    Date dt = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dt);
                    c.add(Calendar.DATE,diaPorPedir.intValue());
                    dt = c.getTime();

                    fech = dt;

                    System.out.println("Fecha a pedir: " + dt + "\n");
                }else {
                    fech = new Date();
                    System.out.println("Pedir hoy!!!" + "\n");
                }
            }
        }


        return new Orden(suplidor,total,estos1,fech);
    }


}
