package com.plato.controllers.admin.product;

import com.plato.models.product.*;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet("/v1/admin/create-product")
@MultipartConfig
public class CreateProductServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String authorName = request.getParameter("author");
        String publisherName = request.getParameter("publisher");
        String categoryIdStr = request.getParameter("categoryId");
        String visibilityStr = request.getParameter("productVisibility");
        String tagsString = request.getParameter("tags");

        if (isEmpty(title) || isEmpty(description) || isEmpty(authorName) || isEmpty(publisherName) || isEmpty(categoryIdStr) || isEmpty(visibilityStr)) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Missing required fields.");
            return;
        }

        int categoryId;
        boolean productVisibility;
        try {
            categoryId = Integer.parseInt(categoryIdStr);
            productVisibility = Boolean.parseBoolean(visibilityStr);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid category ID or visibility.");
            return;
        }

        String imageUrl = null;
        Part imagePart = request.getPart("imageFile");
        if (imagePart != null && imagePart.getSize() > 0) {
            String originalName = imagePart.getSubmittedFileName();
            String extension = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();

            if (!Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension)) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid image type.");
                return;
            }

            String fileName = UUID.randomUUID() + "_" + originalName;
            File uploadsDir = new File("C:/product-images/");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }

            File file = new File(uploadsDir, fileName);
            try {
                Files.copy(imagePart.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imageUrl = "/product-images/" + fileName;
            } catch (IOException e) {
                jrrp.jsonResponseProcess(response, 500, false, null, "Image upload failed.");
                return;
            }
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Author author = findOrCreate(session, Author.class, "name", authorName);
            Publisher publisher = findOrCreate(session, Publisher.class, "name", publisherName);
            Category category = session.get(Category.class, categoryId);

            if (category == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "Category not found.");
                return;
            }

            ProductDetails product = new ProductDetails();
            product.setTitle(title);
            product.setDescription(description);
            product.setImageUrl(imageUrl);
            product.setAuthor(author);
            product.setPublisher(publisher);
            product.setCategory(category);
            product.setProductVisibility(productVisibility);
            product.setDateTime(new Timestamp(System.currentTimeMillis()));

            session.save(product);

            if (tagsString != null && !tagsString.trim().isEmpty()) {
                String[] tags = tagsString.split(",");
                for (String tag : tags) {
                    String trimmed = tag.trim();
                    if (!trimmed.isEmpty()) {
                        SearchTag tagEntity = findOrCreate(session, SearchTag.class, "name", trimmed);
                        ProductDetailsHasTag rel = new ProductDetailsHasTag();
                        rel.setProductDetails(product);
                        rel.setSearchTag(tagEntity);
                        session.save(rel);
                    }
                }
            }

            tx.commit();
            jrrp.jsonResponseProcess(response, 201, true, null, "Product created successfully.");
        } catch (Exception e) {
            throw new ServletException("Error saving product", e);
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private <T> T findOrCreate(Session session, Class<T> clazz, String field, String value) throws Exception {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.select(root).where(cb.equal(root.get(field), value));

        List<T> result = session.createQuery(cq).getResultList();
        if (!result.isEmpty()) {
            return result.get(0);
        }

        T entity = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("set" + capitalize(field), String.class).invoke(entity, value);
        session.save(entity);
        return entity;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
