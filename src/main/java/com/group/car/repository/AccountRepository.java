package com.group.car.repository;

import com.group.car.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    public Account findByEmail(String email);
    public Account findByUsername(String username);

    @Query("SELECT a.id FROM Account a WHERE a.email = :email")
    Long findIdByEmail(@Param("email") String email);

    @Query("SELECT a FROM Account a JOIN a.roles r WHERE r.name = :roleName")
    List<Account> findByRoleName(@Param("roleName") String roleName);
}
