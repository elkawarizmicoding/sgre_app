package dz.univbechar.sgrelaboratory.Adapter;

import com.github.siyamed.shapeimageview.RoundedImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dz.univbechar.sgrelaboratory.Model.CategoryRep;
import dz.univbechar.sgrelaboratory.R;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements OnCategoryListener{

    private CategoryRep categoryRep;
    private Context context;

    public CategoryAdapter(Context context, CategoryRep categoryRep) {
        this.categoryRep = categoryRep;
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
        viewHolder.idCategory.setText(Integer.toString(categoryRep.getData().get(i).getIdCategory()));
        viewHolder.nameCategory.setText(categoryRep.getData().get(i).getNameCategory());
        Picasso.get().load(categoryRep.getData().get(i).getImageCategory()).into(viewHolder.imageCategory);
    }
    @Override
    public int getItemCount() {
        return categoryRep.getData().size();
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
