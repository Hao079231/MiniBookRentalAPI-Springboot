package com.ute.rental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Book extends Auditable{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.ute.rental.service.id.idGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String title;
  private String author;
  private Integer stock = 0;
  private Float price = 0F;
  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;
}
