package com.cfk.ebankingbanking.security.service;

import com.cfk.ebankingbanking.security.entities.AppRole;
import com.cfk.ebankingbanking.security.entities.AppUser;
import com.cfk.ebankingbanking.security.repositories.AppRoleRepository;
import com.cfk.ebankingbanking.security.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ClaimsServiceImpl implements ClaimsService, UserDetailsService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;


    @Override
    public AppUser addNewUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole addNewRole(AppRole appRole) {

        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        AppUser appUser = appUserRepository.findByUserName(userName);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        appUser.getAppRoles().add(appRole);
    }

    @Override
    public AppUser loadUserByUserName(String userName) {

        return appUserRepository.findByUserName(userName);
    }

    @Override
    public List<AppUser> listUsers() {

        return appUserRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username, ClaimsService claimsService) throws UsernameNotFoundException {
        return (UserDetails) claimsService.loadUserByUserName(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
