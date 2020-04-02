package dz.univbechar.sgrelaboratory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dz.univbechar.sgrelaboratory.Model.ProductModel;
import dz.univbechar.sgrelaboratory.R;

public class LoadingProductAdapter extends RecyclerView.Adapter<LoadingProductAdapter.ViewHolder> {

    private ProductModel[] productModel;

    public LoadingProductAdapter(ProductModel[] productModel){
        this.productModel = productModel;
    }

    @NonNull
    @Override
    public LoadingProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_loading_product,viewGroup,false);
        return new LoadingProductAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LoadingProductAdapter.ViewHolder holder, int position) {}
    @Override
    public int getItemCount() {
        return productModel.length;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
