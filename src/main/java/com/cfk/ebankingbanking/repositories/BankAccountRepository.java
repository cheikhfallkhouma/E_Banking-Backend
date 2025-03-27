package com.cfk.ebankingbanking.repositories;

import com.cfk.ebankingbanking.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
