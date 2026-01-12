package org.example.bankback.controller.mapper;

import org.example.bankback.controller.webmodel.response.LoginResponse;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.service.LoginService;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public LoginResponse toSuccessResponse(LoginService.LoginResult loginResult) {
        Client client = loginResult.getClient();

        return new LoginResponse(
            null,
            client.getUsername(),
            client.getNombre() + " " + client.getApellido1()
        );
    }

    public LoginResponse toSuccessResponseBasic(Client client) {
        return new LoginResponse(
            null,
            client.getUsername(),
            client.getNombre() + " " + client.getApellido1()
        );
    }

    public LoginResponse toErrorResponse(String mensaje) {
        return new LoginResponse(
            null,
            null,
            mensaje
        );
    }
}
