package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.CameraInterface;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Interface.SortListner;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Preferences.SharedPrefrences;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Adapter.FolderAdapter;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Model.BaseModel;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Service.GetFileList;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.NotificationUtils;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private AppUpdateManager appUpdateManager;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    private InstallStateUpdatedListener installStateUpdatedListener;

    RecyclerView mFolderRec;
    FolderAdapter folderAdapter;
    ArrayList<BaseModel> mFolderList = new ArrayList<>();

    ImageView mfavourite,mMore;
    public static Handler album_handler;
    CameraInterface anInterface;
    int CAMERA_REQUEST=711;
    public static RelativeLayout mMoreRL,mTypeRL,mColumnRL;
    TextView mSorting,mViewType,mColumn;
    TextView mList,mGrid;
    TextView mColumn2,mColumn3,mColumn4;
     SortListner sortListener;
     EditText mSearchBar;

    private FirebaseAnalytics mFirebaseAnalytics;

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "New app is ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.purple_500))
                .show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }


    @Override
    public void permissionGranted(){
        intializehandler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        init();
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        checkUpdate();

        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };


        hideKeyboard(MainActivity.this);
        mSearchBar.clearFocus();
        anInterface=new CameraInterface(){
            @Override
            public void onCameraClick(){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);
            }
        };
        folderAdapter = new FolderAdapter(MainActivity.this,anInterface);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationUtils notiUtils = new NotificationUtils(this);
        }
        MainCaller();


        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                folderAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        throw new RuntimeException("Test Crash"); // Force a crash


    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(R.id.searchBar);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void MainCaller(){
        Utils.COLUMN= SharedPrefrences.getColumn(MainActivity.this);
        Utils.VIEW_TYPE=SharedPrefrences.getViewType(MainActivity.this);
        Utils.SORTING_TYPE=SharedPrefrences.getSortingType(MainActivity.this);
        Utils.SORTING_TYPE2=SharedPrefrences.getSortingType1(MainActivity.this);
        intializehandler();
        intializeSortingAlbum();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    saveImage(image);
                }
            }
        }
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            Log.e("Result code:", String.valueOf(resultCode));
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled by user! ", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),"Update success!  ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed!  ", Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        File myDir = new File(root, "DCIM");
        myDir.mkdirs();

        File myDir1 = new File(myDir, "Camera");
        myDir1.mkdirs();


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "" + timeStamp + ".jpg";

        File file = new File(myDir1, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "saveImage: " + file.getAbsolutePath());

        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, new String[]{file.getName()}, null);
        MainCaller();

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(Utils.IsUpdate){
//            runOnUiThread(new Runnable(){
//                @Override
//                public void run(){
                    intializehandler();
                    Utils.IsUpdate = false;
//                }
//            });
        }
    }

    public void init(){

        rl_progress=findViewById(R.id.rl_progress);
        avi=findViewById(R.id.avi);
        mSearchBar=findViewById(R.id.searchBar);
        mSearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mFolderRec = findViewById(R.id.folder_grid);
        mfavourite = findViewById(R.id.mFavourite);
        mfavourite.setOnClickListener(this);
        mMore = findViewById(R.id.mFilter);
        mMore.setOnClickListener(this);
        mMoreRL=findViewById(R.id.mMoreRL);
        mTypeRL=findViewById(R.id.mTypeRL);
        mColumnRL=findViewById(R.id.mColumnRL);

        mSorting=findViewById(R.id.mSorting);
        mViewType=findViewById(R.id.mViewType);
        mColumn=findViewById(R.id.mColumn);
        mSorting.setOnClickListener(this);
        mViewType.setOnClickListener(this);
        mColumn.setOnClickListener(this);

        mList=findViewById(R.id.mList);
        mGrid=findViewById(R.id.mGrid);
        mList.setOnClickListener(this);
        mGrid.setOnClickListener(this);

        mColumn2=findViewById(R.id.mColumn2);
        mColumn3=findViewById(R.id.mColumn3);
        mColumn4=findViewById(R.id.mColumn4);
        mColumn2.setOnClickListener(this);
        mColumn3.setOnClickListener(this);
        mColumn4.setOnClickListener(this);
    }

    public void intializehandler() {

        album_handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                int code = message.what;
                if (code == 21){

                    try{
                        mFolderList = new ArrayList<>();
                        mFolderList = (ArrayList<BaseModel>)message.obj;
                        Utils.mFolderDialogList=new ArrayList<>();
                        BaseModel FirstModel1 = new BaseModel();
                        FirstModel1.setFolderName("Create_Album");
                        FirstModel1.setType(1);
                        Utils.mFolderDialogList.add(0,FirstModel1);
                        Utils.mFolderDialogList.addAll(mFolderList);
                        BaseModel FirstModel = new BaseModel();
                        FirstModel.setFolderName("Camera");
                        FirstModel.setBucketName("Camera");
                        FirstModel.setType(1);
                        mFolderList.add(0, FirstModel);


                        if(mFolderList != null && mFolderList.size() > 0){
                            folderAdapter.Addall(mFolderList);
                        }

                        if(Utils.VIEW_TYPE.equals("Grid")){
                            mFolderRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
                            mFolderRec.setLayoutAnimation(null);
                        }else if(Utils.VIEW_TYPE.equals("List")){
                            mFolderRec.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.VERTICAL,false));
                        }
                        mFolderRec.setAdapter(folderAdapter);
                        folderAdapter.notifyDataSetChanged();
                        stopAnim();

                    }catch(Exception e){
                        Log.e("Error",e.getMessage());
                    }
                }
                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnim();
                // activity.startService(new Intent(activity, GetFileList.class).putExtra("action","album"));
                if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(MainActivity.this, GetFileList.class).putExtra("action", "album"));
                } else {
                    startService(new Intent(MainActivity.this, GetFileList.class).putExtra("action", "album"));
                }

            }
        }, 100);

    }

    RelativeLayout rl_progress;
    AVLoadingIndicatorView avi;

    public  void startAnim() {
        //   Log.e("start anim","!!!");
        rl_progress.setVisibility(View.VISIBLE);
        avi.show();
        // or avi.smoothToShow();
    }

    public  void stopAnim() {
        //   Log.e("stop   anim","!!!");
        rl_progress.setVisibility(View.GONE);
        avi.hide();
        // or avi.smoothToHide();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mFavourite:{
                fireAnalytics("favourite_image", "Selected");
                Intent in=new Intent(MainActivity.this,FavouriteActivity.class);
                startActivity(in);
            }
            break;
            case R.id.mFilter:{
                fireAnalytics("filter_image", "Selected");
                if(mMoreRL.getVisibility()==View.VISIBLE){
                    mMoreRL.setVisibility(View.GONE);
                }else{
                    mMoreRL.setVisibility(View.VISIBLE);
                }
                if(mTypeRL.getVisibility()==View.VISIBLE){
                    mTypeRL.setVisibility(View.GONE);
                }
                if(mColumnRL.getVisibility()==View.VISIBLE){
                    mColumnRL.setVisibility(View.GONE);
                }
            }
            break;
            case R.id.mSorting:{
                fireAnalytics("Sorting", "Selected");
                mMoreRL.setVisibility(View.GONE);
                sortingPopUp();
            }
            break;
            case R.id.mViewType:{
                mMoreRL.setVisibility(View.GONE);
                mColumnRL.setVisibility(View.GONE);
                mTypeRL.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.mColumn:{
                mMoreRL.setVisibility(View.GONE);
                mColumnRL.setVisibility(View.VISIBLE);
                mTypeRL.setVisibility(View.GONE);
            }
            break;
            case R.id.mList:{
                fireAnalytics("List_Image", "Selected");
                mTypeRL.setVisibility(View.GONE);
                Utils.VIEW_TYPE="List";
                SharedPrefrences.setViewType(MainActivity.this,Utils.VIEW_TYPE);
                if(Utils.VIEW_TYPE.equals("List")){
                    mFolderRec.setLayoutManager(new LinearLayoutManager(getBaseContext(),RecyclerView.VERTICAL,false));
                }
                mFolderRec.setAdapter(folderAdapter);
                folderAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.mGrid:{
                fireAnalytics("grid_image", "Selected");
                mTypeRL.setVisibility(View.GONE);
                Utils.VIEW_TYPE="Grid";
                SharedPrefrences.setViewType(MainActivity.this,Utils.VIEW_TYPE);
                if(Utils.VIEW_TYPE.equals("Grid")){
                    mFolderRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
                    mFolderRec.setLayoutAnimation(null);
                }
                mFolderRec.setAdapter(folderAdapter);
                folderAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.mColumn2:{
                fireAnalytics("column2", "Selected");
                mColumnRL.setVisibility(View.GONE);
                Utils.VIEW_TYPE="Grid";
                Utils.COLUMN=2;
                SharedPrefrences.setViewType(MainActivity.this,Utils.VIEW_TYPE);
                SharedPrefrences.setColumn(MainActivity.this,2);
                mFolderRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
                mFolderRec.setLayoutAnimation(null);
                mFolderRec.setAdapter(folderAdapter);
                folderAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.mColumn3:{
                fireAnalytics("column3", "Selected");
                mColumnRL.setVisibility(View.GONE);
                Utils.VIEW_TYPE="Grid";
                Utils.COLUMN=3;
                SharedPrefrences.setViewType(MainActivity.this,Utils.VIEW_TYPE);
                SharedPrefrences.setColumn(MainActivity.this, Utils.COLUMN);
                mFolderRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
                mFolderRec.setLayoutAnimation(null);
                mFolderRec.setAdapter(folderAdapter);
                folderAdapter.notifyDataSetChanged();
            }
            break;
            case R.id.mColumn4:{
                fireAnalytics("column4", "Selected");
                mColumnRL.setVisibility(View.GONE);
                Utils.COLUMN=4;
                SharedPrefrences.setViewType(MainActivity.this,Utils.VIEW_TYPE);
                SharedPrefrences.setColumn(MainActivity.this, Utils.COLUMN);
                mFolderRec.setLayoutManager(new GridLayoutManager(getBaseContext(),Utils.COLUMN));
                mFolderRec.setLayoutAnimation(null);
                mFolderRec.setAdapter(folderAdapter);
                folderAdapter.notifyDataSetChanged();
            }
            break;
        }
    }

    private void intializeSortingAlbum() {
        try {
            sortListener = new SortListner() {
                @Override
                public void onSortSelect() {
                    try {
                        if (mFolderList != null && mFolderList.size() > 0) {

                            if (mFolderList.get(0).getType() == 1) {
//                                Log.e("TAG", "onSortSelect: " + mFolderList.get(0).getFolderName() );
                                mFolderList.remove(0);
                            }
                            sortingAlbumList(mFolderList);
                        }
                    } catch (Exception e) {
                        Log.e("Error:",e.getMessage());
                    }

                }
            };
        } catch (Exception e) {
        }

    }

    class NameNoComparator implements Comparator<BaseModel>{

        @Override
        public int compare(BaseModel o1, BaseModel o2) {
            return Integer.compare(o1.getPathlist().size(), o2.getPathlist().size());
        }
    }

    public void sortingAlbumList(final ArrayList<BaseModel> album_filesList) {

        try {
            int sort=0;
            if(SharedPrefrences.getSortingType(MainActivity.this).equals(getResources().getString(R.string.no_of_photos))
                && SharedPrefrences.getSortingType1(MainActivity.this).equals(getResources().getString(R.string.descending))){
                sort = 0;
            }else   if(SharedPrefrences.getSortingType(MainActivity.this).equals(getResources().getString(R.string.no_of_photos))
                 && SharedPrefrences.getSortingType1(MainActivity.this).equals(getResources().getString(R.string.ascending)))     {
                sort=1;
            }else if(SharedPrefrences.getSortingType(MainActivity.this).equals(getResources().getString(R.string.last_modified_date))
                    && SharedPrefrences.getSortingType1(MainActivity.this).equals(getResources().getString(R.string.descending)))     {
                sort=2;
            }else if(SharedPrefrences.getSortingType(MainActivity.this).equals(getResources().getString(R.string.last_modified_date))
                    && SharedPrefrences.getSortingType1(MainActivity.this).equals(getResources().getString(R.string.ascending)))     {
                sort=3;
            }else if(SharedPrefrences.getSortingType(MainActivity.this).equals(getResources().getString(R.string.name))
                    && SharedPrefrences.getSortingType1(MainActivity.this).equals(getResources().getString(R.string.descending)))     {
                sort=4;
            }else if(SharedPrefrences.getSortingType(MainActivity.this).equals(getResources().getString(R.string.name))
                    && SharedPrefrences.getSortingType1(MainActivity.this).equals(getResources().getString(R.string.ascending)))     {
                sort=5;
            }


            Log.e("Sort :",String.valueOf(sort));
            if (album_filesList.size() > 0) {
                switch (sort) {

                    case 0: // Number of photos Decending
//                        Log.e("TAG", " Number of photos Decending" );
                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {

                                if (mediaFileListModel.getPathlist().size() > t1.getPathlist().size()) {

                                    if (mediaFileListModel.getPathlist().get(0).equalsIgnoreCase("empty"))
                                        return -1;
                                    else
                                        return 1;
                                } else
                                    return -1;

                            }
                        });

                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {

                                if (mediaFileListModel.getPathlist().size() < t1.getPathlist().size()) {

                                    if (t1.getPathlist().get(0).equalsIgnoreCase("empty"))
                                        return -1;
                                    else
                                        return 1;
                                } else
                                    return -1;

                                // return t1.getFileCreatedTimeDate().compareTo(mediaFileListModel.getFileCreatedTimeDate()); //Last Modified
                                // return mediaFileListModel.getParentDirectory().compareTo(t1.getParentDirectory()); // Name wise
                            }
                        });

                        break;

                    case 1: // Number of photos Ascending
//                        Log.e("TAG", " Number of photos Ascending");
                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {

                                if (t1.getPathlist().size() < mediaFileListModel.getPathlist().size()) {

                                    if (t1.getPathlist().get(0).equalsIgnoreCase("empty"))
                                        return -1;
                                    else
                                        return 1;
                                } else
                                    return -1;

                            }
                        });

                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {

                                if (t1.getPathlist().size() < mediaFileListModel.getPathlist().size()) {

                                    if (mediaFileListModel.getPathlist().get(0).equalsIgnoreCase("empty"))
                                        return -1;
                                    else
                                        return 1;
                                } else
                                    return -1;

                                // return t1.getFileCreatedTimeDate().compareTo(mediaFileListModel.getFileCreatedTimeDate()); //Last Modified
                                // return mediaFileListModel.getParentDirectory().compareTo(t1.getParentDirectory()); // Name wise

                            }
                        });

                        break;

                    case 2:  // 2 = Last Modified - decending
                        Log.e("TAG", " Last Modified - decending");
                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t2, BaseModel t1) {

                                File file = new File(t2.getPathlist().get(0));
                                String parentPath = file.getAbsoluteFile().getParent();
                                Date lastModDate = new Date(new File(parentPath).lastModified());

                                file = new File(t1.getPathlist().get(0));
                                parentPath = file.getAbsoluteFile().getParent();
                                Date lastModDate2 = new Date(new File(parentPath).lastModified());


                                return lastModDate.toString().compareTo(lastModDate2.toString()); //Last Modified
                                // return mediaFileListModel.getParentDirectory().compareTo(t1.getParentDirectory()); // Name wise
                            }
                        });

                        Collections.reverse(album_filesList);

                        break;

                    case 3:    // 3 = Last Modified - Ascending
                        Log.e("TAG", " Last Modified - Ascending");
                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel t2, BaseModel t1) {

                                File file = new File(t2.getPathlist().get(0));
                                String parentPath = file.getAbsoluteFile().getParent();
                                Date lastModDate = new Date(new File(parentPath).lastModified());

                                file = new File(t1.getPathlist().get(0));
                                parentPath = file.getAbsoluteFile().getParent();
                                Date lastModDate2 = new Date(new File(parentPath).lastModified());

                                return lastModDate.toString().compareTo(lastModDate2.toString()); //Last Modified
                                // return mediaFileListModel.getParentDirectory().compareTo(t1.getParentDirectory()); // Name wise
                            }
                        });

                        break;

                    case 4:  // 4 = Name - decending
