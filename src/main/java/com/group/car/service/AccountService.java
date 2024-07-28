package com.group.car.service;

import com.group.car.models.Account;
import com.group.car.models.Role;
import com.group.car.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account != null) {
            if (!account.isEnabled()) {
                throw new UsernameNotFoundException("User not verified");
            }
            return User.withUsername(account.getEmail())
                    .password(account.getPassword())
                    .roles(account.getRoles().stream().map(Role::getName).toArray(String[]::new))
                    .build();
        }
        throw new UsernameNotFoundException("User not found");
    }
}




//        if (account == null) {
//            account = accountRepository.findByUsername(email);
//        }