package modelo;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by Francis Cáceres on 13/7/2017.
 */
@Data
public class TablaCompra {

    String articulo;
    String cantidad;
    String fecDeseada;


    public TablaCompra() {
    }
}
