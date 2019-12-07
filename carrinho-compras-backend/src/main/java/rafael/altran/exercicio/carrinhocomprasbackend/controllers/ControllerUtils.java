package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class ControllerUtils {

    private static final String ID_FORMAT = "MMddHHmmssSSS";

    private static final DateFormat ID_DATE_FORMAT = new SimpleDateFormat(ID_FORMAT);

    /**
     * Creates a new ID from the current {@link Date} with the format {@value #ID_FORMAT}.
     * The year is not used because the generated ID would be greater than Number.MAX_SAFE_INTEGER from JavaScript.
     *
     * @return new ID from the current {@link Date}
     */
    static Long createUniqueId() {
        // Avoid that 2 instances saved at same millisecond have the same ID
        return Long.parseLong(ID_DATE_FORMAT.format(new Date())) - ((long) (1000 * Math.random()));
    }

}
