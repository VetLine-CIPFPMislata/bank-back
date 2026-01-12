package org.example.bankback.controller.mapper;

import org.example.bankback.controller.webmodel.response.LoginResponse;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.service.LoginService;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public LoginResponse toSuccessResponse(LoginService.LoginResult loginResult) {
        Client client = loginResult.getClient();

        LoginResponse.ClientData clientData = new LoginResponse.ClientData(
            client.getId(),
            client.getUsername(),
            client.getNombre(),
            client.getApellido1(),
            client.getApellido2(),
            client.getDni()
        );

        return new LoginResponse(
            true,
            "Login exitoso",
            clientData
        );
    }

    public LoginResponse toSuccessResponseBasic(Client client) {
        LoginResponse.ClientData clientData = new LoginResponse.ClientData(
            client.getId(),
            client.getUsername(),
            client.getNombre(),
            client.getApellido1(),
            client.getApellido2(),
            client.getDni()
        );

        return new LoginResponse(
            true,
            "Login exitoso",
            clientData
        );
    }

    public LoginResponse toErrorResponse(String mensaje) {
        return new LoginResponse(
            false,
            mensaje,
            null
        );
    }
}
