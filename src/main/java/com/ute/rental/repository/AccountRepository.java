package com.ute.rental.repository;

import com.ute.rental.model.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  boolean existsByUsername(String username);

  Optional<Account> findByUsername(String username);

  Optional<Account> findByEmail(String email);
}
