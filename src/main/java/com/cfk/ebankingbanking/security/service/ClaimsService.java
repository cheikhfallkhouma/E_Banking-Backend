package com.cfk.ebankingbanking.security.service;

import com.cfk.ebankingbanking.security.entities.AppRole;
import com.cfk.ebankingbanking.security.entities.AppUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface ClaimsService {
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String userName, String roleName);
    AppUser loadUserByUserName(String userName);
    List<AppUser> listUsers();

    UserDetails loadUserByUsername(String username, ClaimsService claimsService) throws UsernameNotFoundException;
}