//                        Log.e("TAG", "  Name - decending");
                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {
                                return t1.getBucketName().toLowerCase().compareTo(mediaFileListModel.getBucketName().toLowerCase()); // Name wise
                            }
                        });

                        break;
                    case 5:  // 5 = Name - Ascending
//                        Log.e("TAG", "  Name - Ascending");
                        Collections.sort(album_filesList, new Comparator<BaseModel>() {
                            @Override
                            public int compare(BaseModel mediaFileListModel, BaseModel t1) {
                                return mediaFileListModel.getBucketName().toLowerCase().compareTo(t1.getBucketName().toLowerCase()); // Name wise
                            }
                        });

                        break;
                }

                //------------------create album --------
                BaseModel albumDetail1 = new BaseModel();
                albumDetail1.setFolderName("Camera");
                albumDetail1.setType(1);
                album_filesList.add(0, albumDetail1);

                folderAdapter.Addall(album_filesList);
                folderAdapter.notifyDataSetChanged();

            }
        } catch (Exception w) {
            System.out.println("Sorting Error=>" + w.getMessage());
        }
    }


    public static void Disablecard(){
        mMoreRL.setVisibility(View.GONE);
        mTypeRL.setVisibility(View.GONE);
        mColumnRL.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void sortingPopUp() {
        String[] radio1 = {getResources().getString(R.string.no_of_photos),
                getResources().getString(R.string.last_modified_date),
                getResources().getString(R.string.name)};

        String[] radio2 = {getResources().getString(R.string.ascending),
                getResources().getString(R.string.descending)};

        final Dialog dial = new Dialog(MainActivity.this, android.R.style.Theme_DeviceDefault);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.sorting_dialog);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);

        RadioGroup rg_sorting1 = dial.findViewById(R.id.rg_sorting1);
        RadioGroup rg_sorting2 = dial.findViewById(R.id.rg_sorting2);

        for (int i = 0; i < radio1.length; i++) {
            String sorting1 = radio1[i];
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(30, 30, 7, 30);
            radioButton.setText(sorting1);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            radioButton.setTextColor(getResources().getColor(R.color.black));
            radioButton.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            radioButton.setId(i);

            if (Utils.SORTING_TYPE2.equals("")) {
                if (i == 1) {
                    radioButton.setChecked(true);
                    Utils.SORTING_TYPE = radioButton.getText().toString();
                }
            } else if (radioButton.getText().equals(Utils.SORTING_TYPE)) {
                radioButton.setChecked(true);
            }
            rg_sorting1.addView(radioButton);
        }

        //set listener to radio button group
        rg_sorting1.setOnCheckedChangeListener((group, checkedId) -> {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            RadioButton radioBtn = dial.findViewById(checkedRadioButtonId);
            Utils.SORTING_TYPE = radioBtn.getText().toString();
//            Log.e("Selected radio 1: ", Utils.SORTING_TYPE);
        });

        for (int i = 0; i < radio2.length; i++) {
            String sorting2 = radio2[i];
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(30, 30, 7, 30);
            radioButton.setText(sorting2);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            radioButton.setTextColor(getResources().getColor(R.color.black));
            radioButton.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            radioButton.setId(i + radio1.length);

            if (Utils.SORTING_TYPE2.equals("")) {
                if (i == 0) {
                    radioButton.setChecked(true);
                    Utils.SORTING_TYPE2 = radioButton.getText().toString();
                }
            } else if (radioButton.getText().equals(Utils.SORTING_TYPE2)) {
                radioButton.setChecked(true);
            }
            rg_sorting2.addView(radioButton);
        }

        //set listener to radio button group
        rg_sorting2.setOnCheckedChangeListener((group, checkedId) -> {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            RadioButton radioBtn = dial.findViewById(checkedRadioButtonId);
            Utils.SORTING_TYPE2 = radioBtn.getText().toString();
//            Log.e("Selected radio 2: ", Utils.SORTING_TYPE2);
        });

        dial.findViewById(R.id.tv_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("1 : "+ Utils.SORTING_TYPE , "2 :" + Utils.SORTING_TYPE2);
                SharedPrefrences.setSortingType(MainActivity.this,Utils.SORTING_TYPE);
                SharedPrefrences.setSortingType1(MainActivity.this,Utils.SORTING_TYPE2);
                dial.dismiss();
                if (sortListener != null)
                    sortListener.onSortSelect();
            }
        });

        dial.show();
    }


}