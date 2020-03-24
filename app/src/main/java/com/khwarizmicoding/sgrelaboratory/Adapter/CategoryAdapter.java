package com.khwarizmicoding.sgrelaboratory.Adapter;

import com.github.siyamed.shapeimageview.RoundedImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.khwarizmicoding.sgrelaboratory.Model.CategoryPost;
import com.khwarizmicoding.sgrelaboratory.R;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements OnCategoryListener{

    private CategoryPost categoryPost;
    private Context context;

    public CategoryAdapter(Context context, CategoryPost categoryPost) {
        this.categoryPost=categoryPost;
        this.context=context;
    }
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_items,viewGroup,false);
        return new CategoryAdapter.ViewHolder(view, this);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.idCategory.setText(Integer.toString(categoryPost.getData().get(i).getIdCategory()));
        viewHolder.nameCategory.setText(categoryPost.getData().get(i).getNameCategory());
        Picasso.get().load(categoryPost.getData().get(i).getImageCategory()).into(viewHolder.imageCategory);
    }
    @Override
    public int getItemCount() {
        return categoryPost.getData().size();
    }
    @Override
    public void itemCategoryClick(int position, RoundedImageView roundedImageView) {
        roundedImageView.setBorderColor(R.color.colorObject);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageCategory;
        private TextView idCategory,nameCategory;

        public ViewHolder(@NonNull View itemView, final OnCategoryListener onCategoryListener) {
            super(itemView);
            idCategory = (TextView) itemView.findViewById(R.id.idCategory);
            nameCategory = (TextView) itemView.findViewById(R.id.nameCategory);
            imageCategory = (RoundedImageView) itemView.findViewById(R.id.imageCategory);

            itemView.setOnClickListener((v) ->{
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) onCategoryListener.itemCategoryClick(position, imageCategory);
            });
        }
    }
}
interface OnCategoryListener{
    void itemCategoryClick(int position, RoundedImageView imageCategory);
}
