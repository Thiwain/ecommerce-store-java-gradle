/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.plato.controllers.admin.product;

import com.plato.models.product.ProductDetails;
import com.plato.utils.DbUtils;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ProductItem {

    private String id;
}

@WebServlet(name = "GetProductIdsServerlet", urlPatterns = {"/v1/admin/get-product-ids"})
public class GetProductIdsServerlet extends HttpServlet {

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
                productItems.add(productItem);
            }

            jrrp.jsonResponseProcess(response, 200, true, productItems, "Products retrieved successfully.");
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Error retrieving products: " + e.getMessage());
        }
    }

}
