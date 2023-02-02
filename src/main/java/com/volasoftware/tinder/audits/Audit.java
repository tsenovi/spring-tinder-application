package com.volasoftware.tinder.audits;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class Audit {

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private long createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private long updatedAt;

}
