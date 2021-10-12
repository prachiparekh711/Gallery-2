package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity.MainActivity;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity.ViewAlbumActivity;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.CameraInterface;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Model.BaseModel;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class    FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements Filterable {


    ArrayList<BaseModel> objects = new ArrayList<>();
    Activity activity;
    ArrayList<BaseModel> filteredList = new ArrayList<>();
    CameraInterface anInterface;

    private FirebaseAnalytics mFirebaseAnalytics;

    public FolderAdapter(Activity activity,CameraInterface anInterface){
        this.activity = activity;
        this.anInterface=anInterface;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case 0: {
                if(Utils.VIEW_TYPE.equals("Grid")){
                    itemView = LayoutInflater.from(activity).inflate(R.layout.folder_grid_view,parent,false);
                    viewHolder = new MyClassView(itemView);
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    if (params != null) {
                        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                        int width = wm.getDefaultDisplay().getWidth();
                        params.height = width / Utils.COLUMN;
                    }
                }else if(Utils.VIEW_TYPE.equals("List")){
                    itemView = LayoutInflater.from(activity).inflate(R.layout.folder_list_view,parent,false);
                    viewHolder = new MyClassView1(itemView);
                }
            }
            break;
            case 1:{
                if(Utils.VIEW_TYPE.equals("Grid")){
                    itemView = LayoutInflater.from(activity).inflate(R.layout.camera_grid_view,parent,false);
                    viewHolder = new ViewHolderAlbum(itemView);
                    ViewGroup.LayoutParams params = itemView.getLayoutParams();
                    if (params != null) {
                        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                        int width = wm.getDefaultDisplay().getWidth();
                        params.height = width / Utils.COLUMN;
                    }
                }else if(Utils.VIEW_TYPE.equals("List")){
                    itemView = LayoutInflater.from(activity).inflate(R.layout.camera_list_view,parent,false);
                    viewHolder = new ViewHolderAlbum1(itemView);
                }
            }
            break;
        }


        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holders,int position){
        RequestOptions options = new RequestOptions();
        float radius = activity.getResources().getDimension(R.dimen.my_value);
        switch (holders.getItemViewType()) {
            case 0: {
                if(Utils.VIEW_TYPE.equals("Grid")){
                        MyClassView holder = (MyClassView)holders;
                        BaseModel item = filteredList.get(position);
                        holder.mFavourite.setVisibility(View.GONE);
                        holder.mImage.setClipToOutline(true);
                        holder.mImage.setAdjustViewBounds(true);
                        holder.mImage.setOnTouchListener(new View.OnTouchListener(){
                            @Override
                            public boolean onTouch(View v,MotionEvent event){
                                MainActivity.Disablecard();
                                return false;
                            }
                        });

                        if(item.getPathlist().size() > 0){
                            Uri uri = Uri.fromFile(new File(item.getPathlist().get(0)));
//                            Glide.with(activity)
//                                    .load(uri)
//                                    .apply(options.centerCrop()
//                                            .skipMemoryCache(true)
//                                            .priority(Priority.LOW)
//                                            .format(DecodeFormat.PREFER_ARGB_8888))
//                                    .transition(withCrossFade())
//                                    .transition(new DrawableTransitionOptions().crossFade(500))
//                                    .into(holder.mImage);

                            try{
                                Glide.with(activity)
                                        .load(uri)
                                        .apply(options.centerCrop()
                                                .skipMemoryCache(true)
                                                .priority(Priority.LOW)
                                                .format(DecodeFormat.PREFER_ARGB_8888))
                                        .into(holder.mImage);
                            } catch (Exception e){
                                Glide.with(activity)
                                        .load(uri)
                                        .apply(options.centerCrop()
                                                .skipMemoryCache(true)
                                                .priority(Priority.LOW))
                                        .into(holder.mImage);
                            }
                            holder.mImage.setShapeAppearanceModel(holder.mImage.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopRightCorner(CornerFamily.ROUNDED,radius)
                                    .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                    .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                                    .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                                    .build());
                        }

                        holder.mAlbumName.setText(item.getBucketName());
                        holder.mCount.setText(item.getPathlist().size() + " Photos");


                        holder.mImage.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                fireAnalytics("Folder_of_image", "Selected");
                                MainActivity.Disablecard();
                                ViewAlbumActivity viewAlbumActivity = new ViewAlbumActivity();
                                viewAlbumActivity.SaveList(item);
                                Intent in = new Intent(activity,ViewAlbumActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(in);
                            }
                        });
                    }else if(Utils.VIEW_TYPE.equals("List")){
                        MyClassView1 holder = (MyClassView1)holders;
                        BaseModel item = filteredList.get(position);
                        holder.mFavourite.setVisibility(View.GONE);
                        holder.rootRL.setOnTouchListener(new View.OnTouchListener(){
                            @Override
                            public boolean onTouch(View v,MotionEvent event){
                                MainActivity.Disablecard();
                                return false;
                            }
                        });
                        holder.mImage.setClipToOutline(true);

                        if(item.getPathlist().size() > 0){
                            Uri uri = Uri.fromFile(new File(item.getPathlist().get(0)));
                            Glide.with(activity)
                                    .load(uri)
                                    .apply(options.centerCrop()
                                            .skipMemoryCache(true)
                                            .priority(Priority.LOW)
                                            .format(DecodeFormat.PREFER_ARGB_8888))
                                    .into(holder.mImage);
                            holder.mImage.setShapeAppearanceModel(holder.mImage.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopRightCorner(CornerFamily.ROUNDED,radius)
                                    .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                    .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                                    .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                                    .build());
                        }
                        holder.mAlbumName.setText(item.getBucketName());
                        holder.mCount.setText(item.getPathlist().size() + " Photos");
                        holder.rootRL.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                fireAnalytics("album_view", "Selected");
                                MainActivity.Disablecard();
                                ViewAlbumActivity viewAlbumActivity = new ViewAlbumActivity();
                                viewAlbumActivity.SaveList(item);
                                Intent in = new Intent(activity,ViewAlbumActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(in);
                            }
                        });
                    }
                }
                break;
                case 1: {
                    try{
                        if(Utils.VIEW_TYPE.equals("Grid")){
                            ViewHolderAlbum viewHolderAlbum = (ViewHolderAlbum)holders;

                            viewHolderAlbum.mImage.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    MainActivity.Disablecard();
                                    anInterface.onCameraClick();
                                }
                            });
                            viewHolderAlbum.mImage.setOnTouchListener(new View.OnTouchListener(){
                                @Override
                                public boolean onTouch(View v,MotionEvent event){
                                    MainActivity.Disablecard();
                                    return false;
                                }
                            });


                        }else if(Utils.VIEW_TYPE.equals("List")){
                            ViewHolderAlbum1 viewHolderAlbum = (ViewHolderAlbum1)holders;
                            viewHolderAlbum.rltrootview.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view){
                                    MainActivity.Disablecard();
                                    anInterface.onCameraClick();
                                }
                            });
                            viewHolderAlbum.rltrootview.setOnTouchListener(new View.OnTouchListener(){
                                @Override
                                public boolean onTouch(View v,MotionEvent event){
                                    MainActivity.Disablecard();
                                    return false;
                                }
                            });
                        }
                    }catch(Exception e){
                    }
                }
                break;

            }


    }


    @Override
    public int getItemCount(){
        return filteredList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int val = 0;
        try {
            if (filteredList != null && filteredList.size() > 0) {
                val = filteredList.get(position).getType();
            } else {
                val = 0;
            }
        } catch (Exception w) {
        }
        return val;
    }

    public class MyClassView extends RecyclerView.ViewHolder{

//        Grid
        ShapeableImageView mImage;
        TextView mAlbumName, mCount;
        ImageView mFavourite;


        public MyClassView(@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mAlbumName = itemView.findViewById(R.id.albumName);
            mCount = itemView.findViewById(R.id.count);
            mFavourite=itemView.findViewById(R.id.mFavourite);

        }
    }

    public class MyClassView1 extends RecyclerView.ViewHolder{

//        List
        ShapeableImageView mImage;
        TextView mAlbumName, mCount;
        RelativeLayout rootRL;
        ImageView mFavourite;

        public MyClassView1(@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mAlbumName = itemView.findViewById(R.id.mFolderName);
            mCount = itemView.findViewById(R.id.mCount);
            rootRL = itemView.findViewById(R.id.rootRL);
            mFavourite = itemView.findViewById(R.id.mFavourite);
        }
    }

    public static class ViewHolderAlbum extends RecyclerView.ViewHolder {

        public ImageView mImage;

        public ViewHolderAlbum(View view) {
            super(view);
            mImage =  view.findViewById(R.id.mImage);
        }
    }

    public static class ViewHolderAlbum1 extends RecyclerView.ViewHolder {
        public RelativeLayout rltrootview;
        public ImageView mImage;

        public ViewHolderAlbum1(View view) {
            super(view);
            rltrootview = (RelativeLayout) view.findViewById(R.id.rlRootview);
            mImage =  view.findViewById(R.id.mImage);
        }
    }

    public void Addall(ArrayList<BaseModel> itemdata) {

        objects = new ArrayList<>();
        objects.addAll(itemdata);
        filteredList = new ArrayList<>();
        filteredList.addAll(itemdata);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = objects;
                } else {
                    ArrayList<BaseModel> filteredList1 = new ArrayList<>();
                    for (BaseModel row : objects) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBucketName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList1.add(row);
                        }
                    }

                    filteredList = filteredList1;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<BaseModel>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }
}
