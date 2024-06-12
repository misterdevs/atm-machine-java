package com.tujuhsembilan;

import static com.tujuhsembilan.logic.ConsoleUtil.createMenu;
import static com.tujuhsembilan.logic.ConsoleUtil.delay;
import static com.tujuhsembilan.logic.ConsoleUtil.enterToContinue;
import static com.tujuhsembilan.logic.ConsoleUtil.resetDisplay;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tujuhsembilan.logic.ATMLogic;

import data.constant.BankCompany;
import data.model.ATM;
import data.model.Bank;
import data.model.Customer;
import data.repository.ATMRepo;
import data.repository.BankRepo;

public class App {

    public static void main(String[] args) {
        List<String> bankList = Arrays.asList(BankCompany.values()).stream()
                .map(item -> "ATM " + item.getName())
                .collect(Collectors.toList());
        String[] atmArr = bankList.toArray(new String[bankList.size()]);
        resetDisplay();
        createMenu(input -> {
            if (Integer.valueOf(input) > 0)
                new App(BankCompany.getByOrder(Integer.valueOf(input) - 1).getName()).start();

        }, "ATM MACHINE", "MULTI", atmArr, 0, "Exit");
        resetDisplay();

    }

    /// --- --- --- --- ---

    final Bank bank;
    final ATM atm;

    public App(String bankName) {
        Bank lBank = null;
        ATM lAtm = null;

        Optional<Bank> qBank = BankRepo.findBankByName(bankName);
        if (qBank.isPresent()) {
            Optional<ATM> qAtm = ATMRepo.findATMByBank(qBank.get());
            if (qAtm.isPresent()) {
                lBank = qBank.get();
                lAtm = qAtm.get();
            }
        }

        this.bank = lBank;
        this.atm = lAtm;

    }

    public void start() {
        if (bank != null && atm != null) {
            ATMLogic.login(atm);
            if (atm.getCurrentCustomer() instanceof Customer) {
                mainMenu();
            }
        } else {
            System.out.println("Cannot find Bank or ATM");
            delay();
        }
    }

    public void mainMenu() {
        String[] mainMenuArr = { "Account Balance Information", "Withdraw", "Top up phone credit", "Power Bills Token",
                "Account Mutation (Transfer)" };
        String[] mainMenuWithDepositArr = { "Account Balance Information", "Withdraw", "Top up phone credit",
                "Power Bills Token",
                "Account Mutation (Transfer)", "Deposit" };

        createMenu(input -> {
            Customer customer = atm.getCurrentCustomer();
            resetDisplay();
            switch (Integer.valueOf(input)) {
                case 1:
                    ATMLogic.accountBalanceInformation(customer);
                    enterToContinue();
                    break;
                case 2:
                    ATMLogic.moneyWithdrawal(customer, atm);
                    enterToContinue();
                    break;
                case 3:
                    ATMLogic.phoneCreditsTopUp(customer, atm);
                    enterToContinue();
                    break;
                case 4:
                    ATMLogic.powerBillsToken(customer, atm);
                    enterToContinue();
                    break;
                case 5:
                    ATMLogic.accountMutation(customer, atm);
                    enterToContinue();
                    break;
                case 6:
                    ATMLogic.moneyDeposit(customer, atm);
                    enterToContinue();
                    break;
            }
        }, atm.getBank().getName() + " Bank", "ATM",
                bank.hasDepositFeature() ? mainMenuWithDepositArr : mainMenuArr, 0, "Exit");
    }

}
