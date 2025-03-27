package com.cfk.ebankingbanking.dtos;
import com.cfk.ebankingbanking.enums.AccountStatus;
import lombok.Data;
import java.util.Date;
@Data
public class SavingAccountDTO extends BankAccountDTO{
    private String id;
    private  double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;
}
