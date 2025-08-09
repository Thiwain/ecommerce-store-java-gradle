package com.plato.controllers.admin.product;

import com.plato.models.product.ProductDetails;
import com.plato.utils.DbUtils;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ProductItem {

    private String id;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private String visibility;
    private String timestamp;
}

@WebServlet("/v1/admin/list-products")
public class ListProductsServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ProductDetails> products = new DbUtils().findAll(session, ProductDetails.class);

            List<ProductItem> productItems = new ArrayList<>();
            for (ProductDetails product : products) {
                ProductItem productItem = new ProductItem();
                productItem.setId(String.valueOf(product.getId()));
                productItem.setTitle(product.getTitle());
                productItem.setPublisher(product.getPublisher().getName());
                productItem.setCategory(product.getCategory().getName());
                productItem.setVisibility(String.valueOf(product.isProductVisibility()));
                productItem.setAuthor(product.getAuthor().getName());
                productItem.setTimestamp(product.getDateTime().toString());
                productItems.add(productItem);
            }

            jrrp.jsonResponseProcess(response, 200, true, productItems, "Products retrieved successfully.");
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Error retrieving products: " + e.getMessage());
        }
    }
}
