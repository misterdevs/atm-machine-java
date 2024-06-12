package com.tujuhsembilan.validation;

import data.model.Customer;
import data.repository.BankRepo;

public class Auth {

    private static int limitTries = 3;

    public static Boolean isValidAccountNumber(String input) {
        if (!isRegisteredCustomer(input)) {
            System.out.println("Invalid account number!");
            return false;
        }
        if (isBlockedCustomer(BankRepo.findBankByAccount(input).get().findCustomerByAccount(input).get())) {
            System.out.println("Your account was blocked!");
            return false;
        }
        return true;
    }

    public static Boolean isValidPIN(String input, Customer customer) {
        return input.equals(customer.getPin());

    }

    public static Boolean isRegisteredCustomer(String accountNumber) {
        return !BankRepo.findBankByAccount(accountNumber).isEmpty();
    }

    public static Boolean isBlockedCustomer(Customer customer) {
        return customer.getInvalidTries() != null && customer.getInvalidTries() >= limitTries;
    }

}
