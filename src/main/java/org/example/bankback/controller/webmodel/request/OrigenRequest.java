package org.example.bankback.controller.webmodel.request;

public record OrigenRequest(String numeroTarjeta, String fechaCaducidad, String cvc, String nombreCompleto) {}

