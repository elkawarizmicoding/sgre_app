package dz.univbechar.sgrelaboratory.Model;

import com.squareup.moshi.Json;

import java.util.HashMap;
import java.util.List;

public class OrderRep {
    @Json(name = "id")
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
