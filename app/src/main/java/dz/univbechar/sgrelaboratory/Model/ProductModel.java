package dz.univbechar.sgrelaboratory.Model;

import com.squareup.moshi.Json;

public class ProductModel {

    @Json(name = "id_product")
    private int idProduct;
    @Json(name = "name_product")
    private String nameProduct;
    @Json(name = "description_product")
    private String descriptionProduct;
    @Json(name = "name_category")
    private String nameCategory;
    @Json(name = "quantity_product")
    private int quantityProduct;
    @Json(name = "image_product")
    private String imageProduct;

    public ProductModel(){}

    public int getIdProduct() {
        return idProduct;
    }
    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }
    public String getNameProduct() {
        return nameProduct;
    }
    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }
    public String getDescriptionProduct() {
        return descriptionProduct;
    }
    public void setDescriptionProduct(String descriptionProduct) {
        this.descriptionProduct = descriptionProduct;
    }
    public String getNameCategory() {
        return nameCategory;
    }
    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }
    public int getQuantityProduct() {
        return quantityProduct;
    }
    public void setQuantityProduct(int quantityProduct) {
        this.quantityProduct = quantityProduct;
    }
    public String getImageProduct() {
        return imageProduct;
    }
    public void setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
    }
}
