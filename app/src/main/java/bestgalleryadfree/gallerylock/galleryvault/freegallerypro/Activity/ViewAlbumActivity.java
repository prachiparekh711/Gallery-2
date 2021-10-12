package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.ImagesAdapter;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Model.BaseModel;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;

import java.util.ArrayList;

public class ViewAlbumActivity extends AppCompatActivity{

    RecyclerView mImageRec;
    ImagesAdapter imgAdapter;
    public static ArrayList<String> pathlist;
    public static String FolderPath;
    public static String Bucket_Id;
    public static String Title = "Photos";
    TextView mTitle;
    ImageView mBack;

    private FirebaseAnalytics mFirebaseAnalytics;

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_album);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ViewAlbumActivity.this);
        init();
        imgAdapter = new ImagesAdapter(ViewAlbumActivity.this);
        initView();
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(Utils.IsUpdate){
           initView();
        }
    }



    public void init(){
        mImageRec = findViewById(R.id.images_grid);

        mTitle = findViewById(R.id.mTitle);
        mBack = findViewById(R.id.back);
    }

    public void SaveList(BaseModel albumDetail) {
        pathlist = new ArrayList<>();
        pathlist = albumDetail.getPathlist();
        FolderPath = albumDetail.getBucketPath();
        Bucket_Id = albumDetail.getBucketId();
        Title = albumDetail.getBucketName();
    }

    private void initView() {
        mTitle.setText(Title);
        if(Utils.VIEW_TYPE.equals("Grid")){
            mImageRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
            mImageRec.setLayoutAnimation(null);
        }else if(Utils.VIEW_TYPE.equals("List")){
            mImageRec.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.VERTICAL,false));
        }

        mImageRec.setAdapter(imgAdapter);

        if (pathlist != null && pathlist.size() > 0) {
            imgAdapter.Addall(pathlist);
            imgAdapter.notifyDataSetChanged();
        }
    }
}