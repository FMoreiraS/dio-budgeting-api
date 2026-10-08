package dio.budgeting.application;

import java.time.Month;
import java.util.List;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ListTransactionsByMonthUseCase {
    private TransactionRepository repository;

    public ListTransactionsByMonthUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "list-transactions-by-month", description = "Lista transações financeiras realizadas no mês indicado")
    public List<TransactionOutput> execute(@ToolParam(description = "Mês de uma transação") Month month) {
        return repository.findAllByMonth(month)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
