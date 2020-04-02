package dz.univbechar.sgrelaboratory.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import dz.univbechar.sgrelaboratory.HomeActivity;
import dz.univbechar.sgrelaboratory.Model.CartModel;
import dz.univbechar.sgrelaboratory.Model.CartAction;
import dz.univbechar.sgrelaboratory.R;
import dz.univbechar.sgrelaboratory.TokenManager;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> implements OnCartListener{

    private CartAction cartAction;
    private Context context;
    private TokenManager tokenManager;
    private TextView countCartView;

    public CartAdapter(Context context, CartAction cartAction, TokenManager tokenManager, TextView countCartView) {
        this.cartAction = cartAction;
        this.context=context;
        this.tokenManager=tokenManager;
        this.countCartView=countCartView;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items,viewGroup,false);
        return new ViewHolder(view, this);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.cartNameProduct.setText(cartAction.getCartModel().get(i).getProductModel().getNameProduct());
        viewHolder.cartCategoryName.setText(cartAction.getCartModel().get(i).getProductModel().getNameCategory());
        viewHolder.countCart.setText(Integer.toString(cartAction.getCartModel().get(i).getCount()));
        Picasso.get().load(cartAction.getCartModel().get(i).getProductModel().getImageProduct()).into(viewHolder.cartImageProduct);
    }
    @Override
    public int getItemCount() {
        return cartAction.getCartModel().size();
    }
    @Override
    public void plusCartClick(TextView textView, int position) {
        Gson gson = new Gson();
        if(isCountAction(cartAction.getCartModel().get(position), textView, true)){
            String cartPostJson = gson.toJson(cartAction);
            tokenManager.setCartList(cartPostJson);
        }
    }
    @Override
    public void minCartClick(TextView textView, int position) {
        Gson gson = new Gson();
        if(isCountAction(cartAction.getCartModel().get(position), textView, false)){
            String cartPostJson = gson.toJson(cartAction);
            tokenManager.setCartList(cartPostJson);
        }
    }
    @Override
    public void deleteItemClick(int position) {
        Gson gson = new Gson();
        cartAction.getCartModel().remove(position);
        String cartPostJson = gson.toJson(cartAction);
        tokenManager.setCartList(cartPostJson);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartAction.getCartModel().size());
        countCartView.setText(Integer.toString(cartAction.getCartModel().size()));
        if(cartAction.getCartModel().size() == 0){
            tokenManager.deleteCart();
            progressDialog("Go to Home ...", true);
            countCartView.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.startActivity(new Intent(context, HomeActivity.class));
                }
            }, 1000);
        }
    }
    private void progressDialog(String text, boolean isLoading){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        TextView textLoad = (TextView) progressDialog.getWindow().findViewById(R.id.textLoad);
        textLoad.setText(text);
        if(!isLoading) {
            ProgressBar progressBar = (ProgressBar) progressDialog.getWindow().findViewById(R.id.progressLoading);
            progressBar.setVisibility(View.GONE);
            ImageView imageView = (ImageView) progressDialog.getWindow().findViewById(R.id.checkLoading);
        }
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private boolean isCountAction(CartModel cModel, TextView textView, boolean isAdd){
        int newCount = 0; boolean isCount = false;
        if(isAdd){
            if(cModel.getCount() < 10) {
                isCount  = true;
                newCount = cModel.getCount()+1;
                cModel.setCount(newCount);
            }
        } else{
            if(cModel.getCount() > 1) {
                isCount = true;
                newCount = cModel.getCount()-1;
                cModel.setCount(newCount);
            }
        }
        if(isCount) {
            textView.setText(Integer.toString(newCount));
            return true;
        }
        return false;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView cartImageProduct;
        private TextView cartNameProduct, cartCategoryName, countCart;
        private ImageView plusCart, minCart, deleteItemCart;
        public ViewHolder(@NonNull View itemView, final OnCartListener onCartListener) {
            super(itemView);
            cartNameProduct = (TextView) itemView.findViewById(R.id.cartNameProduct);
            cartCategoryName = (TextView) itemView.findViewById(R.id.cartCategoryName);
            countCart =  (TextView) itemView.findViewById(R.id.countCart);
            cartImageProduct = (CircularImageView) itemView.findViewById(R.id.cartImageProduct);
            plusCart = (ImageView) itemView.findViewById(R.id.plusCart);
            minCart = (ImageView) itemView.findViewById(R.id.minCart);
            deleteItemCart = (ImageView) itemView.findViewById(R.id.deleteItemCart);
            plusCart.setOnClickListener((v) ->{
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) onCartListener.plusCartClick(countCart, position);
            });
            minCart.setOnClickListener((v) ->{
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) onCartListener.minCartClick(countCart, position);
            });
            deleteItemCart.setOnClickListener((v) ->{
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) onCartListener.deleteItemClick(position);
            });
        }
    }
}

interface OnCartListener{
    void plusCartClick(TextView countCart, int position);
    void minCartClick(TextView countCart, int position);
    void deleteItemClick(int position);
}
