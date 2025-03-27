package com.cfk.ebankingbanking.security.repositories;


import com.cfk.ebankingbanking.security.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    public AppRole findByRoleName(String roleName);
}
