package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Fragment.FilterListFragment;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.ColorInterface;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.FontStyleInterface;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.FontColorAdapter;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.FontStyleAdapter;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.CallStickerView;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.SquaredFrameLayout;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.CustomViewPager;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.RotateTransformation;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, FilterListFragment.FiltersListFragmentListener {

    ImageView mBack;
    LinearLayout mFilter, mCrop, mText, mFrame;
    RelativeLayout mEditLayer, mTopLayer;
    ImageView mClose, mDone;
    RelativeLayout mFilterRL, mCropRL;
    LinearLayout mTextRL;
    LinearLayout mBottomLayer;
    ImageView mEditCrop, mEditRotate, mEditVertical, mEditHorizontal;
    LinearLayout mAddTextLL, mTextStyleLL, mTextColorLL;
    CardView mCancel, mOk;
    TextView mDialogTitle;
    CustomViewPager mFilterPager;
    FilterListFragment filtersListFragment;
    GestureImageView mImage;
    Bitmap mFilterBitmap;
    EditText mAddedText;
    String newText="";
    CallStickerView callStickerView;
    protected StickerView stickerView;
    SquaredFrameLayout mMainFrame;
    String mStyleList[];
    int[] colors;
    RecyclerView mStyleRecycler;
    FontColorAdapter mAdapter;
    FontStyleAdapter mFontAdapter;
    ColorInterface colorInterface;
    FontStyleInterface fontStyleInterface;
    MediaScannerConnection msConn;
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        init();

        InrializeFontArrayList("textfonts");
        IntializeColorArrayList();

        fontStyleInterface = new FontStyleInterface() {
            @Override
            public void Font(Typeface typeface) {
                Sticker sticker = callStickerView.GetStickerView();
                if (sticker != null) {
                    ((TextSticker) sticker).setTypeface(typeface);
                    callStickerView.UpdateStickerDetail(sticker);
                } else {
                    Toast.makeText(getApplicationContext(), "Click on text to apply font", Toast.LENGTH_LONG).show();
                }
            }
        };

        colorInterface = new ColorInterface() {
            @Override
            public void ColorCode(int color) {

                Sticker sticker = callStickerView.GetStickerView();
                if (sticker != null) {
                    ((TextSticker) sticker).setTextColor(color);
                    callStickerView.UpdateStickerDetail(sticker);
                } else {
                    Toast.makeText(getApplicationContext(), "Click on text to fill color", Toast.LENGTH_LONG).show();
                }
            }
        };
        mFontAdapter = new FontStyleAdapter(EditActivity.this, mStyleList, fontStyleInterface);
        mAdapter = new FontColorAdapter(EditActivity.this, colors, colorInterface);


    }

    public Bitmap BitmapFromPath(String path){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(path,bmOptions);
    }

    public void init(){
        mImage=findViewById(R.id.mImage);
        mMainFrame=findViewById(R.id.mMainframe);

        mEditLayer = findViewById(R.id.mEditLayer);
        mTopLayer = findViewById(R.id.mTopLayer);
        mBottomLayer = findViewById(R.id.mBottomLayer);
        mFilterRL = findViewById(R.id.mFilterRL);
        mCropRL = findViewById(R.id.mCropRL);
        mTextRL = findViewById(R.id.mTextRL);

        mClose = findViewById(R.id.mClose);
        mDone = findViewById(R.id.mDone);
        mClose.setOnClickListener(this);
        mDone.setOnClickListener(this);

        mBack = findViewById(R.id.back);
        mFilter = findViewById(R.id.mFilter);
        mCrop = findViewById(R.id.mCrop);
        mText = findViewById(R.id.mText);
        mFrame = findViewById(R.id.mFrame);

        mBack.setOnClickListener(this);
        mFilter.setOnClickListener(this);
        mCrop.setOnClickListener(this);
        mText.setOnClickListener(this);
        mFrame.setOnClickListener(this);

        mEditCrop = findViewById(R.id.mEditCrop);
        mEditCrop.setOnClickListener(this);
        mEditRotate = findViewById(R.id.mEditRotate);
        mEditRotate.setOnClickListener(this);
        mEditVertical = findViewById(R.id.mEditVertical);
        mEditVertical.setOnClickListener(this);
        mEditHorizontal = findViewById(R.id.mEditHorizontal);
        mEditHorizontal.setOnClickListener(this);

        mAddTextLL = findViewById(R.id.mAddText);
        mAddTextLL.setOnClickListener(this);
        mTextStyleLL = findViewById(R.id.mTextStyle);
        mTextStyleLL.setOnClickListener(this);
        mTextColorLL = findViewById(R.id.mTextColor);
        mTextColorLL.setOnClickListener(this);

        mFilterPager=findViewById(R.id.mFilterPager);

        stickerView = (StickerView) findViewById(R.id.sticker_view);
        stickerView.setOnClickListener(this);

        callStickerView = new CallStickerView(this, stickerView);
        callStickerView.IntializeStickerView();
        callStickerView.IntializeStickerEvent();
        callStickerView.ShowBorder();

        mStyleRecycler=findViewById(R.id.mStyleRec);
        mStyleRecycler.setVisibility(View.GONE);
        mStyleRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.IsFramed){
            Uri uri=Utils.mEditedURI;
            try {
                Utils.mEditedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                mFilterBitmap=Utils.mEditedBitmap;
                Glide.with(EditActivity.this)
                        .load(Utils.mEditedBitmap)
                        .into(mImage);
                Utils.IsFramed=false;
            } catch (IOException e) {
                Log.e("Enable to frame","!!!");
                e.printStackTrace();
            }
        }else{
            if(!Utils.IsCropped) {
                String mEditFile = Utils.mEditpath;
                Utils.mOriginalFile = new File(mEditFile);

                Glide.with(EditActivity.this)
                        .load(Utils.mOriginalFile.getPath())
                        .into(mImage);
                Utils.mOriginalBitmap = BitmapFromPath(Utils.mOriginalFile.getAbsolutePath());
                Utils.mEditedBitmap = Utils.mOriginalBitmap;
                mFilterBitmap = Utils.mOriginalBitmap;
            }
            if(!Utils.IsFramed) {

                Glide.with(EditActivity.this)
                        .load(Utils.mEditedBitmap)
                        .into(mImage);
                mFilterBitmap = Utils.mEditedBitmap;
            }
        }
    }


    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.back: {
                onBackPressed();
            }
            break;
            case R.id.mFilter: {
                fireAnalytics("Edit_Image", "Filter");
                mTopLayer.setVisibility(View.GONE);
                mEditLayer.setVisibility(View.VISIBLE);

                mFilterRL.setVisibility(View.VISIBLE);
                mCropRL.setVisibility(View.GONE);
                mTextRL.setVisibility(View.GONE);
                mBottomLayer.setVisibility(View.GONE);
                callStickerView.HideBorder();

                setupViewPager();
            }
            break;
            case R.id.mCrop: {
                fireAnalytics("Edit_Image", "Crop");
                mTopLayer.setVisibility(View.GONE);
                mEditLayer.setVisibility(View.VISIBLE);

                mCropRL.setVisibility(View.VISIBLE);
                mFilterRL.setVisibility(View.GONE);
                mTextRL.setVisibility(View.GONE);
                mBottomLayer.setVisibility(View.GONE);
                callStickerView.HideBorder();

            }
            break;
            case R.id.mText: {
                fireAnalytics("Edit_Image", "Text");
                mTopLayer.setVisibility(View.GONE);
                mEditLayer.setVisibility(View.VISIBLE);

                mCropRL.setVisibility(View.GONE);
                mFilterRL.setVisibility(View.GONE);
                mTextRL.setVisibility(View.VISIBLE);
                mBottomLayer.setVisibility(View.GONE);

                AddText();
            }
            break;
            case R.id.mFrame: {
                fireAnalytics("Edit_Image", "Frame");
                callStickerView.HideBorder();
                mMainFrame.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(mMainFrame.getDrawingCache());
                mMainFrame.setDrawingCacheEnabled(false);
                Utils.mEditedURI = getImageUri(EditActivity.this,bitmap);
                Intent in=new Intent(EditActivity.this, FrameActivity.class);
                startActivity(in);
                finish();
            }
            break;
            case R.id.mClose: {

                if(mEditLayer.getVisibility()==View.VISIBLE){
                    onBackPressed();
                }
                OriginalView();
            }
            break;
            case R.id.mDone: {
                OriginalView();
                new AsynchSaveImage().execute((Void[]) null);
            }
            break;
            case R.id.mEditCrop: {
                Utils.IsCropped=true;
                callStickerView.HideBorder();
                startCropImageActivity(getImageUri(EditActivity.this,Utils.mEditedBitmap));
            }
            break;
            case R.id.mEditRotate: {
                callStickerView.HideBorder();
                new AsynchRotateImage().execute((Void[]) null);
            }
            break;
            case R.id.mEditVertical: {
                callStickerView.HideBorder();
                Utils.mEditedBitmap=flipImage(Utils.mEditedBitmap,3);
                mFilterBitmap=Utils.mEditedBitmap;
                mImage.setImageBitmap(Utils.mEditedBitmap);
            }
            break;
            case R.id.mEditHorizontal: {
                callStickerView.HideBorder();
                Utils.mEditedBitmap=flipImage(Utils.mEditedBitmap,4);
                mFilterBitmap=Utils.mEditedBitmap;
                mImage.setImageBitmap(Utils.mEditedBitmap);
            }
            break;
            case R.id.mAddText:{
                mStyleRecycler.setVisibility(View.GONE);
                AddText();
            }
            break;
            case R.id.mTextStyle:{
                mStyleRecycler.setVisibility(View.VISIBLE);
                mStyleRecycler.setAdapter(mFontAdapter);
                mStyleRecycler.setItemAnimator(new DefaultItemAnimator());
            }
            break;
            case R.id.mTextColor:{
                mStyleRecycler.setVisibility(View.VISIBLE);
                mStyleRecycler.setAdapter(mAdapter);
                mStyleRecycler.setItemAnimator(new DefaultItemAnimator());
            }
            break;
            case R.id.sticker_view:{
                mTopLayer.setVisibility(View.GONE);
                mBottomLayer.setVisibility(View.GONE);
                mEditLayer.setVisibility(View.VISIBLE);
                mTextRL.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void InrializeFontArrayList(String dirFrom) {
        Resources res = getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        mStyleList = new String[0];
        try {
            mStyleList = am.list(dirFrom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void IntializeColorArrayList() {
        TypedArray ta = getResources().obtainTypedArray(R.array.rainbow);
        colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }
    }

    public Bitmap flipImage(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == 3) {

            matrix.preScale(1.0f,-1.0f);
        }
        // if horizonal
        else if(type == 4) {
            // x = x * -1
                matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    int CurrentAngle_Postition = -1;
    Integer Angle[] = new Integer[]{90, 180, -90, 0};
    public void rotate() {
        try {

                CurrentAngle_Postition--;
                if (CurrentAngle_Postition == -2) {
                    CurrentAngle_Postition = 2;
                }
                if (CurrentAngle_Postition < 0) {
                    CurrentAngle_Postition = 3;
                }


            Glide.with(EditActivity.this)
                    .asBitmap()
                    .load(Utils.mEditedBitmap)
                    .transform(new RotateTransformation(EditActivity.this, Angle[CurrentAngle_Postition]))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,@Nullable Transition<? super Bitmap> transition) {
                            mImage.setImageBitmap(resource);
                            Utils.mEditedBitmap=resource;
                            mFilterBitmap=Utils.mEditedBitmap;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        } catch (Exception e) {
            Log.e("Error:",e.getMessage());
        }
    }

    public class AsynchRotateImage extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog = new ProgressDialog(EditActivity.this);
                progressDialog.setMessage("Wait..");
                progressDialog.show();
            } catch (Exception e) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            rotate();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressDialog != null) {

                    if (!EditActivity.this.isFinishing() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }catch (Exception e){}
        }
    }

    public Uri getImageUri(Context inContext,Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAllowRotation(false)
                .start(this);
    }



    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // handle result of CropImageActivity
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Bitmap bitmap = null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    mImage.setImageBitmap(bitmap);
                    Utils.mEditedBitmap=bitmap;
                    mFilterBitmap=bitmap;

                }catch(IOException e){
                   Toast.makeText(EditActivity.this,"Problem in cropping image!!!",Toast.LENGTH_SHORT).show();
                }

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this,"Cropping failed: " + result.getError(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void OriginalView(){
        mEditLayer.setVisibility(View.VISIBLE);
        mBottomLayer.setVisibility(View.VISIBLE);
        mFilterRL.setVisibility(View.GONE);
        mCropRL.setVisibility(View.GONE);
        mTextRL.setVisibility(View.GONE);
        mStyleRecycler.setVisibility(View.GONE);
    }

    public class AsynchSaveImage extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                callStickerView.HideBorder();
                progressDialog = new ProgressDialog(EditActivity.this);
                progressDialog.setMessage("Wait..");
                progressDialog.show();
            } catch (Exception e) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mMainFrame.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(mMainFrame.getDrawingCache());
            mMainFrame.setDrawingCacheEnabled(false);
            Uri uri=getImageUri(EditActivity.this,bitmap);
            Utils.CaptureImage(uri,EditActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (progressDialog != null) {

                    if (!EditActivity.this.isFinishing() && progressDialog.isShowing()) {
                        progressDialog.dismiss();

                    }
                }
            }catch (Exception e){}
        }
    }


    private void setupViewPager() {
        mFilterPager.setVisibility(View.VISIBLE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FilterListFragment();
        filtersListFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "Filters");

        mFilterPager.setAdapter(adapter);
    }



    @Override
    public void onBackPressed(){
        if(mFilterRL.getVisibility()==View.VISIBLE || mCropRL.getVisibility()==View.VISIBLE || mTextRL.getVisibility()==View.VISIBLE || mStyleRecycler.getVisibility()==View.VISIBLE){
            Log.e("back on","OriginalView");
            OriginalView();
        }else{
            Log.e("back on","backpress super");
            Utils.IsCropped=false;
            super.onBackPressed();
        }
    }

    public void AddText(){


        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(EditActivity.this);
        final View view = factory.inflate(R.layout.text_dialog,null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCancel = view.findViewById(R.id.mCancel);
        mOk = view.findViewById(R.id.mDone);
        mAddedText = view.findViewById(R.id.mEditText);
        mDialogTitle = view.findViewById(R.id.t1);
        mDialogTitle.setText("Add text");
        mCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertadd.dismiss();
            }
        });

        mOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                newText=mAddedText.getText().toString();
                if(!newText.equals("")) {
                    callStickerView.AdTextViewSticker(newText, null);
                    callStickerView.ShowBorder();
                }
                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    @Override
    public void onFilterSelected(Filter filter){
       Bitmap bitmap=mFilterBitmap;
        final int maxSize = 960;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight =bitmap.getHeight();
        if(inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,outWidth,outHeight,true);

        Bitmap bitmap1 = filter.processFilter(bitmap);
        mImage.setImageBitmap(bitmap1);
        Utils.mEditedBitmap=bitmap1;
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }

    private FirebaseAnalytics mFirebaseAnalytics;
    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }
}