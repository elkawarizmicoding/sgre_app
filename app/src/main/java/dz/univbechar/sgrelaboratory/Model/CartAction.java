package dz.univbechar.sgrelaboratory.Model;

import java.util.ArrayList;
import java.util.List;

public class CartAction {
    private List<CartModel> cartModel;

    public CartAction(){
        cartModel = new ArrayList<>();
    }
    public void putCartModel(CartModel cartModel) {
        this.cartModel.add(cartModel);
    }
    public List<CartModel> getCartModel() {
        return cartModel;
    }
}
