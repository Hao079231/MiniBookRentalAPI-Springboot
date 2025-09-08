package com.ute.rental.repository;

import com.ute.rental.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository extends JpaRepository<Permission, Long>,
    JpaSpecificationExecutor<Permission> {

  boolean existsByAction(String action);
  boolean existsByPermissionCode(String permissionCode);
}
