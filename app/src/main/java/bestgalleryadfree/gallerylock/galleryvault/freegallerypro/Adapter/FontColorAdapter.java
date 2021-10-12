package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.ColorInterface;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;

public class FontColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity context;
    int[] data;
    Typeface myTypeface;
    ColorInterface colorInterface;


    public FontColorAdapter(Activity context,   int[] data,ColorInterface colorInterface) {
        this.context = context;
        this.data = data;
        this.colorInterface=colorInterface;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_view, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mCardColor.setCardBackgroundColor(data[position]);
        viewHolder.mCardColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorInterface.ColorCode(data[position]);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mCardColor;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardColor =  itemView.findViewById(R.id.mCardColor);

        }
    }
}

