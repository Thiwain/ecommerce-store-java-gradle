package com.plato.models.invoice;

import com.plato.models.product.ProductDetails;
import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_has_product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHasProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_details_id", nullable = false)
    private ProductDetails productDetails;

    @Column(nullable = false)
    private int qty;

    @Column(name = "bought_price", nullable = false)
    private double boughtPrice;

    @Column(name = "sold_price", nullable = false)
    private double soldPrice;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;
}
