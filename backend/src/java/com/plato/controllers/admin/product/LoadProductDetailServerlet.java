package com.plato.controllers.admin.product;

import com.plato.models.product.ProductDetails;
import com.plato.models.product.ProductDetailsHasTag;
import com.plato.models.product.SearchTag;
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
import java.util.Vector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ProductItem2 {

    private String id;
    private String title;
    private String author;
    private String description;
    private String category;
    private String categoryID;
    private String publisher;
    private String visibility;
    private String imgUrl;
    private Vector<String> searchTags;
    private String timestamp;
}

@WebServlet("/v1/admin/load-product-detail")
public class LoadProductDetailServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productIDStr = request.getParameter("productID");
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Data");
            return;
        }

        int productID;
        try {
            productID = Integer.parseInt(productIDStr);
        } catch (NumberFormatException e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid productID format");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ProductDetails product = new DbUtils().findOne(session, ProductDetails.class, "id", productID);
            if (product == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "Does Not Exist");
                return;
            }

            List<ProductDetailsHasTag> searchTags = new DbUtils()
                    .simpleSearch(session, ProductDetailsHasTag.class, "productDetails.id", productID)
                    .list();

            ProductItem2 productItem = new ProductItem2();
            productItem.setId(String.valueOf(product.getId()));
            productItem.setTitle(product.getTitle());
            productItem.setPublisher(product.getPublisher() != null ? product.getPublisher().getName() : null);
            productItem.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);
            productItem.setCategoryID(product.getCategory() != null ? String.valueOf(product.getCategory().getId()) : null);
            productItem.setDescription(product.getDescription());
            productItem.setVisibility(String.valueOf(product.isProductVisibility()));
            productItem.setAuthor(product.getAuthor() != null ? product.getAuthor().getName() : null);
            productItem.setImgUrl((product.getImageUrl() != null ? product.getImageUrl() : ""));
            productItem.setTimestamp(product.getDateTime() != null ? product.getDateTime().toString() : null);

            Vector<String> tags = new Vector<>();
            if (searchTags != null) {
                for (ProductDetailsHasTag tagRel : searchTags) {
                    if (tagRel.getSearchTag() != null) {
                        tags.add(tagRel.getSearchTag().getName());
                    }
                }
            }
            productItem.setSearchTags(tags);

            jrrp.jsonResponseProcess(response, 200, true, productItem, "Products retrieved successfully.");
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Error retrieving products: " + e.getMessage());
        }
    }

}
