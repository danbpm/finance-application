package org.tigerbank.finance.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    // ===== ТЕСТЫ ВАЛИДАЦИИ НАЗВАНИЯ =====

    @Test
    void createCategory_withValidName_shouldSucceed() {
        Category category = new Category("Зарплата", CategoryType.INCOME);
        assertEquals("Зарплата", category.getName());
    }

    @Test
    void createCategory_withNullName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Category(null, CategoryType.INCOME)
        );
    }

    @Test
    void createCategory_withEmptyName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Category("", CategoryType.INCOME)
        );
    }

    @Test
    void createCategory_withWhitespaceName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Category("   ", CategoryType.INCOME)
        );
    }

    @Test
    void createCategory_withNameLongerThan50Chars_shouldThrowException() {
        String longName = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, () ->
                new Category(longName, CategoryType.INCOME)
        );
    }

    @Test
    void createCategory_withNameExactly50Chars_shouldSucceed() {
        String name = "A".repeat(50);
        Category category = new Category(name, CategoryType.INCOME);
        assertEquals(name, category.getName());
    }

    @Test
    void createCategory_withNameWithSpaces_shouldBeTrimmed() {
        Category category = new Category("  Кафе  ", CategoryType.EXPENSE);
        assertEquals("Кафе", category.getName());
    }

    // ===== ТЕСТЫ ТИПА КАТЕГОРИИ =====

    @Test
    void createCategory_withIncomeType_shouldHaveIncomeType() {
        Category category = new Category("Зарплата", CategoryType.INCOME);
        assertEquals(CategoryType.INCOME, category.getType());
    }

    @Test
    void createCategory_withExpenseType_shouldHaveExpenseType() {
        Category category = new Category("Продукты", CategoryType.EXPENSE);
        assertEquals(CategoryType.EXPENSE, category.getType());
    }

    @Test
    void createCategory_withNullType_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Category("Test", null)
        );
    }

    // ===== ТЕСТЫ МЕТОДА ПЕРЕИМЕНОВАНИЯ =====

    @Test
    void renameCategory_withValidName_shouldUpdateName() {
        Category category = new Category("Старое имя", CategoryType.EXPENSE);
        category.rename("Новое имя");
        assertEquals("Новое имя", category.getName());
    }

    @Test
    void renameCategory_withInvalidName_shouldThrowException() {
        Category category = new Category("Valid", CategoryType.EXPENSE);
        assertThrows(IllegalArgumentException.class, () ->
                category.rename("")
        );
    }

    // ===== ТЕСТЫ ПРОВЕРКИ СОВМЕСТИМОСТИ =====

    @Test
    void validateOperationCompatibility_withMatchingTypes_shouldNotThrow() {
        Category incomeCategory = new Category("Зарплата", CategoryType.INCOME);
        // Не должно бросать исключение
        assertDoesNotThrow(() ->
                incomeCategory.validateOperationCompatibility(OperationType.INCOME)
        );
    }

    @Test
    void validateOperationCompatibility_withMismatchedTypes_shouldThrowException() {
        Category expenseCategory = new Category("Кафе", CategoryType.EXPENSE);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                expenseCategory.validateOperationCompatibility(OperationType.INCOME)
        );
        assertTrue(exception.getMessage().contains("Нельзя привязать"));

    }
}