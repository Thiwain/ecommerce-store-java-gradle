package com.plato.models.users;

import java.sql.Timestamp;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fname", nullable = false)
    private String fname;

    @Column(name = "lname", nullable = false)
    private String lname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "v_code", nullable = false, length = 6)
    private String vCode;

    @Column(name = "mobile", nullable = false, length = 15)
    private String mobile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_auth_status_id", nullable = false)
    private UserAuthStatus userAuthStatus;

    @Column(name = "date_time", nullable = false)
    private Timestamp dateTime;
}
