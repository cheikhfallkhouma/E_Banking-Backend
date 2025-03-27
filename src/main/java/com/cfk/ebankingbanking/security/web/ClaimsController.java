package com.cfk.ebankingbanking.security.web;

import com.cfk.ebankingbanking.security.entities.AppUser;
import com.cfk.ebankingbanking.security.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ClaimsController {

    private final AppUserRepository appUserRepository;

    @GetMapping("/allUsers")
    public AppUser getAllUsers(){
        AppUser appUser = (AppUser) appUserRepository.findAll();
        return appUser;
    }

    @PostMapping("/saveUser")
    public AppUser saveUser(@RequestBody AppUser appUser){
        return appUserRepository.save(appUser);
    }

    @GetMapping("/username")
    public AppUser findUserByUserName(@PathVariable String username){
        return appUserRepository.findByUserName(username);
    }
}
