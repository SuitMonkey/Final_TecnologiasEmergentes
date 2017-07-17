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

    public Target(Double codArt,String nomArt, Double cantOrdenada, Double precio, Date fecOrden) {
        this.codArt = codArt;
        this.nomArt = nomArt;
        this.cantOrdenada = cantOrdenada;
        this.precio = precio;
        this.fecOrden = fecOrden;
    }

    public Target() {
    }
}
