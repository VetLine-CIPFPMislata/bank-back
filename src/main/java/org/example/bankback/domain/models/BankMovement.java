package org.example.bankback.domain.models;

import java.math.BigDecimal;
import java.util.Date;

public class BankMovement {
    Long id;
    TypeBankMovement tipoMovimientoBancario;
    OriginBankMovement origenMovimientoBancario;
    CreditCard tarjetaCreditoOrigen;
    BankAccount cuentaBancaria;
    Date fechaMovimiento;
    BigDecimal importe;
    String concepto;

    public BankMovement(Long id, TypeBankMovement tipoMovimientoBancario, OriginBankMovement origenMovimientoBancario, CreditCard tarjetaCreditoOrigen, BankAccount cuentaBancaria, Date fechaMovimiento, BigDecimal importe, String concepto) {
        this.id = id;
        this.tipoMovimientoBancario = tipoMovimientoBancario;
        this.origenMovimientoBancario = origenMovimientoBancario;
        this.tarjetaCreditoOrigen = tarjetaCreditoOrigen;
        this.cuentaBancaria = cuentaBancaria;
        this.fechaMovimiento = fechaMovimiento;
        this.importe = importe;
        this.concepto = concepto;
    }

    public TypeBankMovement getTipoMovimientoBancario() {
        return tipoMovimientoBancario;
    }

    public void setTipoMovimientoBancario(TypeBankMovement tipoMovimientoBancario) {
        this.tipoMovimientoBancario = tipoMovimientoBancario;
    }

    public OriginBankMovement getOrigenMovimientoBancario() {
        return origenMovimientoBancario;
    }

    public void setOrigenMovimientoBancario(OriginBankMovement origenMovimientoBancario) {
        this.origenMovimientoBancario = origenMovimientoBancario;
    }
    public Long getId() {
        return id;
    }

    public CreditCard getTarjetaCreditoOrigen() {
        return tarjetaCreditoOrigen;
    }

    public void setTarjetaCreditoOrigen(CreditCard tarjetaCreditoOrigen) {
        this.tarjetaCreditoOrigen = tarjetaCreditoOrigen;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BankAccount getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(BankAccount cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }
}
