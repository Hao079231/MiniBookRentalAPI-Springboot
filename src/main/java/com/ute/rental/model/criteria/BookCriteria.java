package com.ute.rental.model.criteria;

import com.ute.rental.model.Book;
import com.ute.rental.model.Category;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class BookCriteria {
  private static final long serialVerionUID = 1L;
  private String title;
  private String author;
  private Long categoryId;
  private Float price;

  public Specification<Book> getSpecification(){
    return new Specification<Book>() {
      @Override
      public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotEmpty(getTitle())){
          predicates.add(cb.like(root.get("title"), "%" + getTitle() + "%"));
        }

        if (StringUtils.isNotEmpty(getAuthor())){
          predicates.add(cb.like(root.get("author"), "%" + getAuthor() + "%"));
        }

        if (getPrice() != null){
          predicates.add(cb.equal(root.get("price"), getPrice()));
        }

        if (getCategoryId() != null){
          Join<Book, Category> categoryJoin = root.join("category");
          predicates.add(cb.equal(categoryJoin.get("id"), getCategoryId()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
