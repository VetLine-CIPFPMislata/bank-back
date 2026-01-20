package org.example.bankback.controller;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
import org.example.bankback.domain.exception.ValidationException;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BankController {

    private final PagoTarjetaService pagoTarjetaService;
    private final PagoTarjetaMapper mapper;
    private final BankAccountService bankAccountService;
    private final CreditCardService creditCardService;
    private final BankMovementService bankMovementService;
    private final BankApiTokenService bankApiTokenService;
    private final AuthService authService;

    public BankController(PagoTarjetaService pagoTarjetaService,
                         PagoTarjetaMapper mapper,
                         BankAccountService bankAccountService,
                         CreditCardService creditCardService,
                         BankMovementService bankMovementService,
                         BankApiTokenService bankApiTokenService,
                         AuthService authService) {
        this.pagoTarjetaService = pagoTarjetaService;
        this.mapper = mapper;
        this.bankAccountService = bankAccountService;
        this.creditCardService = creditCardService;
        this.bankMovementService = bankMovementService;
        this.bankApiTokenService = bankApiTokenService;
        this.authService = authService;
    }

    @PostMapping("/pago_tarjeta")
    public ResponseEntity<?> pagarConTarjeta(@RequestBody PagoTarjetaRequest request) {
        try {
            if (!bankApiTokenService.validateApiToken(request.autorizacion().api_token())) {
                throw new ValidationException("API Token inválido. No autorizado para procesar pagos");
            }

            PagoTarjetaDTO dto = mapper.toDTO(request);
            PagoTarjetaResponseDTO responseDTO = pagoTarjetaService.procesarPago(dto);
            PagoTarjetaResponse response = mapper.toResponse(responseDTO);

            return ResponseEntity.ok(response);
        } catch (ValidationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + ex.getMessage());
        }
    }

    @GetMapping("/clientes/{clientId}/cuentas")
    public ResponseEntity<List<BankAccount>> getCuentasByCliente(@PathVariable Long clientId,
                                                                 @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        if (token == null) return ResponseEntity.status(401).build();

        Optional<Client> userOpt = authService.getUserFromToken(token);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).build();

        if (!userOpt.get().getId().equals(clientId)) return ResponseEntity.status(403).build();

        List<BankAccount> cuentas = bankAccountService.findByClientId(clientId);
        return ResponseEntity.ok(cuentas);
    }


    @GetMapping("/cuentas/{accountId}/tarjetas")
    public ResponseEntity<List<CreditCard>> getTarjetasByCuenta(@PathVariable Long accountId,
                                                                 @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractTokenFromHeader(authHeader);
        if (token == null) return ResponseEntity.status(401).build();

        Optional<Client> userOpt = authService.getUserFromToken(token);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).build();

        Optional<BankAccount> cuentaOpt = bankAccountService.findById(accountId);
        if (cuentaOpt.isEmpty()) return ResponseEntity.status(404).build();

        if (!cuentaOpt.get().getIdCliente().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }

        List<CreditCard> tarjetas = creditCardService.findByBankAccountId(accountId);
        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/cuentas/{accountId}/movimientos")
    public ResponseEntity<List<BankMovement>> getMovimientosByCuenta(@PathVariable Long accountId,
                                                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) return ResponseEntity.status(401).build();

        Optional<Client> userOpt = authService.getUserFromToken(token);
        if (userOpt.isEmpty()) return ResponseEntity.status(401).build();

        Optional<BankAccount> cuentaOpt = bankAccountService.findById(accountId);
        if (cuentaOpt.isEmpty()) return ResponseEntity.status(404).build();

        if (!cuentaOpt.get().getIdCliente().equals(userOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }

        List<BankMovement> movimientos = bankMovementService.findAllByBankAccountId(accountId);

        return ResponseEntity.ok(movimientos);
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
