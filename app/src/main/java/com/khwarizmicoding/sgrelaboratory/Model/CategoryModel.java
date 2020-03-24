package com.khwarizmicoding.sgrelaboratory.Model;

import com.squareup.moshi.Json;

public class CategoryModel {

    @Json(name = "id_category")
    private int idCategory;
    @Json(name = "name_category")
    private String nameCategory;
    @Json(name = "image_category")
    private String imageCategory;

    public int getIdCategory() {
        return idCategory;
    }
    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }
    public String getNameCategory() {
        return nameCategory;
    }
    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }
    public String getImageCategory() {
        return imageCategory;
    }
    public void setImageCategory(String imageCategory) {
        this.imageCategory = imageCategory;
    }

}
