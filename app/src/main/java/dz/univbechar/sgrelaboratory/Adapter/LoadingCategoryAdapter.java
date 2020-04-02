package dz.univbechar.sgrelaboratory.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dz.univbechar.sgrelaboratory.Model.CategoryModel;
import dz.univbechar.sgrelaboratory.R;

public class LoadingCategoryAdapter extends RecyclerView.Adapter<LoadingCategoryAdapter.ViewHolder> {

    private CategoryModel[] categoryModel;

    public LoadingCategoryAdapter(CategoryModel[] categoryModel){
        this.categoryModel = categoryModel;
    }

    @NonNull
    @Override
    public LoadingCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_loading_category,viewGroup,false);
        return new LoadingCategoryAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LoadingCategoryAdapter.ViewHolder holder, int position) {}
    @Override
    public int getItemCount() {
        return categoryModel.length;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
