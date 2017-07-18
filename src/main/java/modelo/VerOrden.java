package modelo;

import lombok.Data;

import java.util.Date;

/**
 * Created by Francis Cáceres on 18/7/2017.
 */
@Data
public class VerOrden {
    String Supli;
    Date fecha;

    public VerOrden(String supli, Date fecha) {
        Supli = supli;
        this.fecha = fecha;
    }

    public VerOrden() {
    }
}
