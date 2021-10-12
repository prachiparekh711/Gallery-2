package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity.ViewImageActivity;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

/**
 * Created by ravi on 23/10/17.
 */

public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.MyViewHolder> {

    private final List<ThumbnailItem> thumbnailItemList;
    private final ThumbnailsAdapterListener listener;
    private final Context mContext;
    private int selectedIndex = 0;

    private FirebaseAnalytics mFirebaseAnalytics;

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    public FilterViewAdapter(Context context,List<ThumbnailItem> thumbnailItemList,ThumbnailsAdapterListener listener) {
        mContext = context;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filter_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);

        Glide.with(mContext)
                .load(thumbnailItem.image)
                .into( holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireAnalytics("Filter_Apply", "Selected");
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public interface ThumbnailsAdapterListener {
        void onFilterSelected(Filter filter);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        GestureImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);

            thumbnail=view.findViewById(R.id.imgFilterView);
        }
    }
}