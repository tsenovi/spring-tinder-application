package com.volasoftware.tinder.audits;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public abstract class Audit {

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private long createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  private long updatedAt;

}
