package dio.budgeting.application;

import java.time.LocalDate;
import java.util.List;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ListTransactionsByDateUseCase {
    private final TransactionRepository repository;

    public ListTransactionsByDateUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "list-transactions-by-date-interval", description = "Lista transações realizadas no intervalo indicado")
    public List<TransactionOutput> execute(@ToolParam(description = "Data de início") LocalDate startDate,
                                           @ToolParam(description = "Data de fim") LocalDate endDate) {
        return repository.findAllByDateBetweenOrderByDate(startDate, endDate)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
