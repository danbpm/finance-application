package org.tigerbank.finance.service;

import org.tigerbank.finance.repository.IOperationRepository;
import org.tigerbank.finance.repository.ICategoryRepository;
import org.tigerbank.finance.model.Operation;
import org.tigerbank.finance.model.OperationType;
import org.tigerbank.finance.model.Category;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService implements  IAnalyticsService{
    private final IOperationRepository operationRepo;
    private final ICategoryRepository categoryRepo;

    public AnalyticsService(IOperationRepository operationRepo, ICategoryRepository categoryRepo) {
        this.operationRepo = operationRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public BigDecimal calculateNetIncome(LocalDate from, LocalDate to) {
        List<Operation> ops = operationRepo.findByPeriod(from, to);
        BigDecimal income = ops.stream()
                .filter((op) -> op.getType() == OperationType.INCOME)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal expense = ops.stream()
                .filter((op) -> op.getType() == OperationType.EXPENSE)
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return income.subtract(expense);
    }

    /**
     * @implNote Неизвестные категории(те у которых ID равен null) не попадают в результирующую выборку
     */
    @Override
    public Map<Category, BigDecimal> groupByCategory(LocalDate from, LocalDate to, OperationType opType) {
      return  operationRepo.findByPeriod(from, to).stream()
              .filter((op) -> op.getType() == opType && op.getCategoryId() != null)
              .collect(Collectors.groupingBy(
                      (op) -> categoryRepo.findById(op.getCategoryId())
                              .orElseThrow(() -> new IllegalArgumentException("Категория отсутствует")),
                      Collectors.reducing(BigDecimal.ZERO, Operation::getAmount, BigDecimal::add)
              ));
    }
}
