package com.volasoftware.tinder.accounts;

import com.volasoftware.tinder.audits.Audit;
import com.volasoftware.tinder.constants.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account extends Audit {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_gen")
  @SequenceGenerator(name = "account_gen", sequenceName = "account_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  private String firstName;
  private String lastName;
  private String email;
  private String password;

  @Enumerated(EnumType.ORDINAL)
  private Gender gender;

}
