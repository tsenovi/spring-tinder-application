package com.volasoftware.tinder.models;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Audit {

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdDate;

  @Column(name = "updated_at")
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime lastModifiedDate;

}
