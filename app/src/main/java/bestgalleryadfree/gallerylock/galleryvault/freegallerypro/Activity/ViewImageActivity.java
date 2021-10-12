package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.FolderInterface;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.AccordionPageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.AlphaPageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.CubePageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.FadePageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.FlipPageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.RotatePageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.animation.ZoomCenterPageTransformer;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.FolderdialogAdapter;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.ShowImageAdapter;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Service.GetFileList;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Class.CustomViewPager;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ViewImageActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    ImageView mBack, mRotate, mFavourite, mMore;
    ImageView mSlideshow, mEdit, mDelete, mShare;
    public static RelativeLayout mRotateRL, mOptionRL;
    RelativeLayout mTopLayer;
    LinearLayout mBottomLayer;
    CustomViewPager mImagePager;
    TextView mRotateRight, mRightLeft, mRotateBottom;
    TextView mCopy, mMove, mRename, mPrint, mWallpaper, mInfo;
    public static ArrayList<String> list;
    public static int currenpositionToDisplay = 0;
    ShowImageAdapter imageAdapter;
    TextView mImageName;
    ArrayList<String> mFavouriteImageList = new ArrayList<>();
    String json1;
    Boolean exist = false;
    int pos;
    FolderdialogAdapter folderAdapter;
    RecyclerView mFolderRec;
    FolderInterface folderInterface;
    MediaScannerConnection msConn;
    Dialog dial,deleteDialog;
    CardView mCancel, mOk;
    TextView mDialogTitle;
    EditText mNewName;
    Timer swipeTimer;
    TextView mDeleteFile;
    CardView mDialogCancel,mDialogDelete;
    String From="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ViewImageActivity.this);
        init();
        initview();
        From=getIntent().getStringExtra("From");
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    public void init(){
        mImagePager = findViewById(R.id.mImagePager);

        mTopLayer = findViewById(R.id.mTopLayer);
        mTopLayer.setOnTouchListener(this);
        mBottomLayer = findViewById(R.id.mBottomLayer);
        mBottomLayer.setOnTouchListener(this);

        mBack = findViewById(R.id.back);
        mRotate = findViewById(R.id.mRotate);
        mFavourite = findViewById(R.id.mFavourite);
        mMore = findViewById(R.id.mMore);
        mSlideshow = findViewById(R.id.mSlideshow);
        mEdit = findViewById(R.id.mEdit);
        mDelete = findViewById(R.id.mDelete);
        mShare = findViewById(R.id.mShare);

        mBack.setOnClickListener(this);
        mRotate.setOnClickListener(this);
        mFavourite.setOnClickListener(this);
        mMore.setOnClickListener(this);
        mSlideshow.setOnClickListener(this);
        mEdit.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mShare.setOnClickListener(this);

        mRotateRL = findViewById(R.id.mRotateRL);
        mOptionRL = findViewById(R.id.mOptionRL);

        mRotateRight = findViewById(R.id.mRotateRight);
        mRightLeft = findViewById(R.id.mRotateLeft);
        mRotateBottom = findViewById(R.id.mRotateBottom);
        mRotateRight.setOnClickListener(this);
        mRightLeft.setOnClickListener(this);
        mRotateBottom.setOnClickListener(this);

        mCopy = findViewById(R.id.mCopy);
        mMove = findViewById(R.id.mMove);
        mRename = findViewById(R.id.mRename);
        mPrint = findViewById(R.id.mPrint);
        mWallpaper = findViewById(R.id.mWallpaper);
        mInfo = findViewById(R.id.mInfo);
        mCopy.setOnClickListener(this);
        mMove.setOnClickListener(this);
        mRename.setOnClickListener(this);
        mPrint.setOnClickListener(this);
        mWallpaper.setOnClickListener(this);
        mInfo.setOnClickListener(this);

        mImageName=findViewById(R.id.mImageName);
    }

    public void initview() {

        SharedPreferences sharedPreferences = getSharedPreferences("Favourites_pref",MODE_PRIVATE);
        Gson gson = new Gson();

        mFavouriteImageList = new ArrayList<>();
        json1 = sharedPreferences.getString("Fav_Image","");
        Type type1 = new TypeToken<ArrayList<String>>(){
        }.getType();
        mFavouriteImageList = gson.fromJson(json1,type1);

        if(mFavouriteImageList == null){
            mFavouriteImageList = new ArrayList<>();
        }

        System.gc();
        imageAdapter = new ShowImageAdapter(this, list,mImagePager);
        mImagePager.setAdapter(imageAdapter);
        mImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (list != null) {
                    File file= new File(list.get(position));
                    mImageName.setText((position + 1) + " / " + (list.size()));
                    if(mFavouriteImageList.size() > 0){
                        if(mFavouriteImageList.contains(file.getPath())){
                            mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_heart));
                        }else{
                            mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.white_heart));
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        File file= new File(list.get(currenpositionToDisplay));
        mImageName.setText((currenpositionToDisplay + 1) + " / " + (list.size()));
        if(mFavouriteImageList.size() > 0){
            if(mFavouriteImageList.contains(file.getPath())){
                mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_heart));
            }else{
                mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.white_heart));
            }
        }
        mImagePager.setCurrentItem(currenpositionToDisplay);
        folderInterface=new FolderInterface(){
            @Override
            public void OnSelectFolder(String action,String path){
                if(action.equals("copy")){
                    CopyImage(path);
                }else if(action.equals("move")){
                    MoveImage(path);
                }
                dial.dismiss();
            }
        };
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(Utils.IsUpdate){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    if (imageAdapter != null) {
                        imageAdapter.notifyDataSetChanged();
                    }
                    registerReceiver(DeleteCompletedBroadcast, new IntentFilter("DeleteCompleted"));
//                    initview();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        startForegroundService(new Intent(getApplicationContext(), GetFileList.class).putExtra("action", "album"));
//                    } else {
//                        startService(new Intent(getApplicationContext(), GetFileList.class).putExtra("action", "album"));
//                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (DeleteCompletedBroadcast != null) {
                unregisterReceiver(DeleteCompletedBroadcast);
            }
        } catch (Exception e) {
        }
    }

    private BroadcastReceiver DeleteCompletedBroadcast = new BroadcastReceiver() {

        public ProgressDialog pr_dialog;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null) {
                if (intent != null) {
                    if (intent.getBooleanExtra("started", false)) {
                        try {
                            pr_dialog = ProgressDialog.show(context, "Deleting",
                                    "Deleting files...",
                                    true, false);
                        } catch (Exception e) {
                        }
                    }

                    if (intent.getBooleanExtra("completed", false)) {
                        try {
                            //-----------------data refresh-----
                            if (imageAdapter != null) {
                                imageAdapter.RefreshDeleteData(mImagePager.getCurrentItem());
                            }
                            //----------------------------
                        } catch (Exception e) {
                        }
                        if (pr_dialog != null)
                            pr_dialog.dismiss();
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.back: {
                onBackPressed();
            }
            break;
            case R.id.mRotate: {
                fireAnalytics("Rotate_Image", "Selected");
                mRotateRL.setVisibility(View.VISIBLE);
                mOptionRL.setVisibility(View.GONE);
            }
            break;
            case R.id.mFavourite: {
                fireAnalytics("favourite_image", "Selected");
                SetAsFavourite();
            }
            break;
            case R.id.mMore: {
                mOptionRL.setVisibility(View.VISIBLE);
                mRotateRL.setVisibility(View.GONE);
            }
            break;
            case R.id.mSlideshow: {
                fireAnalytics("SlideShow", "Selected");
                mOptionRL.setVisibility(View.GONE);
                HideToolBar();
                StartTimer();
            }
            break;
            case R.id.mEdit: {
                fireAnalytics("Edit_Image", "Selected");
                mOptionRL.setVisibility(View.GONE);
                File mEditFile=new File(list.get(mImagePager.getCurrentItem()));
                Intent in = new Intent(ViewImageActivity.this,EditActivity.class);
                Utils.mEditpath=mEditFile.getPath();
                startActivity(in);
            }
            break;
            case R.id.mDelete: {
                fireAnalytics("Delete_image", "Selected");
                deleteDialog= new Dialog(ViewImageActivity.this, android.R.style.Theme_DeviceDefault);
                deleteDialog.requestWindowFeature(1);
                deleteDialog.setContentView(R.layout.delete_dialog);
                mDeleteFile = (TextView) deleteDialog.findViewById(R.id.txt_filename);
                mDialogCancel = (CardView) deleteDialog.findViewById(R.id.mCancel);
                mDialogDelete = (CardView) deleteDialog.findViewById(R.id.mDelete);
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                deleteDialog.setCanceledOnTouchOutside(true);
                mOptionRL.setVisibility(View.GONE);
                Utils.IsUpdate=true;
                int position=mImagePager.getCurrentItem();
                deleteDialog.show();
                File file=new File(list.get(position));
                mDeleteFile.setText(file.getName());
                mDialogDelete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        deleteDialog.dismiss();
                        imageAdapter.DeleteAction(position,From);
                    }
                });
                mDialogCancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        deleteDialog.dismiss();
                    }
                });
            }
            break;
            case R.id.mShare: {
                fireAnalytics("Share_image", "Selected");
                mOptionRL.setVisibility(View.GONE);

                String path;
                File file;
                Uri uri;

                path= list.get(mImagePager.getCurrentItem());
                file=new File(path);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(ViewImageActivity.this, getPackageName() + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
            }
            break;
            case R.id.mRotateRight: {
                fireAnalytics("rotate_right_image", "Selected");
                mRotateRL.setVisibility(View.GONE);
                imageAdapter.rotate(1,mImagePager.getCurrentItem());
            }
            break;
            case R.id.mRotateLeft: {
                fireAnalytics("rotate_left_image", "Selected");
                mRotateRL.setVisibility(View.GONE);
                imageAdapter.rotate(2,mImagePager.getCurrentItem());
            }
            break;
            case R.id.mRotateBottom: {
                fireAnalytics("rotate_bottom_image", "Selected");
                mRotateRL.setVisibility(View.GONE);
                imageAdapter.rotate(3,mImagePager.getCurrentItem());
            }
            break;
            case R.id.mCopy: {
                fireAnalytics("copy_image", "Selected");
                mOptionRL.setVisibility(View.GONE);
                FolderDialog("copy");
            }
            break;
            case R.id.mMove: {
                fireAnalytics("move_image", "Selected");
                mOptionRL.setVisibility(View.GONE);
                FolderDialog("move");
            }
            break;
            case R.id.mRename: {
                fireAnalytics("rename_image", "Selected");
                mOptionRL.setVisibility(View.GONE);
                RenameDialog();
            }
            break;
            case R.id.mPrint: {
                fireAnalytics("print_image", "Selected");
                mOptionRL.setVisibility(View.GONE);
                PrintDialog(mImagePager.getCurrentItem());
            }
            break;
            case R.id.mWallpaper: {
                fireAnalytics("setWallpaper", "Selected");
                mOptionRL.setVisibility(View.GONE);
                SetAsWallaper(mImagePager.getCurrentItem());
            }
            break;
            case R.id.mInfo: {
                fireAnalytics("Info_Image", "Selected");
                mOptionRL.setVisibility(View.GONE);
                FileInfo(mImagePager.getCurrentItem());
            }
            break;
        }
    }



    int currentPage=0;
    private void StartTimer() {
        try {
            if (folderAdapter != null) {
                // System.out.println("==>>>>>>"+pager.getCurrentItem());
                currentPage = (mImagePager.getCurrentItem() + 1); //--if use scroll manully after sttart slideshow thrn start continously
            }
            //----animation show so delay whule slideshow
            if (mImagePager != null) {
                mImagePager.setDurationScroll(3000);
            }
            currentPage=mImagePager.getCurrentItem();
            // Auto start of viewpager
            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {

                    if (list != null) {
                        if (currentPage == list.size()) {
                            currentPage = 0;
                            CancelSlideShow();
                            mImagePager.setCurrentItem(currentPage, true);
                        } else {

                            // SetAnimatePagerTransition();//--effect
                            mImagePager.setCurrentItem(currentPage++, true);
                            SetAnimatePagerTransition();//--effect
                        }
                    }

                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 0, 3000);
        } catch (Exception e) {
        }
    }

    public void CancelSlideShow() {

        try {
            if (swipeTimer != null) {
                swipeTimer.cancel();
                swipeTimer.purge();
                swipeTimer = null;
                mTopLayer.setVisibility(View.VISIBLE);
                mBottomLayer.setVisibility(View.VISIBLE);
            }
            //----animation stop for delay scrolling
            if (mImagePager != null) {
                mImagePager.setDurationScroll(500);
                if (counter > 0) {
                    mImagePager.setCurrentItem(currentPage--, false);
                } else {
                    mImagePager.setCurrentItem(0, false);
                }
                folderAdapter.notifyDataSetChanged();
                mImagePager.setPageTransformer(true, null);
            }
        } catch (Exception e) {
        }
    }

    public boolean IsSlideShowRunning() {
        if (swipeTimer != null) {
            return true;
        } else {
            return false;
        }
    }

    public void HideToolBar() {
        // toolbar.setVisibility(View.GONE);
        if (IsSlideShowRunning()) {
            mTopLayer.setVisibility(View.VISIBLE);
            mBottomLayer.setVisibility(View.VISIBLE);
        } else {
            mTopLayer.setVisibility(View.GONE);
            mBottomLayer.setVisibility(View.GONE);
        }
    }


    int counter = 0;
    private void SetAnimatePagerTransition() {
        if (counter >= 7) {
            counter = 0;
        }
        if (counter == 0) {
            mImagePager.setPageTransformer(true, new FadePageTransformer());
        } else if (counter == 1) {
            mImagePager.setPageTransformer(true, new CubePageTransformer());
        } else if (counter == 2) {
            mImagePager.setPageTransformer(true, new AccordionPageTransformer());
        } else if (counter == 3) {
            mImagePager.setPageTransformer(true, new RotatePageTransformer());
        } else if (counter == 4) {
            mImagePager.setPageTransformer(true, new ZoomCenterPageTransformer());
        } else if (counter == 5) {
            mImagePager.setPageTransformer(true, new AlphaPageTransformer());
        } else if (counter == 6) {
            mImagePager.setPageTransformer(true, new FlipPageTransformer());
        }
        counter++;
    }

    public void FileInfo(int position) {
        try {
            if (list != null && list.size() > 0) {
                File filePath = new File(list.get(position));

                final Dialog fileDetailsDialog = new Dialog(ViewImageActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                fileDetailsDialog.setContentView(R.layout.custom_file_details_dialog);
                fileDetailsDialog.show();

                final TextView lblFileName = (TextView) fileDetailsDialog.findViewById(R.id.id_name);
                final TextView lblFilePath = (TextView) fileDetailsDialog.findViewById(R.id.id_path);
                final TextView lblSize = (TextView) fileDetailsDialog.findViewById(R.id.id_size);
                final TextView lblCreateAt = (TextView) fileDetailsDialog.findViewById(R.id.id_create_at);

                // File file = new File(filePath);
                lblFileName.setText("Name : " + filePath.getName());
                lblFilePath.setText("Path : " + filePath.getPath());
                lblSize.setText("Size : " + Utils.getSize(filePath.length()));

                Date lastModDate = null;
                try {
                    lastModDate = new Date(filePath.lastModified());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    lblCreateAt.setText("Created on : " + Utils.convertTimeFromUnixTimeStamp(lastModDate.toString()));
                } catch (Exception e) {
                }

                Button btnOkay = (Button) fileDetailsDialog.findViewById(R.id.btn_okay);
                btnOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lblFileName.setText("");
                        lblFilePath.setText("");
                        lblSize.setText("");
                        lblCreateAt.setText("");
                        fileDetailsDialog.dismiss();
                    }
                });
            }
        } catch (Exception e) {
        }
    }



    public void SetAsWallaper(int postion) {
        try {
            if (list.get(postion).startsWith("https")) {
                Toast.makeText(ViewImageActivity.this, "A problem occur with this file!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                File file = new File(list.get(postion));
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(ViewImageActivity.this, getPackageName() + ".provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                // uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "image/jpeg");
                intent.putExtra("mimeType", "image/jpeg");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Set as:"));
            }
        } catch (Exception e) {
        }
    }

    public void PrintDialog(int position){
        if (list != null && list.size() > position) {
            String ext = new File(list.get(position)).getName();
            String sub_ext = ext.substring(ext.lastIndexOf(".") + 1);
            if (sub_ext.equalsIgnoreCase("jpg") || sub_ext.equalsIgnoreCase("jpeg") || sub_ext.equalsIgnoreCase("gif")
                    || sub_ext.equalsIgnoreCase("png")) {
                PrintHelper photoPrinter = new PrintHelper(ViewImageActivity.this);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                Bitmap bitmap = BitmapFactory.decodeFile(list.get(position));
                photoPrinter.printBitmap(new File(list.get(position)).getName(), bitmap);
            } else {
                Toast.makeText(ViewImageActivity.this, "Can't print file.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void RenameDialog(){
        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ViewImageActivity.this);
        final View view = factory.inflate(R.layout.text_dialog,null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCancel = view.findViewById(R.id.mCancel);
        mOk = view.findViewById(R.id.mDone);
        mDialogTitle=view.findViewById(R.id.t1);
        File from = new File(list.get(mImagePager.getCurrentItem()));
        mDialogTitle.setText("Rename " + from.getName() );
        mNewName=view.findViewById(R.id.mEditText);
        mCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertadd.dismiss();
            }
        });

        mOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mNewName.getText()!=null)
                    ChangeName(mNewName.getText().toString());
                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public void ChangeName(String newName){
        int position=mImagePager.getCurrentItem();
        File from = new File(list.get(position));
        String extension = from.getAbsolutePath().substring(from.getAbsolutePath().lastIndexOf("."));
        String FolderPath=from.getParentFile().getAbsolutePath();
        File to=new File(FolderPath + "/" + newName + extension);
        boolean isRename =from.renameTo(to);
//        Log.e("From path",from.getPath());
//        Log.e("To path",to.getPath());
        if(isRename){
            ContentResolver resolver = getApplicationContext().getContentResolver();
            resolver.delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA +
                            " =?", new String[]{from.getAbsolutePath()});
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(to));
            getApplicationContext().sendBroadcast(intent);
            scanPhoto(to.toString());

            Utils.IsUpdate = true;
            list.remove(position);
            list.add(position, to.getAbsolutePath());
            Toast.makeText(ViewImageActivity.this,"Rename Successfully!",Toast.LENGTH_SHORT).show();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(new Intent(ViewImageActivity.this,GetFileList.class).putExtra("action","album"));
            }else{
                startService(new Intent(ViewImageActivity.this,GetFileList.class).putExtra("action","album"));
            }
        }else{
            Toast.makeText(ViewImageActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
        }
    }

    public void FolderDialog(String action){
        folderAdapter = new FolderdialogAdapter(ViewImageActivity.this,Utils.mFolderDialogList,folderInterface);
        dial= new Dialog(ViewImageActivity.this, android.R.style.Theme_DeviceDefault);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.folders_dialog);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);
        folderAdapter.action=action;
        mFolderRec=dial.findViewById(R.id.targetList);
        mFolderRec.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.VERTICAL,false));
        mFolderRec.setAdapter(folderAdapter);
        dial.show();
    }

    public void CopyImage(String FolderPath){

        File from = new File(list.get(mImagePager.getCurrentItem()));
        String[] arr = from.getName().split("\\.");
        File to = new File(FolderPath + "/" + arr[0]+Utils.GetRandomNumber()+"."+arr[1]);
//        Log.e("From path",from.getPath());
//        Log.e("To path",to.getPath());
        try {
            InputStream in = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        }catch (Exception e){}

        scanPhoto(to.toString());

        Utils.IsUpdate=true;
        Toast.makeText(ViewImageActivity.this,"Image coppied suceesfully!!!",Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(ViewImageActivity.this, GetFileList.class).putExtra("action", "album"));
        } else {
            startService(new Intent(ViewImageActivity.this, GetFileList.class).putExtra("action", "album"));
        }
    }

    public void MoveImage(String FolderPath){
        int position=mImagePager.getCurrentItem();
        File from = new File(list.get(position));
        File to=new File(FolderPath + "/" + from.getName());
        boolean IsRename=from.renameTo(to);
        if(IsRename) {
            list.remove(position);
            if (position == list.size() - 1) {
                onBackPressed();
            } else {
                imageAdapter = new ShowImageAdapter(this, list,mImagePager);
                mImagePager.setAdapter(imageAdapter);
            }

            if (list.size() == 0)
                onBackPressed();
            scanPhoto(to.toString());

            Utils.IsUpdate = true;
            Toast.makeText(ViewImageActivity.this,"Image moved suceesfully!!!",Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(ViewImageActivity.this, GetFileList.class).putExtra("action", "album"));
            } else {
                startService(new Intent(ViewImageActivity.this, GetFileList.class).putExtra("action", "album"));
            }
        }
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
            }
        });
        this.msConn.connect();
    }

    public void SetAsFavourite(){
        File file=new File(list.get(mImagePager.getCurrentItem()));
        SharedPreferences sharedPreferences = getSharedPreferences("Favourites_pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        json1 = sharedPreferences.getString("Fav_Image","");
        Type type1 = new TypeToken<ArrayList<String>>(){
        }.getType();
        mFavouriteImageList = gson.fromJson(json1,type1);

        if(mFavouriteImageList == null){
            mFavouriteImageList = new ArrayList<>();
        }

        if(mFavouriteImageList.size() > 0){
            for(int i = 0;i < mFavouriteImageList.size();i++){
                if(mFavouriteImageList.get(i).equals(file.getPath())){
                    exist = true;
                    pos = i;
                    break;
                }else{
                    exist = false;
                }
            }
        }
        if(!exist){
            mFavouriteImageList.add(file.getPath());
            mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_heart));
        }else{
            mFavouriteImageList.remove(pos);
            mFavourite.setImageDrawable(getResources().getDrawable(R.drawable.white_heart));
            exist = false;
        }

        Utils.IsUpdate=true;
        json1 = gson.toJson(mFavouriteImageList);
        editor.putString("Fav_Image",json1);
        editor.apply();
    }

    @Override
    public void onBackPressed(){

        if (IsSlideShowRunning()) {
            CancelSlideShow();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        if(v.getId() == R.id.mTopLayer || v.getId() == R.id.mBottomLayer ){
            mRotateRL.setVisibility(View.GONE);
            mOptionRL.setVisibility(View.GONE);
        }
        return false;
    }

    public void SetList(ArrayList<String> pathlist, int position) {
        try {

            list = pathlist;
            currenpositionToDisplay = position;
        } catch (Exception e) {
            Log.e("Error:",e.getMessage());
        }
    }

}