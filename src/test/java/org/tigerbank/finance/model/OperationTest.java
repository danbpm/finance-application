package org.tigerbank.finance.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OperationTest {

    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    // ===== ТЕСТЫ ВАЛИДАЦИИ СУММЫ =====

    @Test
    void createOperation_withPositiveAmount_shouldSucceed() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        assertEquals(new BigDecimal("100.00"), op.getAmount());
    }

    @Test
    void createOperation_withZeroAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.INCOME,
                        accountId,
                        BigDecimal.ZERO,
                        LocalDate.now(),
                        "Test",
                        categoryId
                )
        );
    }

    @Test
    void createOperation_withNegativeAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.EXPENSE,
                        accountId,
                        new BigDecimal("-50.00"),
                        LocalDate.now(),
                        "Test",
                        categoryId
                )
        );
    }

    @Test
    void createOperation_withNullAmount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.INCOME,
                        accountId,
                        null,
                        LocalDate.now(),
                        "Test",
                        categoryId
                )
        );
    }

    @Test
    void createOperation_withAmountShouldBeRoundedToTwoDecimals() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("123.456"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        assertEquals(new BigDecimal("123.46"), op.getAmount());
    }

    // ===== ТЕСТЫ ВАЛИДАЦИИ ДАТЫ =====

    @Test
    void createOperation_withPastDate_shouldSucceed() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now().minusDays(1),
                "Test",
                categoryId
        );
        assertEquals(LocalDate.now().minusDays(1), op.getDate());
    }

    @Test
    void createOperation_withTodayDate_shouldSucceed() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        assertEquals(LocalDate.now(), op.getDate());
    }

    @Test
    void createOperation_withFutureDate_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.INCOME,
                        accountId,
                        new BigDecimal("100.00"),
                        LocalDate.now().plusDays(1),
                        "Test",
                        categoryId
                )
        );
    }

    @Test
    void createOperation_withNullDate_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.INCOME,
                        accountId,
                        new BigDecimal("100.00"),
                        null,
                        "Test",
                        categoryId
                )
        );
    }

    // ===== ТЕСТЫ ВАЛИДАЦИИ ОПИСАНИЯ =====

    @Test
    void createOperation_withValidDescription_shouldBeTrimmed() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "  Тестовое описание  ",
                categoryId
        );
        assertEquals("Тестовое описание", op.getDescription());
    }

    @Test
    void createOperation_withEmptyDescription_shouldBeNull() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "",
                categoryId
        );
        assertNull(op.getDescription());
    }

    @Test
    void createOperation_withWhitespaceDescription_shouldBeNull() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "   ",
                categoryId
        );
        assertNull(op.getDescription());
    }

    @Test
    void createOperation_withDescriptionLongerThan255Chars_shouldThrowException() {
        String longDesc = "A".repeat(256);
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.INCOME,
                        accountId,
                        new BigDecimal("100.00"),
                        LocalDate.now(),
                        longDesc,
                        categoryId
                )
        );
    }

    @Test
    void createOperation_withDescriptionExactly255Chars_shouldSucceed() {
        String desc = "A".repeat(255);
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                desc,
                categoryId
        );
        assertEquals(desc, op.getDescription());
    }

    // ===== ТЕСТЫ ОБЯЗАТЕЛЬНЫХ ПОЛЕЙ =====

    @Test
    void createOperation_withNullType_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        null,
                        accountId,
                        new BigDecimal("100.00"),
                        LocalDate.now(),
                        "Test",
                        categoryId
                )
        );
    }

    @Test
    void createOperation_withNullAccountId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Operation(
                        OperationType.INCOME,
                        null,
                        new BigDecimal("100.00"),
                        LocalDate.now(),
                        "Test",
                        categoryId
                )
        );
    }

    // ===== ТЕСТЫ МЕТОДОВ ИЗМЕНЕНИЯ СОСТОЯНИЯ =====

    @Test
    void updateDescription_withValidText_shouldUpdate() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Старое",
                categoryId
        );
        op.updateDescription("Новое описание");
        assertEquals("Новое описание", op.getDescription());
    }

    @Test
    void updateDescription_withEmptyString_shouldSetToNull() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        op.updateDescription("");
        assertNull(op.getDescription());
    }

    @Test
    void getAmountWithSign_forIncome_shouldBePositive() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("500.00"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        assertEquals(new BigDecimal("500.00"), op.getAmountWithSign());
    }

    @Test
    void getAmountWithSign_forExpense_shouldBeNegative() {
        Operation op = new Operation(
                OperationType.EXPENSE,
                accountId,
                new BigDecimal("300.00"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        assertEquals(new BigDecimal("-300.00"), op.getAmountWithSign());
    }

    // ===== ТЕСТЫ КАТЕГОРИИ (ОПЦИОНАЛЬНОЙ) =====

    @Test
    void createOperation_withNullCategoryId_shouldSucceed() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Test",
                null // Категория не указана
        );
        assertNull(op.getCategoryId());
    }

    @Test
    void removeCategory_shouldSetCategoryIdToNull() {
        Category category = new Category("Кафе", CategoryType.EXPENSE);
        Operation op = new Operation(
                OperationType.EXPENSE,
                accountId,
                new BigDecimal("200.00"),
                LocalDate.now(),
                "Test",
                category.getId()
        );
        op.removeCategory();
        assertNull(op.getCategoryId());
    }

    @Test
    void assignCategory_withCompatibleType_shouldUpdateCategoryId() {
        Category incomeCat = new Category("Зарплата", CategoryType.INCOME);
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                "Test",
                null
        );
        op.assignCategory(incomeCat);
        assertEquals(incomeCat.getId(), op.getCategoryId());
    }

    @Test
    void assignCategory_withIncompatibleType_shouldThrowException() {
        Category expenseCat = new Category("Кафе", CategoryType.EXPENSE);
        Operation incomeOp = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                "Test",
                null
        );
        assertThrows(IllegalArgumentException.class, () ->
                incomeOp.assignCategory(expenseCat)
        );
    }

    @Test
    void assignCategory_withNull_shouldRemoveCategory() {
        Operation op = new Operation(
                OperationType.INCOME,
                accountId,
                new BigDecimal("100.00"),
                LocalDate.now(),
                "Test",
                categoryId
        );
        op.assignCategory(null);
        assertNull(op.getCategoryId());
    }
}