package com.cfk.ebankingbanking.services;

import com.cfk.ebankingbanking.constant.Description;
import com.cfk.ebankingbanking.constant.ExceptionMessages;
import com.cfk.ebankingbanking.dtos.*;
import com.cfk.ebankingbanking.entities.*;
import com.cfk.ebankingbanking.enums.OperationType;
import com.cfk.ebankingbanking.exceptions.BalanceNotSufficientException;
import com.cfk.ebankingbanking.exceptions.BankAccountNotFoundException;
import com.cfk.ebankingbanking.exceptions.CustomerNotFoundException;
import com.cfk.ebankingbanking.mappers.BankAccountMapperImpl;
import com.cfk.ebankingbanking.repositories.AccountOperationRepository;
import com.cfk.ebankingbanking.repositories.BankAccountRepository;
import com.cfk.ebankingbanking.repositories.CustomerRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImpl implements BankAccountService {
    private BankAccountMapperImpl dtoMapper;
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    //Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND_CREATE_CUSTOMER_FIRST);

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentAccount(savedCurrentAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null)
            throw new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND_CREATE_CUSTOMER_FIRST);
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingAccount(savedSavingAccount);
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(ExceptionMessages.BANK_ACCOUNT_NOT_FOUND));
        //SavingAccount savingAccount = null;
        if (bankAccount instanceof SavingAccount) {
           SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingAccount(savingAccount);
        }
        else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(ExceptionMessages.BANK_ACCOUNT_NOT_FOUND));
        if(bankAccount.getBalance()<amount) throw new BalanceNotSufficientException(ExceptionMessages.BALANCE_NOT_SUFFICIENT);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }
    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException(ExceptionMessages.BANK_ACCOUNT_NOT_FOUND));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }
    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount, Description.TRANSFER_TO +accountIdDestination);
        credit(accountIdDestination, amount, Description.TRANSFER_FROM +accountIdSource);
    }
    @Override
    public List<BankAccountDTO> bankAccountList(){
           List<BankAccount> bankAccounts =  bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream()
                .map(op-> dtoMapper.fromAccountOperation(op))
                .collect(Collectors.toList());
    }
    /**
     * Operations
     */
    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException(ExceptionMessages.BANK_ACCOUNT_NOT_FOUND);
        Page<AccountOperation> accountOperationsPages = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationPagesDTOS = accountOperationsPages.getContent().stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTO(accountOperationPagesDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperationsPages.getTotalPages());
        return accountHistoryDTO;

    }

    @Override
    public List<CustomerDTO> searchCustomersByName(String keyword) {
        List<Customer> customerSearched = customerRepository.findByNameContains(keyword);
        return customerSearched.stream().map(customer-> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
    }

    @Override
    public Customer getOneCustomer() {
        //Customer customer = Customer.builder().build();
        Customer customer = Customer.builder()
                .id(2L)
                .name("Paul")
                .email("abc@aubay.com")
                //.address("aaaaaaaaaaa")
                .bankAccounts((List<BankAccount>) new BankAccount())
                .build();
        return customer;
    }
}


