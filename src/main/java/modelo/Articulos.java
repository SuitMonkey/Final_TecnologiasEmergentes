package modelo;

import lombok.Data;

import java.util.Date;

/**
 * Created by Francis Cáceres on 15/7/2017.
 */
@Data
public class Articulos {
    Double codigoArticulo;
    Double cantidadOrdenada;
    Double precioCompra;
    Double cantExist;
    Double diasSupli;

    public Articulos(Double codigoArticulo, Double cantidadOrdenada, Double precioCompra, Double cantExist, Double diasSupli) {
        this.codigoArticulo = codigoArticulo;
        this.cantidadOrdenada = cantidadOrdenada;
        this.precioCompra = precioCompra;
        this.cantExist = cantExist;
        this.diasSupli = diasSupli;
    }

    public Articulos() {
    }
}
