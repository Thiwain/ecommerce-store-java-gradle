/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.plato.models.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Acer
 */
@Entity
@Table(name = "user_auth")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAuth {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(nullable = false, name = "user_auth_status_id")
    private UserAuthStatus userAuthStatus;

    @Column(nullable = false, length = 200, name = "email")
    private String email;

    @Column(nullable = false, length = 20, name = "password")
    private String password;

    @Column(nullable = false, length = 6, name = "v_code")
    private String vCode;

}
