// src/test/java/com/tigerbank/finance/model/BankAccountTest.java
package org.tigerbank.finance.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountTest {

    @Test
    void deposit_shouldIncreaseBalance() {
        BankAccount account = new BankAccount("Test");
        account.deposit(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("500.00"), account.getBalance());
    }

    @Test
    void withdraw_shouldDecreaseBalance() {
        BankAccount account = new BankAccount("Test");
        account.deposit(new BigDecimal("1000.00"));
        account.withdraw(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("700.00"), account.getBalance());
    }

    @Test
    void withdraw_shouldThrowExceptionWhenInsufficientFunds() {
        BankAccount account = new BankAccount("Test");
        account.deposit(new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(new BigDecimal("200.00"))
        );
    }

    @Test
    void rename_shouldUpdateName() {
        BankAccount account = new BankAccount("Old Name");
        account.rename("New Name");
        assertEquals("New Name", account.getName());
    }
}