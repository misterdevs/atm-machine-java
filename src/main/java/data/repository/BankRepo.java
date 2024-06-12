package data.repository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import data.constant.BankCompany;
import data.model.Bank;
import data.model.Customer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class BankRepo {

        private static final long DEFAULT_DAILY_EXPENSE = 5_000_000;
        private static final long DEFAULT_WITHDRAWAL_LIMIT = 2_500_000;
        private static final long DEFAULT_FEE_OTHER_BANK = 2_500;
        private static final long DEFAULT_RESIDUE_BALANCE = 10_000;

        private static final Set<Bank> store = new HashSet<>();
        static {
                Bank bri = Bank.builder()
                                .id(UUID.randomUUID().toString())
                                .name(BankCompany.BRI.getName())
                                .maxExpensePerUserDaily(BigDecimal.valueOf(DEFAULT_DAILY_EXPENSE))
                                .maxExpensePerWithdrawal(BigDecimal.valueOf(DEFAULT_WITHDRAWAL_LIMIT))
                                .feeOtherBank(BigDecimal.valueOf(DEFAULT_FEE_OTHER_BANK))
                                .residueBalance(BigDecimal.valueOf(DEFAULT_RESIDUE_BALANCE))
                                .build();
                store.add(bri);
                bri.getCustomers().add(Customer.builder()
                                .id(UUID.randomUUID().toString())
                                .account("111111111")
                                .pin("111111")
                                .fullName("Nasabah BRI 1")
                                .balance(BigDecimal.valueOf(5_000_000))
                                .build());

                Bank bni = Bank.builder()
                                .id(UUID.randomUUID().toString())
                                .name(BankCompany.BNI.getName())
                                .depositFeature(true)
                                .maxExpensePerUserDaily(BigDecimal.valueOf(DEFAULT_DAILY_EXPENSE))
                                .maxExpensePerWithdrawal(BigDecimal.valueOf(DEFAULT_WITHDRAWAL_LIMIT))
                                .feeOtherBank(BigDecimal.valueOf(DEFAULT_FEE_OTHER_BANK))
                                .residueBalance(BigDecimal.valueOf(DEFAULT_RESIDUE_BALANCE))
                                .build();
                store.add(bni);
                bni.getCustomers().add(Customer.builder()
                                .id(UUID.randomUUID().toString())
                                .account("222222222")
                                .pin("222222")
                                .fullName("Nasabah BNI 1")
                                .balance(BigDecimal.valueOf(3_000_000))
                                .build());

                Bank mandiri = Bank.builder()
                                .id(UUID.randomUUID().toString())
                                .name(BankCompany.MANDIRI.getName())
                                .depositFeature(true)
                                .maxExpensePerUserDaily(BigDecimal.valueOf(DEFAULT_DAILY_EXPENSE))
                                .maxExpensePerWithdrawal(BigDecimal.valueOf(DEFAULT_WITHDRAWAL_LIMIT))
                                .feeOtherBank(BigDecimal.valueOf(DEFAULT_FEE_OTHER_BANK))
                                .residueBalance(BigDecimal.valueOf(DEFAULT_RESIDUE_BALANCE))
                                .build();
                store.add(mandiri);
                mandiri.getCustomers().add(Customer.builder()
                                .id(UUID.randomUUID().toString())
                                .account("333333333")
                                .pin("333333")
                                .fullName("Nasabah Mandiri 1")
                                .balance(BigDecimal.valueOf(1_500_000))
                                .build());

                Bank bca = Bank.builder()
                                .id(UUID.randomUUID().toString())
                                .name(BankCompany.BCA.getName())
                                .maxExpensePerUserDaily(BigDecimal.valueOf(DEFAULT_DAILY_EXPENSE))
                                .maxExpensePerWithdrawal(BigDecimal.valueOf(DEFAULT_WITHDRAWAL_LIMIT))
                                .feeOtherBank(BigDecimal.valueOf(DEFAULT_FEE_OTHER_BANK))
                                .residueBalance(BigDecimal.valueOf(DEFAULT_RESIDUE_BALANCE))
                                .build();
                store.add(bca);
                bca.getCustomers().add(Customer.builder()
                                .id(UUID.randomUUID().toString())
                                .account("444444444")
                                .pin("444444")
                                .fullName("Nasabah BCA 1")
                                .balance(BigDecimal.valueOf(240_000))
                                .build());
        }

        public static Optional<Bank> findBankByName(String name) {
                return store.stream().filter(item -> name.equals(item.getName())).findAny();
        }

        public static Optional<Bank> findBankByAccount(String name) {
                return store.stream().filter(item -> !item.findCustomerByAccount(name).isEmpty()).findFirst();
        }

}
