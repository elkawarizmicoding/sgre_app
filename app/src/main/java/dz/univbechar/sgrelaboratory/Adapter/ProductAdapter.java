package dz.univbechar.sgrelaboratory.Adapter;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import dz.univbechar.sgrelaboratory.Model.CartModel;
import dz.univbechar.sgrelaboratory.Model.CartAction;
import dz.univbechar.sgrelaboratory.Model.ProductRep;
import dz.univbechar.sgrelaboratory.R;
import dz.univbechar.sgrelaboratory.TokenManager;
import com.github.siyamed.shapeimageview.RoundedImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements OnProductListener {

    private ProductRep productRep;
    private Context context;
    private TokenManager tokenManager;
    private CartAction cartAction;
    private CartModel cartModel;
    private TextView countCartView;

    public ProductAdapter(Context context, ProductRep productRep, TokenManager tokenManager, TextView countCartView) {
        this.productRep = productRep;
        this.context=context;
        this.tokenManager=tokenManager;
        this.countCartView=countCartView;
    }
    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_items,viewGroup,false);
        return new ProductAdapter.ViewHolder(view, this);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder viewHolder, int i) {
        viewHolder.idProduct.setText(Integer.toString(productRep.getData().get(i).getIdProduct()));
        viewHolder.nameProduct.setText(productRep.getData().get(i).getNameProduct());
        viewHolder.categoryName.setText(productRep.getData().get(i).getNameCategory());
        viewHolder.quantityProduct.setText(Integer.toString(productRep.getData().get(i).getQuantityProduct()));
        Picasso.get().load(productRep.getData().get(i).getImageProduct()).into(viewHolder.imageProduct);
    }
    @Override
    public int getItemCount() {
        return productRep.getData().size();
    }
    @Override
    public void itemProductClick(View itemView, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                context, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_card_product, (LinearLayout)itemView.findViewById(R.id.layoutCardProductInfo));
        TextView cardIdProduct=(TextView)bottomSheetView.findViewById(R.id.cardIdProduct);
        TextView cardNameProduct=(TextView)bottomSheetView.findViewById(R.id.cardNameProduct);
        TextView cardDescriptionProduct=(TextView)bottomSheetView.findViewById(R.id.cardDescriptionProduct);
        TextView cardCategoryName=(TextView)bottomSheetView.findViewById(R.id.cardCategoryName);
        TextView cardQuantityProduct=(TextView)bottomSheetView.findViewById(R.id.cardQuantityProduct);
        RoundedImageView cardImageProduct=(RoundedImageView)bottomSheetView.findViewById(R.id.cardImageProduct);
        cardIdProduct.setText(Integer.toString(productRep.getData().get(position).getIdProduct()));
        cardNameProduct.setText(productRep.getData().get(position).getNameProduct());
        cardDescriptionProduct.setText(productRep.getData().get(position).getDescriptionProduct());
        cardCategoryName.setText(productRep.getData().get(position).getNameCategory());
        cardQuantityProduct.setText(Integer.toString(productRep.getData().get(position).getQuantityProduct()));
        Picasso.get().load(productRep.getData().get(position).getImageProduct()).into(cardImageProduct);
        bottomSheetView.findViewById(R.id.btnAddToCart).setOnClickListener((v) -> {
            onAddItemToCart(position);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    @Override
    public void actionProductClick(int position) {
        onAddItemToCart(position);
    }
    private void onAddItemToCart(int position){
        Gson gson = new Gson();
        cartAction = tokenManager.getCartList() != null ? gson.fromJson(tokenManager.getCartList(), CartAction.class) : new CartAction();
        boolean isExits = false, isCount = false;
        for (CartModel cartModel: cartAction.getCartModel()) {
            if(cartModel.getProductModel().getIdProduct() == productRep.getData().get(position).getIdProduct()){
                if(cartModel.getCount() > 11) isCount = true;
                cartModel.setCount(cartModel.getCount()+1);
                this.cartModel=cartModel;
                isExits = true;
                break;
            }
        }
        if(!isExits){
            cartModel = new CartModel();
            cartModel.setProductModel(productRep.getData().get(position));
            cartModel.setCount(1);
            cartAction.putCartModel(cartModel);
        }
        if(!isCount){
            String cartPostJson = gson.toJson(cartAction);
            tokenManager.setCartList(cartPostJson);
            countCartView.setText(Integer.toString(cartAction.getCartModel().size()));
        }
        if(cartAction.getCartModel().size() == 1) countCartView.setVisibility(View.VISIBLE);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView imageProduct;
        private ImageView addProduct;
        private TextView idProduct,nameProduct,quantityProduct,categoryName;
        public ViewHolder(@NonNull View itemView, final OnProductListener onProductListener) {
            super(itemView);
            idProduct=(TextView)itemView.findViewById(R.id.idProduct);
            nameProduct=(TextView)itemView.findViewById(R.id.nameProduct);
            categoryName=(TextView)itemView.findViewById(R.id.categoryName);
            quantityProduct=(TextView)itemView.findViewById(R.id.quantityProduct);
            imageProduct=(CircularImageView)itemView.findViewById(R.id.imageProduct);
            addProduct=(ImageView)itemView.findViewById(R.id.addProduct);
            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) onProductListener.itemProductClick(itemView, position);
            });
            addProduct.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) onProductListener.actionProductClick(position);
            });
        }
    }
}
interface OnProductListener {
    void itemProductClick(View itemView, int position);
    void actionProductClick(int position);
}
