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
@Table(name = "db_rental_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RentalDetail extends Auditable{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.ute.rental.service.id.idGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  @ManyToOne
  @JoinColumn(name = "rental_transaction_id")
  private RentalTransaction rentalTransaction;
  @ManyToOne
  @JoinColumn(name = "book_id")
  private Book book;
  private Float refundAmount = 0F;
  private Integer bookCount = 0;
}
