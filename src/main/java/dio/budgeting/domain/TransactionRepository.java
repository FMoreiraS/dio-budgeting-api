package dio.budgeting.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);
    List<Transaction> findAllByDateBetweenOrderByDate(LocalDate startDate, LocalDate endDate);
}
