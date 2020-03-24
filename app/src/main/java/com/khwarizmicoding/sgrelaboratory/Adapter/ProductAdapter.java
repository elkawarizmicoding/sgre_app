package com.khwarizmicoding.sgrelaboratory.Adapter;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.khwarizmicoding.sgrelaboratory.Model.ProductPost;
import com.khwarizmicoding.sgrelaboratory.R;
import com.github.siyamed.shapeimageview.RoundedImageView;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements OnProductListener {

    private ProductPost productPost;
    private Context context;
    ProgressDialog progressDialog = null;

    public ProductAdapter(Context context, ProductPost productPost) {
        this.productPost=productPost;
        this.context=context;
    }
    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_items,viewGroup,false);
        return new ProductAdapter.ViewHolder(view, this);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder viewHolder, int i) {
        viewHolder.idProduct.setText(Integer.toString(productPost.getData().get(i).getIdProduct()));
        viewHolder.nameProduct.setText(productPost.getData().get(i).getNameProduct());
        viewHolder.categoryName.setText(productPost.getData().get(i).getNameCategory());
        viewHolder.quantityProduct.setText(Integer.toString(productPost.getData().get(i).getQuantityProduct()));
        Picasso.get().load(productPost.getData().get(i).getImageProduct()).into(viewHolder.imageProduct);
    }
    @Override
    public int getItemCount() {
        return productPost.getData().size();
    }

    @Override
    public void itemProductClick(View itemView, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                context, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_card_product, (LinearLayout)itemView.findViewById(R.id.layoutCardProductInfo));
        //call id of layout card product
        TextView cardIdProduct=(TextView)bottomSheetView.findViewById(R.id.cardIdProduct);
        TextView cardNameProduct=(TextView)bottomSheetView.findViewById(R.id.cardNameProduct);
        TextView carddescriptionProduct=(TextView)bottomSheetView.findViewById(R.id.cardDescriptionProduct);
        TextView cardCategoryName=(TextView)bottomSheetView.findViewById(R.id.cardCategoryName);
        TextView cardQuantityProduct=(TextView)bottomSheetView.findViewById(R.id.cardQuantityProduct);
        RoundedImageView cardImageProduct=(RoundedImageView)bottomSheetView.findViewById(R.id.cardImageProduct);
        cardIdProduct.setText(Integer.toString(productPost.getData().get(position).getIdProduct()));
        cardNameProduct.setText(productPost.getData().get(position).getNameProduct());
        carddescriptionProduct.setText(productPost.getData().get(position).getDescriptionProduct());
        cardCategoryName.setText(productPost.getData().get(position).getNameCategory());
        cardQuantityProduct.setText(Integer.toString(productPost.getData().get(position).getQuantityProduct()));
        Picasso.get().load(productPost.getData().get(position).getImageProduct()).into(cardImageProduct);
        bottomSheetView.findViewById(R.id.btnAddToCart).setOnClickListener((v) -> {
            Toast.makeText(context, "Clicked True "+cardNameProduct.getText().toString().trim(), Toast.LENGTH_LONG).show();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    @Override
    public void actionProductClick(int position) {
        Toast.makeText(context, "Action: "+Integer.toString(position), Toast.LENGTH_LONG).show();
    }
    private void progressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    private void stopProgressDialog(){
        if(progressDialog != null) progressDialog.dismiss();
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
