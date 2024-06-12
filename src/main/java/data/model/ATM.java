package data.model;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.NONE)
public class ATM {
  private String id;

  private BigDecimal balance;

  private Bank bank;

  @Setter
  private Customer currentCustomer;

  /**
   * Use this function to subtract balance to ATM
   *
   * @param amount
   */
  public void subtract(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }

  public void add(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
