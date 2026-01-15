package org.example.bankback.domain;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.repository.BankAccountRepository;
import org.example.bankback.domain.service.impl.BankAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new BankAccount(1L, "ES6112343456420456325555", new BigDecimal("1000.00"));
        testAccount.setIdCliente(1L);
    }

    @Test
    void testFindById_ConIdExistente_DebeRetornarCuenta() {
        
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        
        Optional<BankAccount> result = bankAccountService.findById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("ES6112343456420456325555", result.get().getIban());
        verify(bankAccountRepository).findById(1L);
    }

    @Test
    void testFindById_ConIdNoExistente_DebeRetornarVacio() {
        
        when(bankAccountRepository.findById(999L)).thenReturn(Optional.empty());

        
        Optional<BankAccount> result = bankAccountService.findById(999L);

        
        assertFalse(result.isPresent());
        verify(bankAccountRepository).findById(999L);
    }

    @Test
    void testFindByIBAN_ConIbanExistente_DebeRetornarCuenta() {
        
        String iban = "ES6112343456420456325555";
        when(bankAccountRepository.findByIBAN(iban)).thenReturn(Optional.of(testAccount));

        
        Optional<BankAccount> result = bankAccountService.findByIBAN(iban);

        
        assertTrue(result.isPresent());
        assertEquals(iban, result.get().getIban());
        verify(bankAccountRepository).findByIBAN(iban);
    }

    @Test
    void testFindByIBAN_ConIbanNoExistente_DebeRetornarVacio() {
        
        String iban = "ES9999999999999999999999";
        when(bankAccountRepository.findByIBAN(iban)).thenReturn(Optional.empty());

        
        Optional<BankAccount> result = bankAccountService.findByIBAN(iban);

        
        assertFalse(result.isPresent());
        verify(bankAccountRepository).findByIBAN(iban);
    }

    @Test
    void testSave_DebeGuardarYRetornarCuenta() {
        
        BankAccount newAccount = new BankAccount(null, "ES1234567890123456789012", new BigDecimal("500.00"));
        newAccount.setIdCliente(2L);
        BankAccount savedAccount = new BankAccount(2L, "ES1234567890123456789012", new BigDecimal("500.00"));
        savedAccount.setIdCliente(2L);

        when(bankAccountRepository.save(newAccount)).thenReturn(savedAccount);

        
        BankAccount result = bankAccountService.save(newAccount);

        
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("ES1234567890123456789012", result.getIban());
        assertEquals(new BigDecimal("500.00"), result.getSaldo());
        verify(bankAccountRepository).save(newAccount);
    }

    @Test
    void testSave_ActualizarSaldo_DebeGuardarCambios() {
        
        testAccount.setSaldo(new BigDecimal("1500.00"));
        when(bankAccountRepository.save(testAccount)).thenReturn(testAccount);

        
        BankAccount result = bankAccountService.save(testAccount);

        
        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), result.getSaldo());
        verify(bankAccountRepository).save(testAccount);
    }

    @Test
    void testFindByClientId_ConClienteConCuentas_DebeRetornarLista() {
        
        Long clientId = 1L;
        BankAccount account1 = new BankAccount(1L, "ES6112343456420456325555", new BigDecimal("1000.00"));
        account1.setIdCliente(clientId);
        BankAccount account2 = new BankAccount(2L, "ES6112343456420456325556", new BigDecimal("2000.00"));
        account2.setIdCliente(clientId);

        List<BankAccount> accounts = Arrays.asList(account1, account2);
        when(bankAccountRepository.findByClientId(clientId)).thenReturn(accounts);

        
        List<BankAccount> result = bankAccountService.findByClientId(clientId);

        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getIdCliente().equals(clientId)));
        verify(bankAccountRepository).findByClientId(clientId);
    }

    @Test
    void testFindByClientId_ConClienteSinCuentas_DebeRetornarListaVacia() {
        
        Long clientId = 999L;
        when(bankAccountRepository.findByClientId(clientId)).thenReturn(List.of());

        
        List<BankAccount> result = bankAccountService.findByClientId(clientId);

        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bankAccountRepository).findByClientId(clientId);
    }
}


