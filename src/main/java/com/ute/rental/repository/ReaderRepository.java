package com.ute.rental.repository;

import com.ute.rental.model.Reader;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReaderRepository extends JpaRepository<Reader, Long>, JpaSpecificationExecutor<Reader> {

  Optional<Reader> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);
}
