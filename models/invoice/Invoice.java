package com.plato.models.invoice;

import com.plato.models.users.User;
import java.sql.Timestamp;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_time", nullable = false)
    private Timestamp dateTime;

    @Column(name = "receiver_mobile", nullable = false, length = 20)
    private String reciverMobile;

    @Column(name = "receiver_address", columnDefinition = "TEXT", nullable = true)
    private String reciverAddress;

    @Column(name = "total", nullable = false)
    private double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", nullable = true)
    private Discount discount;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;
}
