package com.plato.controllers.admin.product;

import com.plato.models.product.ProductDetails;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ChanageVisibilityDTO {

    private String productID;
    private boolean visibility;
}

@WebServlet(name = "ProductVisibilityUpdateServerlet", urlPatterns = {"/admin/v1/change-product-visibility"})
public class ProductVisibilityUpdateServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ChanageVisibilityDTO dto;
        try {
            dto = jrrp.jsonRequestProcess(request, ChanageVisibilityDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON");
            return;
        }

        int productId;
        boolean productVisibility;
        try {
            productId = Integer.parseInt(dto.getProductID());
            productVisibility = dto.isVisibility();
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid number format.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            ProductDetails product = session.get(ProductDetails.class, productId);
            if (product == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "Product not found.");
                return;
            }
            product.setProductVisibility(productVisibility);
            session.update(product);
            tx.commit();
            jrrp.jsonResponseProcess(response, 200, true, null, "Product updated successfully.");
        } catch (Exception e) {
            throw new ServletException("Error updating product", e);
        }
    }

}
