package com.tujuhsembilan.logic;

import static com.tujuhsembilan.logic.ConsoleUtil.createTable;
import static com.tujuhsembilan.logic.ConsoleUtil.delay;
import static com.tujuhsembilan.logic.ConsoleUtil.enterToContinue;
import static com.tujuhsembilan.logic.ConsoleUtil.isNumber;
import static com.tujuhsembilan.logic.ConsoleUtil.isNumberLength;
import static com.tujuhsembilan.logic.ConsoleUtil.printTitle;
import static com.tujuhsembilan.logic.ConsoleUtil.printTitleCustom;
import static com.tujuhsembilan.logic.ConsoleUtil.resetDisplay;
import static com.tujuhsembilan.logic.ConsoleUtil.rupiahFormatter;
import static com.tujuhsembilan.logic.ConsoleUtil.validate;
import static com.tujuhsembilan.logic.ConsoleUtil.validateCustom;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.tujuhsembilan.validation.Auth;

import data.constant.TransactionType;
import data.model.ATM;
import data.model.Bank;
import data.model.Customer;
import data.model.Transaction;
import data.repository.BankRepo;
import de.vandermeer.asciithemes.TA_GridThemes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class ATMLogic {

  public static void login(ATM atm) {
    resetDisplay();
    String account = validateCustom("Input your account number",
        input -> {
          if (!Auth.isRegisteredCustomer(input)) {
            System.out.println("Invalid account number!");
            return false;
          }
          if (Auth.isBlockedCustomer(BankRepo.findBankByAccount(input).get().findCustomerByAccount(input).get())) {
            System.out.println("Your account was blocked!");
            return false;
          }
          return true;
        });

    validateCustom("Input your PIN", input -> {
      Customer getAccount = BankRepo.findBankByAccount(account).get().findCustomerByAccount(account).get();
      getAccount.setInvalidTries(getAccount.getInvalidTries() != null ? getAccount.getInvalidTries() + 1 : 1);

      if (Auth.isBlockedCustomer(getAccount)) {
        resetDisplay();
        printTitleCustom("You have " + getAccount.getInvalidTries() + " invalid tries. Your account has been blocked!",
            2, 2);
        delay();
        return true;
      }
      if (!Auth.isValidPIN(input, getAccount)) {
        System.out.println("Invalid PIN!");
        return false;
      }

      if (!Auth.isBlockedCustomer(getAccount) && Auth.isValidPIN(input, getAccount))
        atm.setCurrentCustomer(getAccount);

      return true;
    });

  }

  public static void accountBalanceInformation(Customer customer) {
    resetDisplay();
    printTitle("Account Balance Information");
    printTitleCustom(rupiahFormatter(customer.getBalance().intValue()), 1, 2);
  }

  public static void moneyWithdrawal(Customer customer, ATM atm) {
    Bank customerBank = BankRepo.findBankByAccount(customer.getAccount()).get();
    int[] amounts = { 3000000, 1000000, 300000, 100000, 50000, 20000, 10000 };

    resetDisplay();
    printTitle("Withdraw");
    createTable(table -> {
      for (int i = 0; i < amounts.length; i++) {
        table.addRow((i + 1) + ". " + rupiahFormatter(amounts[i]));
      }
      table.addRow("");
      table.getContext().setGridTheme(TA_GridThemes.NONE);
      System.out.println(table.render());
    });

    String amount = validateCustom("Choose amount (1,2,3,4,5)", input -> {
      if (!isNumber(input)) {
        System.out.println("Number only!");
        return false;
      }
      if (Integer.valueOf(input) < 1 || Integer.valueOf(input) > amounts.length) {
        System.out.println("Choose available amount onyl!");
        return false;
      }

      return true;
    });

    BigDecimal amountBD = new BigDecimal(amounts[Integer.valueOf(amount) - 1]);
    BigDecimal totalAmountBD = amountBD;
    BigDecimal balanceWithResidue = customer.getBalance().subtract(customerBank.getResidueBalance());

    if (!customerBank.getName().equals(atm.getBank().getName())) {
      totalAmountBD = amountBD.add(customerBank.getFeeOtherBank());
    }

    if (customerBank.getMaxExpensePerWithdrawal().compareTo(totalAmountBD) < 0) {
      resetDisplay();
      printTitle("Withdraw");
      printTitleCustom("Maximum limit per withdraw has been reached", 1, 2);
    } else if (balanceWithResidue.compareTo(totalAmountBD) < 0) {
      resetDisplay();
      printTitle("Withdraw");
      printTitleCustom("Balance not enough", 1, 2);
    } else if (customerBank.getMaxExpensePerUserDaily()
        .compareTo(customerBank.getTotalExpenseTodayByAccount(customer.getAccount())) < 0) {
      resetDisplay();
      printTitle("Withdraw");
      printTitleCustom("Maximum daily transactions has been reached", 1, 2);
    } else {
      customer.subtract(totalAmountBD);
      atm.getBalance().subtract(amountBD);
      createTransaction(customer,
          TransactionType.WITHDRAWAL, totalAmountBD);
      accountBalanceInformation(customer);
    }
  }

  public static void phoneCreditsTopUp(Customer customer, ATM atm) {
    Boolean isSuccess;
    Bank customerBank = BankRepo.findBankByAccount(customer.getAccount()).get();
    int[] amounts = { 10000, 20000, 50000, 100000 };

    resetDisplay();
    printTitle("Top up phone credit");
    String phoneNumber = validate("Input phone number", "Invalid phone number!",
        s -> isNumber(s) && isNumberLength(s, 3, 13));

    resetDisplay();
    printTitle("Top up phone credit");
    createTable(table -> {
      for (int i = 0; i < amounts.length; i++) {
        table.addRow((i + 1) + ". " + rupiahFormatter(amounts[i]));
      }
      table.addRow("");
      table.getContext().setGridTheme(TA_GridThemes.NONE);
      System.out.println(table.render());
    });

    String chosenAmount = validateCustom("Choose credit amount (1,2,3,4)", input -> {
      if (!isNumber(input)) {
        System.out.println("Number only!");
        return false;
      }
      if (Integer.valueOf(input) < 1 || Integer.valueOf(input) > amounts.length) {
        System.out.println("Choose available amount onyl!");
        return false;
      }

      return true;
    });

    int amount = amounts[Integer.valueOf(chosenAmount) - 1];
    BigDecimal amountBD = new BigDecimal(amount);
    BigDecimal balanceWithResidue = customer.getBalance().subtract(customerBank.getResidueBalance());

    if (balanceWithResidue.compareTo(amountBD) >= 0) {
      customer.subtract(amountBD);
      createTransaction(customer, TransactionType.TOP_UP, amountBD);
      isSuccess = true;
    } else {
      isSuccess = false;
    }

    resetDisplay();
    printTitle("Top up phone credit");
    createTable(table -> {
      table.addRow("Credit Amount", ": " + rupiahFormatter(Integer.valueOf(amount)));
      table.addRow("Phone Number", ": " + phoneNumber);
      table.addRow("Balance", ": " + rupiahFormatter(customer.getBalance().intValue()));
      table.addRow("Status", ": " + (isSuccess ? "SUCCESS" : "FAILED"));
      table.addRow("", "");
      table.getContext().setGridTheme(TA_GridThemes.NONE);
      System.out.println(table.render());
    });
  }

  public static void powerBillsToken(Customer customer, ATM atm) {
    Boolean isSuccess;
    Bank customerBank = BankRepo.findBankByAccount(customer.getAccount()).get();
    int[] amounts = { 50000, 100000, 200000, 500000 };

    resetDisplay();
    printTitle("Power Bills Token");
    String meterNumber = validate("Input meter number", "Invalid meter number!", s -> isNumber(s));

    resetDisplay();
    printTitle("Power Bills Token");
    createTable(table -> {
      for (int i = 0; i < amounts.length; i++) {
        table.addRow((i + 1) + ". " + rupiahFormatter(amounts[i]));
      }
      table.addRow("");
      table.getContext().setGridTheme(TA_GridThemes.NONE);
      System.out.println(table.render());
    });

    String chosenAmount = validateCustom("Choose token amount (1,2,3,4)", input -> {
      if (!isNumber(input)) {
        System.out.println("Number only!");
        return false;
      }
      if (Integer.valueOf(input) < 1 || Integer.valueOf(input) > amounts.length) {
        System.out.println("Choose available amount onyl!");
        return false;
      }

      return true;
    });

    int amount = amounts[Integer.valueOf(chosenAmount) - 1];
    BigDecimal amountBD = new BigDecimal(amount);
    BigDecimal balanceWithResidue = customer.getBalance().subtract(customerBank.getResidueBalance());

    if (balanceWithResidue.compareTo(amountBD) >= 0) {
      customer.subtract(amountBD);
      createTransaction(customer, TransactionType.TOP_UP, amountBD);
      isSuccess = true;
    } else {
      isSuccess = false;
    }

    resetDisplay();
    printTitle("Power Bills Token");
    createTable(table -> {
      if (isSuccess)
        table.addRow("Token", ": " + UUID.randomUUID().toString() + "_" + amount);
      table.addRow("Meter Number", ": " + meterNumber);
      table.addRow("Balance", ": " + rupiahFormatter(customer.getBalance().intValue()));
      table.addRow("Status", ": " + (isSuccess ? "SUCCESS" : "FAILED"));
      table.addRow("", "");
      table.getContext().setGridTheme(TA_GridThemes.NONE);
      System.out.println(table.render());
    });
  }

  public static void accountMutation(Customer customer, ATM atm) {
    resetDisplay();
    printTitle("Account Mutation");
    String destinationAccountNumber = validate("Input destination account number", "Invalid account number!",
        input -> !BankRepo.findBankByAccount(input).isEmpty());

    resetDisplay();
    printTitle("Account Mutation");
    String amount = validate("Input amount", "Number only!",
        input -> isNumber(input));

    Bank customerBank = BankRepo.findBankByAccount(customer.getAccount()).get();
    Bank receivedBank = BankRepo.findBankByAccount(destinationAccountNumber).get();

    BigDecimal amountBD = new BigDecimal(amount);
    BigDecimal totalAmountBD = amountBD;
    BigDecimal fee = customerBank.getFeeOtherBank();
    BigDecimal balanceWithResidue = customer.getBalance().subtract(customerBank.getResidueBalance());

    if (!customerBank.getName().equals(receivedBank.getName())) {
      totalAmountBD = amountBD.add(fee);
    }

    if (balanceWithResidue.compareTo(amountBD) >= 0) {
      customer.subtract(totalAmountBD);
      receivedBank.findCustomerByAccount(destinationAccountNumber).get().getBalance().add(amountBD);
      createTransaction(customer, TransactionType.WITHDRAWAL, totalAmountBD);

      resetDisplay();
      printTitle("Account Mutation");
      createTable(table -> {
        table.addRow("Destination Account Number", ": " + destinationAccountNumber);
        table.addRow("Transferred Amount", ": " + rupiahFormatter(amountBD.intValue()));
        table.addRow("Transaction fee",
            ": " + rupiahFormatter(customerBank.getName().equals(receivedBank.getName()) ? 0 : fee.intValue()));
        table.addRow("Balance", ": " + rupiahFormatter(customer.getBalance().intValue()));
        table.addRow("Status", ": SUCCESS");
        table.addRow("", "");
        table.getContext().setGridTheme(TA_GridThemes.NONE);
        System.out.println(table.render());
      });
    } else {
      resetDisplay();
      printTitle("Account Mutation");
      printTitleCustom("Balance not enough", 1, 2);
    }
  }

  public static void moneyDeposit(Customer customer, ATM atm) {
    if (atm.getBank().hasDepositFeature()) {
      resetDisplay();
      printTitle("Deposit");
      String amount = validateCustom("Input amount", s -> {
        if (!isNumber(s)) {
          System.out.println("Number only!");
          return false;
        }
        if (Integer.valueOf(s) % 10000 != 0) {
          System.out.println("Multiples 10.000 only!");
          return false;
        }
        return true;
      });

      BigDecimal amountBD = new BigDecimal(amount);

      customer.add(amountBD);
      atm.add(amountBD);
      accountBalanceInformation(customer);
    } else {
      resetDisplay();
      printTitle("Deposit");
      printTitleCustom("The machine not supported this feature", 1, 2);
    }
  }

  private static void createTransaction(Customer customer, TransactionType type,
      BigDecimal amount) {
    Bank customerBank = BankRepo.findBankByAccount(customer.getAccount()).get();
    customerBank.getTransactions().add(Transaction.builder()
        .id(UUID.randomUUID().toString())
        .timestamp(LocalDateTime.now())
        .customer(customer)
        .type(type)
        .expense(amount)
        .build());
  }

}
