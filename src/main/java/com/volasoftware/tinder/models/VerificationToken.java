package com.volasoftware.tinder.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends Audit {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  private LocalDateTime verifiedAt;

  @ManyToOne
  @JoinColumn(nullable = false, name = "account_id")
  private Account account;

  public VerificationToken(String token,
      LocalDateTime createdDate,
      LocalDateTime lastModifiedDate,
      LocalDateTime expiresAt,
      Account account) {
    super(createdDate, lastModifiedDate);
    this.token = token;
    this.expiresAt = expiresAt;
    this.account = account;
  }
}
