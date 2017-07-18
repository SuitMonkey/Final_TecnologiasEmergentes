package modelo;

import lombok.Data;

import java.util.Date;

/**
 * Created by Francis Cáceres on 15/7/2017.
 */
@Data
public class Target {
    Double codArt;
    String nomArt;
    Double cantOrdenada;
    Double precio;
    Date fecOrden;
    Double cantidadTotal;
    Double bestSupli;

    public Target(Double codArt,String nomArt, Double cantOrdenada, Double precio, Date fecOrden, Double cantidadTotal,Double bestSupli) {
        this.codArt = codArt;
        this.nomArt = nomArt;
        this.cantOrdenada = cantOrdenada;
        this.precio = precio;
        this.fecOrden = fecOrden;
        this.cantidadTotal = cantidadTotal;
        this.bestSupli = bestSupli;
    }

    public Target() {
    }
}
