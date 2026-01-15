package org.example.bankback.domain;

import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.repository.CreditCardRepository;
import org.example.bankback.domain.service.impl.CreditCardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {

    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    private CreditCard testCreditCard;

    @BeforeEach
    void setUp() {
        testCreditCard = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA");
        testCreditCard.setIdCuentaBancaria(1L);
    }

    @Test
    void testFindByCardNumber_ConNumeroExistente_DebeRetornarTarjeta() {
        
        String cardNumber = "4111111111111111";
        when(creditCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.of(testCreditCard));

        
        Optional<CreditCard> result = creditCardService.findByCardNumber(cardNumber);

        
        assertTrue(result.isPresent());
        assertEquals(cardNumber, result.get().getNumeroTarjeta());
        assertEquals("JUAN GARCIA GARCIA", result.get().getNombreCompleto());
        verify(creditCardRepository).findByCardNumber(cardNumber);
    }

    @Test
    void testFindByCardNumber_ConNumeroNoExistente_DebeRetornarVacio() {
        
        String cardNumber = "5555555555555555";
        when(creditCardRepository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

        
        Optional<CreditCard> result = creditCardService.findByCardNumber(cardNumber);

        
        assertFalse(result.isPresent());
        verify(creditCardRepository).findByCardNumber(cardNumber);
    }

    @Test
    void testFindByCardNumber_ConNumeroConEspacios_DebeRetornarTarjeta() {
        
        String cardNumberWithSpaces = "4111 1111 1111 1111";
        when(creditCardRepository.findByCardNumber(cardNumberWithSpaces)).thenReturn(Optional.of(testCreditCard));

        
        Optional<CreditCard> result = creditCardService.findByCardNumber(cardNumberWithSpaces);

        
        assertTrue(result.isPresent());
        verify(creditCardRepository).findByCardNumber(cardNumberWithSpaces);
    }

    @Test
    void testFindByName_ConNombreExistente_DebeRetornarTarjeta() {
        
        String name = "JUAN GARCIA GARCIA";
        when(creditCardRepository.findByName(name)).thenReturn(Optional.of(testCreditCard));

        
        Optional<CreditCard> result = creditCardService.findByName(name);

        
        assertTrue(result.isPresent());
        assertEquals(name, result.get().getNombreCompleto());
        verify(creditCardRepository).findByName(name);
    }

    @Test
    void testFindByName_ConNombreNoExistente_DebeRetornarVacio() {
        
        String name = "PEDRO LOPEZ";
        when(creditCardRepository.findByName(name)).thenReturn(Optional.empty());

        
        Optional<CreditCard> result = creditCardService.findByName(name);

        
        assertFalse(result.isPresent());
        verify(creditCardRepository).findByName(name);
    }

    @Test
    void testFindByBankAccountId_ConCuentaConTarjetas_DebeRetornarLista() {
        
        Long accountId = 1L;
        CreditCard card1 = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA");
        card1.setIdCuentaBancaria(accountId);
        CreditCard card2 = new CreditCard(2L, "5555555555555555", "2028-06", "456", "JUAN GARCIA");
        card2.setIdCuentaBancaria(accountId);

        List<CreditCard> cards = Arrays.asList(card1, card2);
        when(creditCardRepository.findByBankAccountId(accountId)).thenReturn(cards);

        
        List<CreditCard> result = creditCardService.findByBankAccountId(accountId);

        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getIdCuentaBancaria().equals(accountId)));
        verify(creditCardRepository).findByBankAccountId(accountId);
    }

    @Test
    void testFindByBankAccountId_ConCuentaSinTarjetas_DebeRetornarListaVacia() {
        
        Long accountId = 999L;
        when(creditCardRepository.findByBankAccountId(accountId)).thenReturn(List.of());

        
        List<CreditCard> result = creditCardService.findByBankAccountId(accountId);

        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(creditCardRepository).findByBankAccountId(accountId);
    }

    @Test
    void testFindByBankAccountId_VerificarDatosDeTarjetas() {
        
        Long accountId = 1L;
        CreditCard card1 = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA");
        card1.setIdCuentaBancaria(accountId);

        when(creditCardRepository.findByBankAccountId(accountId)).thenReturn(List.of(card1));

        
        List<CreditCard> result = creditCardService.findByBankAccountId(accountId);

        
        assertNotNull(result);
        assertEquals(1, result.size());
        CreditCard retrievedCard = result.getFirst();
        assertEquals("4111111111111111", retrievedCard.getNumeroTarjeta());
        assertEquals("2027-12", retrievedCard.getFechaCaducidad());
        assertEquals("123", retrievedCard.getCvc());
        assertEquals("JUAN GARCIA", retrievedCard.getNombreCompleto());
        verify(creditCardRepository).findByBankAccountId(accountId);
    }
}

