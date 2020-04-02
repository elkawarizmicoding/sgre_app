package dz.univbechar.sgrelaboratory.Model;

import com.google.gson.annotations.SerializedName;

public class ProductSelect {
    private @SerializedName("id") int id;
    private @SerializedName("count") int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
