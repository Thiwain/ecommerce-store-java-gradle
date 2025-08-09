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
    private String description;
    private String category;
    private String categoryID;
    private String publisher;
    private String visibility;
    private String timestamp;
    private String imgUrl;
}

@WebServlet("/v1/admin/load-product-detail")
public class LoadProductDetailServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("productID").isEmpty() || request.getParameter("productID").equals(null) || request.getParameter("productID").equals("")) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Data");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ProductDetails product = new DbUtils().findOne(session, ProductDetails.class, "id", Integer.parseInt(request.getParameter("productID")));
            if (product == null) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Does Not Exist");
            }
            ProductItem productItem = new ProductItem();
            productItem.setId(String.valueOf(product.getId()));
            productItem.setTitle(product.getTitle());
            productItem.setPublisher(product.getPublisher().getName().toString());
            productItem.setCategory(product.getCategory().getName());
            productItem.setCategoryID(String.valueOf(product.getCategory().getId()));
            productItem.setDescription(product.getDescription());
            productItem.setVisibility(String.valueOf(product.isProductVisibility()));
            productItem.setAuthor(product.getAuthor().getName());
            productItem.setImgUrl("C:" + product.getImageUrl());
            productItem.setTimestamp(product.getDateTime().toString());

            jrrp.jsonResponseProcess(response, 200, true, productItem, "Products retrieved successfully.");
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Error retrieving products: " + e.getMessage());
        }
    }
}
