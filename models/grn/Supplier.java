package com.plato.models.grn;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "fname", nullable = false, length = 45)
    private String fname;

    @Column(name = "lname", nullable = false, length = 45)
    private String lname;

    @Column(name = "mobile_1", nullable = false, length = 15)
    private String mobile_1;

    @Column(name = "mobile_2", length = 15)
    private String mobile_2;

    @Column(name = "email", length = 200)
    private String email;
}
