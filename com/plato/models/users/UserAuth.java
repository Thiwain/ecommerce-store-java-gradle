package com.plato.models.users;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_auth")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_auth_status_id", nullable = false)
    private UserAuthStatus userAuthStatus;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true, length = 200, name = "email")
    private String email;

    @Column(nullable = false, length = 20, name = "password")
    private String password;

    @Column(nullable = true, length = 6, name = "v_code")
    private String vCode;

    @Column(name = "created_at", nullable = false)
    private Timestamp dateTime;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updateDateTime;
}
