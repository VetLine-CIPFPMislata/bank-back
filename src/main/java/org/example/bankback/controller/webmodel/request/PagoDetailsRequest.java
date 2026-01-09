package org.example.bankback.controller.webmodel.request;

import java.math.BigDecimal;

public record PagoDetailsRequest(BigDecimal importe, String concepto) {}

