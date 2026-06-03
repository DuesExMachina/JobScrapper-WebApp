package com.JobScrapper.OrchestratorService.models;

import java.time.LocalDateTime;

import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data // generates getters, setters and constructors at run time
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Email(message = "Email should be valid")
    private String email;
    @NotNull
    private String name;
    private String role;
    private String refreshToken; // store refresh token in db to verify user when they request for new access
                                 // token using refresh token
    private LocalDateTime refreshTokenExpiry;
}
