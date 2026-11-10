package dio.budgeting;

import dio.budgeting.application.ListAllTransactionsUseCase;
import dio.budgeting.application.ListTransactionsByCategoryUseCase;
import dio.budgeting.application.ListTransactionsByDateUseCase;
import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class ToolCallingIT {
    @Autowired
    OpenAiChatModel openAiChatModel;

    @Autowired
    GroqChatModel groqChatModel;

    @Autowired
    static TransactionRepository repository;

    @BeforeAll
    static void populateDb() {
        repository.save(new Transaction("New car tire", 300L, Category.AUTO, LocalDate.of(2026, 2, 14)));
        repository.save(new Transaction("Hamburger and fries", 80L, Category.GROCERIES, LocalDate.of(2026, 5, 20)));
        repository.save(new Transaction("Vitamins", 50L, Category.PHARMA, LocalDate.of(2026, 9, 3)));
    }

//    @Test
//    void should_executeSum_when_prompted() {
//        var chatClient = ChatClient.builder(openAiChatModel)
//                .defaultSystem("Você é um matemático")
//                .defaultTools(new MathTools())
//                .build();
//
//        var response = chatClient.prompt("Some 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba apenas o resultado final sem explicações")
//                .call().content();
//
//        assertThat(response).contains("0");
//        System.out.println(response);
//    }

    @Test
    void mustPersistTransactionBasedOnPrompt() {
        ChatClient chatClient = ChatClient.builder(groqChatModel)
                .defaultSystem("You are a budgeting assistant")
//                        ". You must figure out user's expenses based on the prompts, confirm if the transaction is successful, and show it with: name, category, value, and date")
                .defaultTools(new PersistTransactionUseCase(repository))
                .build();

        // Request: "I stopped at a coffee shop and bought a 5 dollar cappuccino"
        String response = chatClient
                .prompt("I stopped at a coffee shop and bought a 5 dollar cappuccino")
                .call()
                .content();

        Assertions.assertThat(response).containsIgnoringCase("cappuccino");
        Assertions.assertThat(response).containsIgnoringCase("groceries");
        Assertions.assertThat(response).contains("5");
        System.out.println(response);
    }

    @Test
    void mustListAllTransactions() {
        ChatClient chatClient = ChatClient.builder(groqChatModel)
                .defaultSystem("You are a budgeting assistant")
                .defaultTools(new ListAllTransactionsUseCase(repository))
                .build();

        String response = chatClient
                .prompt("I want to see all the expenses")
                .call()
                .content();

        Assertions.assertThat(response).containsIgnoringCase("New car tire");
        Assertions.assertThat(response).containsIgnoringCase("Vitamins");
        System.out.println(response);
    }

    @Test
    void mustListTransactionsInAGivenCategory() {
        ChatClient chatClient = ChatClient.builder(groqChatModel)
                .defaultSystem("You are a budgeting assistant")
                .defaultTools(new ListTransactionsByCategoryUseCase(repository))
                .build();

        String response = chatClient
                .prompt("I want to see the expenses in the groceries category")
                .call()
                .content();

        Assertions.assertThat(response).containsIgnoringCase("Hamburger and fries");
        System.out.println(response);
    }

    @Test
    void mustListTransactionsInADateInterval() {
        ChatClient chatClient = ChatClient.builder(groqChatModel)
                .defaultSystem("You are a budgeting assistant")
                .defaultTools(new ListTransactionsByDateUseCase(repository))
                .build();

        String response = chatClient
                .prompt("I want to see the expenses from May to September of this year")
                .call()
                .content();

        Assertions.assertThat(response).containsIgnoringCase("Hamburger and fries");
        Assertions.assertThat(response).containsIgnoringCase("Vitamins");
        System.out.println(response);
    }

    static class MathTools {
        @Tool(description = "soma dois números inteiros, a e b")
        public int sum(int a, int b) {
            return a + b;
        }

        @Tool(description = "subtrai dois números inteiros, a e b")
        public int diff(int a, int b) {
            return a - b;
        }
    }
}
