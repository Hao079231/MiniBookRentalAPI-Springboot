package com.ute.rental.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable extends ReuseId {
  @CreatedDate
  @Column(name = "created_date", nullable = false)
  private Date createdDate;
  @LastModifiedDate
  @Column(name = "modified_date", nullable = false)
  private Date modifiedDate;
  private int status = 1;
}
