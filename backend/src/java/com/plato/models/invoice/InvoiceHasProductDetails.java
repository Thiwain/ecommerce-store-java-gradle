package com.plato.models.invoice;

import com.plato.models.product.ProductDetails;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    private Timestamp timeStamp;
}
