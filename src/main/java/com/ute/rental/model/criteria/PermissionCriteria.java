package com.ute.rental.model.criteria;

import com.ute.rental.model.Permission;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class PermissionCriteria implements Serializable {
  private static final long serialVerionUID = 1L;
  private String action;
  private String pCode;

  public Specification<Permission> getSpecification(){
    return new Specification<Permission>() {
      @Override
      public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotEmpty(getAction())){
          predicates.add(cb.like(root.get("action"), "%" + getAction() + "%"));
        }

        if (StringUtils.isNotEmpty(getPCode())){
          predicates.add(cb.like(root.get("pCode"), "%" + getPCode() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
