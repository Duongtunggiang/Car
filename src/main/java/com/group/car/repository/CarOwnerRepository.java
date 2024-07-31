package com.group.car.repository;

import com.group.car.models.CarOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarOwnerRepository extends JpaRepository<CarOwner, Long> {
    CarOwner findByAccountId(Long accountId);

    CarOwner findByAccountEmail(String email);

    Optional<CarOwner> findByEmail(String email);
}
