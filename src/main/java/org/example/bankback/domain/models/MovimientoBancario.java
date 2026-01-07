package org.example.bankback.domain.models;

import java.math.BigDecimal;
import java.util.Date;

public class MovimientoBancario {
    Long id;
    TipoMovimientoBancario tipoMovimientoBancario;
    OrigenMovimientoBancario origenMovimientoBancario;
    TarjetaCredito tarjetaCreditoOrigen;
    Date fechaMovimiento;
    BigDecimal importe;
    String concepto;
}
