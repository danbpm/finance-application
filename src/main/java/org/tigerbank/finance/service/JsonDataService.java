package org.tigerbank.finance.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.tigerbank.finance.dto.FinanceData;
import org.tigerbank.finance.dto.mixin.BankAccountMixin;
import org.tigerbank.finance.dto.mixin.CategoryMixin;
import org.tigerbank.finance.dto.mixin.OperationMixin;
import org.tigerbank.finance.model.BankAccount;
import org.tigerbank.finance.model.Category;
import org.tigerbank.finance.model.Operation;
import org.tigerbank.finance.repository.IBankAccountRepository;
import org.tigerbank.finance.repository.ICategoryRepository;
import org.tigerbank.finance.repository.IOperationRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class JsonDataService implements IDataService {

    private final IBankAccountRepository accountRepo;
    private final ICategoryRepository categoryRepo;
    private final IOperationRepository operationRepo;
    private final ObjectMapper objectMapper;

    public JsonDataService(IBankAccountRepository accountRepo,
                           ICategoryRepository categoryRepo,
                           IOperationRepository operationRepo) {
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
        this.operationRepo = operationRepo;

        // Настраиваем ObjectMapper с Mixin'ами
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.addMixIn(BankAccount.class, BankAccountMixin.class);
        objectMapper.addMixIn(Category.class, CategoryMixin.class);
        objectMapper.addMixIn(Operation.class, OperationMixin.class);
    }

    @Override
    public void exportToFile(String filename) throws IOException {
        FinanceData data = new FinanceData(
                accountRepo.findAll(),
                categoryRepo.findAll(),
                operationRepo.findAll()
        );

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        Files.writeString(Path.of(filename), json);
        System.out.println("Экспорт завершён: " + filename);
    }

    @Override
    public void importFromFile(String filename) throws IOException {
        String json = Files.readString(Path.of(filename));
        FinanceData data = objectMapper.readValue(json, FinanceData.class);

        // Очищаем текущие данные
        accountRepo.findAll().forEach(a -> accountRepo.deleteById(a.getId()));
        categoryRepo.findAll().forEach(c -> categoryRepo.deleteById(c.getId()));
        operationRepo.findAll().forEach(o -> operationRepo.deleteById(o.getId()));

        // Сохраняем импортированные данные
        data.accounts.forEach(accountRepo::save);
        data.categories.forEach(categoryRepo::save);
        data.operations.forEach(operationRepo::save);

        System.out.println("Импорт завершён из: " + filename);
        System.out.println("   Счетов: " + data.accounts.size());
        System.out.println("   Категорий: " + data.categories.size());
        System.out.println("   Операций: " + data.operations.size());
    }
}