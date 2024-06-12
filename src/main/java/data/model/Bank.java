package data.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
public class Bank {
  private String id;

  private String name;

  @Getter(AccessLevel.NONE)
  private Boolean depositFeature;

  private BigDecimal maxExpensePerWithdrawal;
  private BigDecimal maxExpensePerUserDaily;

  @Builder.Default
  private Set<Customer> customers = new HashSet<>();
  @Builder.Default
  private Set<Transaction> transactions = new HashSet<>();

  private BigDecimal feeOtherBank;
  private BigDecimal residueBalance;

  public boolean hasDepositFeature() {
    return this.depositFeature == null ? false : true;
  }

  public Optional<Customer> findCustomerByAccount(String account) {
    return customers.stream().filter(item -> account.equals(item.getAccount())).findAny();
  }

  public Set<Transaction> findAllTransactionsByAccount(String account) {
    return transactions.stream().filter(item -> account.equals(item.getCustomer().getAccount()))
        .collect(Collectors.toSet());
  }

  public BigDecimal getTotalExpenseByAccount(String account) {
    return transactions.stream().filter(item -> account.equals(item.getCustomer().getAccount())).reduce(
        BigDecimal.valueOf(0),
        (a, b) -> a.add(b.getExpense()), BigDecimal::add);
  }

  public BigDecimal getTotalExpenseTodayByAccount(String account) {
    return transactions.stream().filter(item -> account.equals(item.getCustomer().getAccount()))
        .filter(item -> item.getTimestamp().toLocalDate().compareTo(LocalDate.now()) == 0).reduce(
            BigDecimal.valueOf(0),
            (a, b) -> a.add(b.getExpense()), BigDecimal::add);
  }
}
