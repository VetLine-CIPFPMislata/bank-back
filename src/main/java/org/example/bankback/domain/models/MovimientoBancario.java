package org.example.bankback.domain.models;

import java.math.BigDecimal;
import java.util.Date;

public class MovimientoBancario {
    private final Long id;
    private final TipoMovimientoBancario tipoMovimientoBancario;
    private final OrigenMovimientoBancario origenMovimientoBancario;
    private final TarjetaCredito tarjetaCreditoOrigen;
    private final Date fechaMovimiento;
    private final BigDecimal importe;
    private final String concepto;

    public MovimientoBancario(Long id, TipoMovimientoBancario tipoMovimientoBancario, OrigenMovimientoBancario origenMovimientoBancario, TarjetaCredito tarjetaCreditoOrigen, Date fechaMovimiento, BigDecimal importe, String concepto) {
        this.id = id;
        this.tipoMovimientoBancario = tipoMovimientoBancario;
        this.origenMovimientoBancario = origenMovimientoBancario;
        this.tarjetaCreditoOrigen = tarjetaCreditoOrigen;
        this.fechaMovimiento = fechaMovimiento;
        this.importe = importe;
        this.concepto = concepto;
    }

    public Long getId() {
        return id;
    }

    public TipoMovimientoBancario getTipoMovimientoBancario() {
        return tipoMovimientoBancario;
    }

    public OrigenMovimientoBancario getOrigenMovimientoBancario() {
        return origenMovimientoBancario;
    }

    public TarjetaCredito getTarjetaCreditoOrigen() {
        return tarjetaCreditoOrigen;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public String getConcepto() {
        return concepto;
    }
}
