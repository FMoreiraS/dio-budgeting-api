package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAllTransactionsUseCase {
    private final TransactionRepository repository;

    public ListAllTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    @Tool(name = "list-all-transactions", description = "Lista todas as transações")
    public List<TransactionOutput> execute() {
        return repository.findAll()
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
