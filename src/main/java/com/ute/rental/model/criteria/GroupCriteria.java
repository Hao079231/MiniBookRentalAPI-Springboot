package com.ute.rental.model.criteria;

import com.ute.rental.model.Group;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class GroupCriteria implements Serializable {
  private static final long serialVersionUID = 1L;
  @NotNull(message = "kind required")
  private int kind;

  public Specification<Group> getSpecification(){
    return new Specification<Group>() {
      @Override
      public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("kind"), getKind()));
        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
