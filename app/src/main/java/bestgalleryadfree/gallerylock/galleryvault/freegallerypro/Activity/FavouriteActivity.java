package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.FavouriteAdapter;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity{


    ArrayList<String> mFavouriteImageList = new ArrayList<>();
    String json1;
    RecyclerView mImageRec;
    FavouriteAdapter favouriteAdapter;
    ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        mBack=findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("Favourites_pref",MODE_PRIVATE);

        mImageRec = findViewById(R.id.images_grid);
        Gson gson = new Gson();
        mFavouriteImageList = new ArrayList<>();
        json1 = sharedPreferences.getString("Fav_Image","");
        Type type1 = new TypeToken<ArrayList<String>>(){        }.getType();
        mFavouriteImageList = gson.fromJson(json1,type1);


        if(mFavouriteImageList!=null && mFavouriteImageList.size() > 0){
//            for(int i = 0;i < mFavouriteImageList.size();i++){
//                Log.e("Fav Image:",mFavouriteImageList.get(i));
//            }
            favouriteAdapter = new FavouriteAdapter(FavouriteActivity.this);
            if(Utils.VIEW_TYPE.equals("Grid")){
                mImageRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
                mImageRec.setLayoutAnimation(null);
            }else if(Utils.VIEW_TYPE.equals("List")){
                mImageRec.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.VERTICAL,false));
            }

            mImageRec.setAdapter(favouriteAdapter);

            favouriteAdapter.Addall(mFavouriteImageList);
            favouriteAdapter.notifyDataSetChanged();

        }
    }
}