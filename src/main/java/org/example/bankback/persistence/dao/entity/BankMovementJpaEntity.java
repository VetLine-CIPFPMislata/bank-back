package org.example.bankback.persistence.dao.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.models.OriginBankMovement;

@Entity
@Table(name = "bank_movements")
public class BankMovementJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bank_movement")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private TypeBankMovement tipoMovimientoBancario;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin_movement", nullable = false)
    private OriginBankMovement origenMovimientoBancario;

    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCardJpaEntity tarjetaCreditoOrigen;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccountJpaEntity cuentaBancaria;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "movement_date", nullable = false)
    private Date fechaMovimiento;

    @Column(name = "amount", nullable = false)
    private BigDecimal importe;

    @Column(name = "concept", nullable = false)
    private String concepto;

    public BankMovementJpaEntity() {
    }

    public BankMovementJpaEntity(Long id, TypeBankMovement tipoMovimientoBancario, OriginBankMovement origenMovimientoBancario, CreditCardJpaEntity tarjetaCreditoOrigen, BankAccountJpaEntity cuentaBancaria, Date fechaMovimiento, BigDecimal importe, String concepto) {
        this.id = id;
        this.tipoMovimientoBancario = tipoMovimientoBancario;
        this.origenMovimientoBancario = origenMovimientoBancario;
        this.tarjetaCreditoOrigen = tarjetaCreditoOrigen;
        this.cuentaBancaria = cuentaBancaria;
        this.fechaMovimiento = fechaMovimiento;
        this.importe = importe;
        this.concepto = concepto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CreditCardJpaEntity getTarjetaCreditoOrigen() {
        return tarjetaCreditoOrigen;
    }

    public void setTarjetaCreditoOrigen(CreditCardJpaEntity tarjetaCreditoOrigen) {
        this.tarjetaCreditoOrigen = tarjetaCreditoOrigen;
    }

    public BankAccountJpaEntity getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(BankAccountJpaEntity cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
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
}
