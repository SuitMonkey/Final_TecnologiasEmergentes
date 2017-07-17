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
    Date fecDesea;
    Double precioCompra;

    public Articulos(Double codigoArticulo, Double cantidadOrdenada,Date fecDesea, Double precioCompra) {
        this.codigoArticulo = codigoArticulo;
        this.cantidadOrdenada = cantidadOrdenada;
        this.fecDesea = fecDesea;
        this.precioCompra = precioCompra;
    }

    public Articulos() {
    }
}
