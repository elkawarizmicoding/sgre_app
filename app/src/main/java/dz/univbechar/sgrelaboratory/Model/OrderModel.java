package dz.univbechar.sgrelaboratory.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderModel {
    private @SerializedName("name_theme") String nameTheme;
    private @SerializedName("name_teachers") String nameTeachers;
    private @SerializedName("name_students") String nameStudents;
    private @SerializedName("data_select") List<ProductSelect> data;

    public String getNameTheme() {
        return nameTheme;
    }

    public void setNameTheme(String nameTheme) {
        this.nameTheme = nameTheme;
    }

    public String getNameTeachers() {
        return nameTeachers;
    }

    public void setNameTeachers(String nameTeachers) {
        this.nameTeachers = nameTeachers;
    }

    public String getNameStudents() {
        return nameStudents;
    }

    public void setNameStudents(String nameStudents) {
        this.nameStudents = nameStudents;
    }

    public List<ProductSelect> getData() {
        return data;
    }

    public void setData(List<ProductSelect> data) {
        this.data = data;
    }
}
