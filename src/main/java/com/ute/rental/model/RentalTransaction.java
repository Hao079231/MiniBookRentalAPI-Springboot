package com.ute.rental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_rental_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RentalTransaction extends Auditable{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.ute.rental.service.id.idGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  @ManyToOne
  @JoinColumn(name = "staff_id")
  private Account staff;
  @ManyToOne
  @JoinColumn(name = "reader_id")
  private Reader reader;
  private Float depositTotal = 0F;
  private Float refundAmountTotal = 0F;
  private Date dueDate;
  private Integer state; // 1 - renting, 2 - complete, 3 - overdue
}
