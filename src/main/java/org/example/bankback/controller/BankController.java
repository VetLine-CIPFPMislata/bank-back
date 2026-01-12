package org.example.bankback.controller;

import org.example.bankback.controller.mapper.LoginMapper;
import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.controller.webmodel.request.LoginRequest;
import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.LoginResponse;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BankController {

    private final PagoTarjetaService pagoTarjetaService;
    private final PagoTarjetaMapper mapper;
    private final ClientService clientService;
    private final BankAccountService bankAccountService;
    private final CreditCardService creditCardService;
    private final BankMovementService bankMovementService;
    private final LoginMapper loginMapper;

    public BankController(PagoTarjetaService pagoTarjetaService,
                         PagoTarjetaMapper mapper,
                         ClientService clientService,
                         BankAccountService bankAccountService,
                         CreditCardService creditCardService,
                         BankMovementService bankMovementService,
                         LoginMapper loginMapper) {
        this.pagoTarjetaService = pagoTarjetaService;
        this.mapper = mapper;
        this.clientService = clientService;
        this.bankAccountService = bankAccountService;
        this.creditCardService = creditCardService;
        this.bankMovementService = bankMovementService;
        this.loginMapper = loginMapper;
    }

    @PostMapping("/pago_tarjeta")
    public ResponseEntity<PagoTarjetaResponse> pagarConTarjeta(@RequestBody PagoTarjetaRequest request) {
        PagoTarjetaDTO dto = mapper.toDTO(request);
        PagoTarjetaResponseDTO responseDTO = pagoTarjetaService.procesarPago(dto);
        PagoTarjetaResponse response = mapper.toResponse(responseDTO);

        if (!response.exito()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<Client> clientOpt = clientService.findByUsername(request.username());

        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(401).body(loginMapper.toErrorResponse("Credenciales inválidas"));
        }

        Client client = clientOpt.get();

        // Validar password y api_token
        if (!client.getPassword().equals(request.password()) ||
            !client.getApi_token().equals(request.api_token())) {
            return ResponseEntity.status(401).body(loginMapper.toErrorResponse("Credenciales inválidas"));
        }

        // Login exitoso - devolver solo datos básicos del cliente
        LoginResponse successResponse = loginMapper.toSuccessResponseBasic(client);
        return ResponseEntity.ok(successResponse);
    }

    // Endpoint para obtener las cuentas del cliente autenticado
    @GetMapping("/clientes/{clientId}/cuentas")
    public ResponseEntity<List<BankAccount>> getCuentasByCliente(@PathVariable Long clientId) {
        List<BankAccount> cuentas = bankAccountService.findByClientId(clientId);
        return ResponseEntity.ok(cuentas);
    }

    // Endpoint para obtener las tarjetas del cliente autenticado
    @GetMapping("/clientes/{clientId}/tarjetas")
    public ResponseEntity<List<CreditCard>> getTarjetasByCliente(@PathVariable Long clientId) {
        // Primero obtener las cuentas del cliente
        List<BankAccount> cuentas = bankAccountService.findByClientId(clientId);

        // Luego obtener todas las tarjetas de esas cuentas
        List<CreditCard> tarjetas = cuentas.stream()
            .flatMap(cuenta -> creditCardService.findByBankAccountId(cuenta.getId()).stream())
            .toList();

        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/clientes/{clientId}/movimientos")
    public ResponseEntity<List<BankMovement>> getMovimientosByCliente(@PathVariable Long clientId) {
        List<BankAccount> cuentas = bankAccountService.findByClientId(clientId);


        List<CreditCard> tarjetas = cuentas.stream()
            .flatMap(cuenta -> creditCardService.findByBankAccountId(cuenta.getId()).stream())
            .toList();

        List<BankMovement> movimientos = tarjetas.stream()
            .flatMap(tarjeta -> bankMovementService.findAllByCreditCardId(tarjeta.getId()).stream())
            .toList();

        return ResponseEntity.ok(movimientos);
    }


    @GetMapping("/cuentas/{cuentaId}")
    public ResponseEntity<BankAccount> getCuenta(@PathVariable Long cuentaId) {
        Optional<BankAccount> cuenta = bankAccountService.findById(cuentaId);
        return cuenta.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/tarjetas/{tarjetaId}/movimientos")
    public ResponseEntity<List<BankMovement>> getMovimientosByTarjeta(@PathVariable Long tarjetaId) {
        List<BankMovement> movimientos = bankMovementService.findAllByCreditCardId(tarjetaId);
        return ResponseEntity.ok(movimientos);
    }
}
