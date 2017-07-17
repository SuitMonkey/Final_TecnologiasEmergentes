package modelo;

import lombok.Data;

import java.util.List;

/**
 * Created by Francis Cáceres on 15/7/2017.
 */
@Data
public class Articulo {
    Double codigoArticulo;
    String descripcion;
    Double cantidadTotal;


    public Articulo(Double codigoArticulo, String descripcion, Double cantidadTotal) {
        this.codigoArticulo = codigoArticulo;
        this.descripcion = descripcion;
        this.cantidadTotal = cantidadTotal;
    }
}
