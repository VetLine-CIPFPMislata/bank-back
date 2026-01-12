package org.example.bankback.controller.webmodel.response;




public record LoginResponse(
        String token,
        String username,
        String name
) {
}

