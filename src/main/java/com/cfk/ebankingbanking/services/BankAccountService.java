package com.cfk.ebankingbanking.services;

import com.cfk.ebankingbanking.dtos.*;
import com.cfk.ebankingbanking.entities.BankAccount;
import com.cfk.ebankingbanking.entities.CurrentAccount;
import com.cfk.ebankingbanking.entities.Customer;
import com.cfk.ebankingbanking.entities.SavingAccount;
import com.cfk.ebankingbanking.exceptions.BalanceNotSufficientException;
import com.cfk.ebankingbanking.exceptions.BankAccountNotFoundException;
import com.cfk.ebankingbanking.exceptions.CustomerNotFoundException;


import java.util.List;

public interface BankAccountService {
    CurrentAccountDTO saveCurrentBankAccount (double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccountDTO saveSavingBankAccount (double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    //List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();




    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    public List<CustomerDTO> searchCustomersByName(String keyword);

    Customer getOneCustomer();
}
