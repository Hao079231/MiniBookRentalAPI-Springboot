package com.ute.rental.repository;

import com.ute.rental.model.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {

  Optional<Group> findFirstByName(String name);

  boolean existsByName(String name);

  Optional<Group> findByKind(Integer kind);
}
