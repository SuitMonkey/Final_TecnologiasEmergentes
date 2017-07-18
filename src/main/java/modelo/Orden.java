package modelo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Francis Cáceres on 15/7/2017.
 */
@Data
public class Orden {
    String codigoSuplidor;
    Double montoTotal;
    Date fecPedir;
    List<Articulos> articulos;

    public Orden(String codigoSuplidor, Double montoTotal, List<Articulos> articulos, Date fecPedir) {
        this.codigoSuplidor = codigoSuplidor;
        this.montoTotal = montoTotal;
        this.articulos = articulos;
        this.fecPedir = fecPedir;
    }

    public Orden() {
    }
}
