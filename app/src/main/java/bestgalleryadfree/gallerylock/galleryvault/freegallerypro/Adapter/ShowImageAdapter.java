package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity.ViewImageActivity;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.CustomViewPager;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.RotateTransformation;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ShowImageAdapter extends PagerAdapter{

    Activity _activity;
    public static ArrayList<String> filesPath;
    GestureImageView mImage;
    HashMap<Integer,GestureImageView> Rotate_ImageObject = new HashMap<>();
    Integer Angle[] = new Integer[]{90, 180, -90, 0};
    List<Integer> roratelist;
    int CurrentAngle_Postition = -1;
    CustomViewPager pager;
    ArrayList<String> mFavouriteImageList = new ArrayList<>();
    String json1;

    public ShowImageAdapter(Activity _activity,ArrayList<String> filesPath,CustomViewPager pager) {
        this._activity = _activity;
        this.filesPath = filesPath;
        this.pager = pager;
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position) {
        LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fullscreen_image, container, false);
        String FileListModel = filesPath.get(position);

        mImage = (GestureImageView) viewLayout.findViewById(R.id.mImage);
        mImage.getController().getSettings().setMaxZoom(3f).setDoubleTapZoom(3f);
        mImage.getController().getSettings()
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(true)
                .setRestrictRotation(true);

        Glide.with(_activity)
                .load(FileListModel)
                .override(900, 900)
                .into(mImage);

        Rotate_ImageObject.put(position, mImage);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            mImage.getController().enableScrollInViewPager(pager); //----after zoom image scrolling allow to another image*/
        }
        mImage.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v,MotionEvent event){
                ViewImageActivity.mRotateRL.setVisibility(View.GONE);
                ViewImageActivity.mOptionRL.setVisibility(View.GONE);
                return false;
            }
        });
        mImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ViewImageActivity.mRotateRL.setVisibility(View.GONE);
                ViewImageActivity.mOptionRL.setVisibility(View.GONE);
            }
        });

        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    @Override
    public int getCount() {
        try {
            if (filesPath != null && filesPath.size() > 0) {
                return filesPath.size();
            } else {
                return 0;
            }
        } catch (Exception w) {
            return 0;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    public void rotate(int type, int position) {
        try {
            if (Rotate_ImageObject != null && Rotate_ImageObject.size() > 0) {
                if (type == 1) {
                    CurrentAngle_Postition++;
                    if (CurrentAngle_Postition >= 4) {
                        CurrentAngle_Postition = 0;
                    }
                } else if (type == 2) {
                    CurrentAngle_Postition--;
                    //--------------not working then try remve this line firsttime left working------
                    if (CurrentAngle_Postition == -2) {
                        CurrentAngle_Postition = 2;
                    }
                    if (CurrentAngle_Postition < 0) {
                        CurrentAngle_Postition = 3;
                    }
                } else if (type == 3) {
                    roratelist = new ArrayList<>(Arrays.asList(Angle));
                    if (CurrentAngle_Postition >= 4) {
                        CurrentAngle_Postition = 0;
                    }
                    if (CurrentAngle_Postition == -1) {
                        CurrentAngle_Postition = 3;
                    }
                    if (Angle[CurrentAngle_Postition] == 180) {
                        CurrentAngle_Postition = roratelist.indexOf(0);
                    } else if (Angle[CurrentAngle_Postition] == 0) {
                        CurrentAngle_Postition = roratelist.indexOf(180);
                    } else if (Angle[CurrentAngle_Postition] == 90) {
                        CurrentAngle_Postition = roratelist.indexOf(-90);
                    } else if (Angle[CurrentAngle_Postition] == -90) {
                        CurrentAngle_Postition = roratelist.indexOf(90);
                    }
                }


                Glide.with(_activity)
                        .load(filesPath.get(position))
                        .transform(new RotateTransformation(_activity, Angle[CurrentAngle_Postition]))
                        .into(Rotate_ImageObject.get(position));
            } else {
                Toast.makeText(_activity, "Can't rotate.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    public void DeleteAction(int position,String from){
        if(from.equals("Album")) {
            File file = new File(filesPath.get(position));
            boolean isDelete = Utils.delete(_activity, file);
            if (!isDelete)
                isDelete = file.delete();
            if (isDelete) {
                filesPath.remove(position);
                if (position == filesPath.size() - 1) {
                    _activity.onBackPressed();
                } else {
                    notifyDataSetChanged();
                }

                if (filesPath.size() == 0)
                    _activity.onBackPressed();
                Toast.makeText(_activity, "Image Successfully deleted!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(_activity, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
            }
        }else if(from.equals("Favs")){
            SharedPreferences sharedPreferences = _activity.getSharedPreferences("Favourites_pref",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            json1 = sharedPreferences.getString("Fav_Image", "");
            Type type1 = new TypeToken<ArrayList<String>>() {
            }.getType();
            mFavouriteImageList = gson.fromJson(json1, type1);

            if (mFavouriteImageList == null) {
                mFavouriteImageList = new ArrayList<>();
            }

            mFavouriteImageList.remove(position);
            filesPath.remove(position);
            _activity.onBackPressed();

            json1 = gson.toJson(mFavouriteImageList);
            editor.putString("Fav_Image", json1);
            editor.apply();

        }
    }

    public void RefreshDeleteData(int currentItem) {
        //---remove single photo and update
        filesPath.remove(currentItem);
        notifyDataSetChanged();
    }
}
