package org.tigerbank.finance.console;

import org.tigerbank.finance.model.*;
import org.tigerbank.finance.repository.*;
import org.tigerbank.finance.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

@Component
public class FinanceRunner implements CommandLineRunner {

    private final IOperationService operationService;
    private final IAnalyticsService analyticsService;
    private final IBankAccountRepository accountRepo;
    private final ICategoryRepository categoryRepo;
    private final IDataService dataService;

    public FinanceRunner(IOperationService os, IAnalyticsService as,
                         IBankAccountRepository ar, ICategoryRepository cr,
                         IDataService ds) {
        this.operationService = os;
        this.analyticsService = as;
        this.accountRepo = ar;
        this.categoryRepo = cr;
        this.dataService = ds;
    }

    @Override
    public void run(String... args) throws IOException {
        System.out.println("ТИГРБАНК: Модуль «Учет финансов»\n");

        // Создаём счёт
        BankAccount mainAccount = accountRepo.save(new BankAccount("Основной счёт"));
        System.out.println("Счёт создан: " + mainAccount.getName() + " ID: " + mainAccount.getId());

        // Создаём категории
        Category salary = categoryRepo.save(new Category("Зарплата", CategoryType.INCOME));
        Category cafe = categoryRepo.save(new Category("Кафе", CategoryType.EXPENSE));
        Category health = categoryRepo.save(new Category("Здоровье", CategoryType.EXPENSE));
        System.out.println("Категории: " + salary.getName() + ", " + cafe.getName() + ", " + health.getName());

        // Добавляем операции
        operationService.addOperation(new Operation(
                OperationType.INCOME, mainAccount.getId(), new BigDecimal("100000.00"),
                LocalDate.now().minusDays(5), "Аванс", salary.getId()
        ));
        operationService.addOperation(new Operation(
                OperationType.EXPENSE, mainAccount.getId(), new BigDecimal("450.50"),
                LocalDate.now().minusDays(3), "Обед", cafe.getId()
        ));
        operationService.addOperation(new Operation(
                OperationType.EXPENSE, mainAccount.getId(), new BigDecimal("2500.00"),
                LocalDate.now().minusDays(2), "Стоматолог", health.getId()
        ));
        operationService.addOperation(new Operation(
                OperationType.INCOME, mainAccount.getId(), new BigDecimal("90000.00"),
                LocalDate.now(), "Зарплата", salary.getId()
        ));
        System.out.println("\nДобавлено 4 операции");

        // Показываем баланс
        BigDecimal balance = operationService.getAccountBalance(mainAccount.getId());
        System.out.println("\nТекущий баланс: " + balance + " у.е.");

        // Аналитика за период
        BigDecimal netIncome = analyticsService.calculateNetIncome(
                LocalDate.now().minusDays(7),
                LocalDate.now()
        );
        System.out.println("Чистый доход за неделю: " + netIncome + " у.е.");

        // Группировка по категориям
        System.out.println("\nРасходы по категориям за неделю:");
        Map<Category, BigDecimal> expenses = analyticsService.groupByCategory(
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                OperationType.EXPENSE
        );
        expenses.forEach((cat, amt) ->
                System.out.printf("   • %-15s: %,.2f у.е.%n", cat.getName(), amt)
        );

        // 7. Экспорт в JSON
        String exportContent = """
            {
              "account": "%s",
              "balance": %.2f,
              "total_operations": %d,
              "income_operations": %d,
              "expense_operations": %d,
              "period_start": "%s",
              "period_end": "%s",
              "net_income": %.2f
            }
            """.formatted(
                mainAccount.getName(),
                balance,
                operationService.getAccountOperations(mainAccount.getId()).size(),
                operationService.getAccountOperations(mainAccount.getId()).stream()
                        .filter(op -> op.getType() == OperationType.INCOME).count(),
                operationService.getAccountOperations(mainAccount.getId()).stream()
                        .filter(op -> op.getType() == OperationType.EXPENSE).count(),
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                netIncome
        );

        Files.writeString(Path.of("tigerbank_export.json"), exportContent);
        System.out.println("Данные экспортированы в tigerbank_export.json");

        // ЭКСПОРТ ДАННЫХ
        System.out.println("\nЭКСПОРТ ДАННЫХ В JSON...");
        dataService.exportToFile("tigerbank_full_export.json");

        // ИМПОРТ ДАННЫХ (демонстрация)
        System.out.println("\nИМПОРТ ДАННЫХ ИЗ JSON (для проверки)...");
        dataService.importFromFile("tigerbank_full_export.json");

        // Проверка после импорта
        System.out.println("\nПроверка после импорта:");
        System.out.println("   Счетов в системе: " + accountRepo.findAll().size());
        System.out.println("   Баланс основного счёта: " +
                operationService.getAccountBalance(accountRepo.findAll().get(0).getId()) + " ₽");

        System.out.println("\n Работа завершена! Все данные сохранены в tigerbank_full_export.json");
        System.exit(0);
    }
}