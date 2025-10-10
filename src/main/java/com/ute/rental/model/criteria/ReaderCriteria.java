package com.ute.rental.model.criteria;

import com.ute.rental.model.Reader;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class ReaderCriteria {
  private static final long serialVerionUID = 1L;
  private String name;
  private String email;
  private String phone;

  public Specification<Reader> getSpecification(){
    return new Specification<Reader>() {
      @Override
      public Predicate toPredicate(Root<Reader> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotEmpty(getName())) {
          predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (StringUtils.isNotEmpty(getEmail())) {
          predicates.add(cb.like(cb.lower(root.get("email")), "%" + getEmail().toLowerCase() + "%"));
        }

        if (StringUtils.isNotBlank(getPhone())) {
          predicates.add(cb.like(root.get("phone"), "%" + getPhone() + "%"));
        }

        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
