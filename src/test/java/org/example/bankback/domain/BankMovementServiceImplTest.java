package org.example.bankback.domain;

import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.OriginBankMovement;
import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.repository.BankMovementRepository;
import org.example.bankback.domain.service.impl.BankMovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankMovementServiceImplTest {

    @Mock
    private BankMovementRepository bankMovementRepository;

    @InjectMocks
    private BankMovementServiceImpl bankMovementService;

    private BankMovement testMovement;
    private CreditCard testCreditCard;

    @BeforeEach
    void setUp() {
        testCreditCard = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA");
        testCreditCard.setIdCuentaBancaria(1L);

        testMovement = new BankMovement(
                1L,
                TypeBankMovement.DEBE,
                OriginBankMovement.TARJETA,
                testCreditCard,
                new Date(),
                new BigDecimal("100.00"),
                "Compra en tienda"
        );
    }

    @Test
    void testFindById_ConIdExistente_DebeRetornarMovimiento() {
        
        when(bankMovementRepository.findById(1L)).thenReturn(Optional.of(testMovement));

        
        Optional<BankMovement> result = bankMovementService.findById(1L);

        
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(new BigDecimal("100.00"), result.get().getImporte());
        verify(bankMovementRepository).findById(1L);
    }

    @Test
    void testFindById_ConIdNoExistente_DebeRetornarVacio() {
        
        when(bankMovementRepository.findById(999L)).thenReturn(Optional.empty());

        
        Optional<BankMovement> result = bankMovementService.findById(999L);

        
        assertFalse(result.isPresent());
        verify(bankMovementRepository).findById(999L);
    }

    @Test
    void testFindByCreditCard_ConTarjetaExistente_DebeRetornarMovimiento() {
        
        when(bankMovementRepository.findByCreditCard(testCreditCard)).thenReturn(Optional.of(testMovement));

        
        Optional<BankMovement> result = bankMovementService.findByCreditCard(testCreditCard);

        
        assertTrue(result.isPresent());
        assertEquals(testCreditCard.getId(), result.get().getTarjetaCreditoOrigen().getId());
        verify(bankMovementRepository).findByCreditCard(testCreditCard);
    }

    @Test
    void testFindByCreditCard_ConTarjetaNoExistente_DebeRetornarVacio() {
        
        CreditCard otherCard = new CreditCard(999L, "5555555555555555", "2027-12", "456", "OTRO");
        when(bankMovementRepository.findByCreditCard(otherCard)).thenReturn(Optional.empty());

        
        Optional<BankMovement> result = bankMovementService.findByCreditCard(otherCard);

        
        assertFalse(result.isPresent());
        verify(bankMovementRepository).findByCreditCard(otherCard);
    }

    @Test
    void testFindByDate_ConFechaExistente_DebeRetornarMovimiento() {
        
        Date testDate = new Date();
        when(bankMovementRepository.findByDate(testDate)).thenReturn(Optional.of(testMovement));

        
        Optional<BankMovement> result = bankMovementService.findByDate(testDate);

        
        assertTrue(result.isPresent());
        verify(bankMovementRepository).findByDate(testDate);
    }

    @Test
    void testFindByDate_ConFechaNoExistente_DebeRetornarVacio() {
        
        Date testDate = new Date();
        when(bankMovementRepository.findByDate(testDate)).thenReturn(Optional.empty());

        
        Optional<BankMovement> result = bankMovementService.findByDate(testDate);

        
        assertFalse(result.isPresent());
        verify(bankMovementRepository).findByDate(testDate);
    }

    @Test
    void testSave_DebeGuardarYRetornarMovimiento() {
        
        BankMovement newMovement = new BankMovement(
                null,
                TypeBankMovement.DEBE,
                OriginBankMovement.TARJETA,
                testCreditCard,
                new Date(),
                new BigDecimal("250.00"),
                "Nueva compra"
        );

        BankMovement savedMovement = new BankMovement(
                2L,
                TypeBankMovement.DEBE,
                OriginBankMovement.TARJETA,
                testCreditCard,
                new Date(),
                new BigDecimal("250.00"),
                "Nueva compra"
        );

        when(bankMovementRepository.save(newMovement)).thenReturn(savedMovement);

        
        BankMovement result = bankMovementService.save(newMovement);

        
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(new BigDecimal("250.00"), result.getImporte());
        assertEquals("Nueva compra", result.getConcepto());
        verify(bankMovementRepository).save(newMovement);
    }

    @Test
    void testFindAllByCreditCardId_ConTarjetaConMovimientos_DebeRetornarLista() {
        
        Long creditCardId = 1L;
        BankMovement movement1 = new BankMovement(1L, TypeBankMovement.DEBE, OriginBankMovement.TARJETA,
                testCreditCard, new Date(), new BigDecimal("100.00"), "Compra 1");
        BankMovement movement2 = new BankMovement(2L, TypeBankMovement.DEBE, OriginBankMovement.TARJETA,
                testCreditCard, new Date(), new BigDecimal("200.00"), "Compra 2");

        List<BankMovement> movements = Arrays.asList(movement1, movement2);
        when(bankMovementRepository.findAllByCreditCardId(creditCardId)).thenReturn(movements);

        
        List<BankMovement> result = bankMovementService.findAllByCreditCardId(creditCardId);

        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bankMovementRepository).findAllByCreditCardId(creditCardId);
    }

    @Test
    void testFindAllByCreditCardId_ConTarjetaSinMovimientos_DebeRetornarListaVacia() {
        
        Long creditCardId = 999L;
        when(bankMovementRepository.findAllByCreditCardId(creditCardId)).thenReturn(List.of());

        
        List<BankMovement> result = bankMovementService.findAllByCreditCardId(creditCardId);

        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bankMovementRepository).findAllByCreditCardId(creditCardId);
    }

    @Test
    void testFindAllByBankAccountId_ConCuentaConMovimientos_DebeRetornarLista() {
        
        Long accountId = 1L;
        BankMovement movement1 = new BankMovement(1L, TypeBankMovement.DEBE, OriginBankMovement.TARJETA,
                testCreditCard, new Date(), new BigDecimal("100.00"), "Compra 1");
        BankMovement movement2 = new BankMovement(2L, TypeBankMovement.HABER, OriginBankMovement.TRANSFERENCIA,
                null, new Date(), new BigDecimal("500.00"), "Ingreso");

        List<BankMovement> movements = Arrays.asList(movement1, movement2);
        when(bankMovementRepository.findAllByBankAccountId(accountId)).thenReturn(movements);

        
        List<BankMovement> result = bankMovementService.findAllByBankAccountId(accountId);

        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bankMovementRepository).findAllByBankAccountId(accountId);
    }

    @Test
    void testFindAllByBankAccountId_ConCuentaSinMovimientos_DebeRetornarListaVacia() {
        
        Long accountId = 999L;
        when(bankMovementRepository.findAllByBankAccountId(accountId)).thenReturn(List.of());

        
        List<BankMovement> result = bankMovementService.findAllByBankAccountId(accountId);

        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bankMovementRepository).findAllByBankAccountId(accountId);
    }

    @Test
    void testSave_MovimientoTipoDebe_DebeGuardarCorrectamente() {
        
        BankMovement debeMovement = new BankMovement(
                null,
                TypeBankMovement.DEBE,
                OriginBankMovement.TARJETA,
                testCreditCard,
                new Date(),
                new BigDecimal("100.00"),
                "Pago con tarjeta"
        );

        when(bankMovementRepository.save(debeMovement)).thenReturn(debeMovement);

        
        BankMovement result = bankMovementService.save(debeMovement);

        
        assertNotNull(result);
        assertEquals(TypeBankMovement.DEBE, result.getTipoMovimientoBancario());
        verify(bankMovementRepository).save(debeMovement);
    }

    @Test
    void testSave_MovimientoTipoHaber_DebeGuardarCorrectamente() {
        
        BankMovement haberMovement = new BankMovement(
                null,
                TypeBankMovement.HABER,
                OriginBankMovement.TRANSFERENCIA,
                null,
                new Date(),
                new BigDecimal("500.00"),
                "Ingreso"
        );

        when(bankMovementRepository.save(haberMovement)).thenReturn(haberMovement);

        
        BankMovement result = bankMovementService.save(haberMovement);

        
        assertNotNull(result);
        assertEquals(TypeBankMovement.HABER, result.getTipoMovimientoBancario());
        verify(bankMovementRepository).save(haberMovement);
    }
}

