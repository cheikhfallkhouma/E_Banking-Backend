package com.cfk.ebankingbanking.security.repositories;


import com.cfk.ebankingbanking.security.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUserName(String userName);
}
