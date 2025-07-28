package com.plato.models.grn;

import com.plato.models.product.ProductDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "grn_has_product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrnHasProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private Grn grn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_details_id", nullable = false)
    private ProductDetails productDetails;

    @Column(nullable = false)
    private int qty;

    @Column(name = "buying_price", nullable = false)
    private double buyingPrice;

    @Column(name = "selling_price", nullable = false)
    private double sellingPrice;
}
