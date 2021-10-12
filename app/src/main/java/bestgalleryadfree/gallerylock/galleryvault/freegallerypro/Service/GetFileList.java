package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity.MainActivity;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Model.BaseModel;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.NotificationUtils;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Umiya Mataji on 7/18/2017.
 */

public class GetFileList extends Service{


    public static int TotalPhotos = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent,int flags,int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, NotificationUtils.ANDROID_CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(1, notification);
            stopForeground(STOP_FOREGROUND_REMOVE);
        }
        String action = null;

        try {
            action = intent.getStringExtra("action");
        } catch (Exception e) {
        }
//        Log.e("Action:",action);
        try {
            if (action.equalsIgnoreCase("photo")) {
                GetAllPhotos();
            }  else if (action.equalsIgnoreCase("album")) {

                GetPhotoAlbumLis();
            }
        } catch (Exception e) {
        }

        return super.onStartCommand(intent, flags, startId);

    }


    public void GetAllPhotos() {

        try {
            ArrayList<String> Image_List;
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.DATA
            };

            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            Cursor cur = getContentResolver().query(images,
                    projection, // Which columns to return
                    null,       // Which rows to return (all rows)
                    null,       // Selection arguments (none)
                    orderBy + " DESC"        // Ordering
            );

            Image_List = new ArrayList<>();
            if (cur.moveToFirst()) {
                String bucket;
                String path;
                int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int dateColumn = cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                int column_index = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {

                    bucket = cur.getString(bucketColumn);
                    if(!bucket.startsWith(".")){
                        path = cur.getString(column_index);
                        File filePath = new File(path);
                        double length = filePath.length();
//                        Log.e("TAG","GetAllPhotos: " + filePath.getName() + " ?? " + length);
                        if(!filePath.getName().startsWith(".")){
                            if(length > 0)
                                Image_List.add(path);
                        }
                    }
                } while (cur.moveToNext());

                if (Utils.SORTING_TYPE2.equals(getString(R.string.descending))) {
                    Collections.reverse(Image_List);
                }
                Message message = new Message();
                message.what = 21;
                message.obj = Image_List;
            }
        } catch (Exception e) {
        }
    }

    class NameNoComparator implements Comparator<BaseModel>{

        @Override
        public int compare(BaseModel o1, BaseModel o2) {
            return Integer.compare(o1.getPathlist().size(), o2.getPathlist().size());
        }
    }

    public void GetPhotoAlbumLis() {
        try {
            TotalPhotos = 0;
            ArrayList<BaseModel> folderListArray = new ArrayList<BaseModel>();
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            ArrayList<String> ids = new ArrayList<String>();
            int count = 0;
//            Log.e("array size", "" + ids.size() + "===" + cursor.getCount());

            if (cursor != null) {
//                TotalPhotos = cursor.getCount();
                while (cursor.moveToNext()) {

                    BaseModel album = new BaseModel();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    int columnIndexName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    album.setBucketId(cursor.getString(columnIndex));
                    String fname = cursor.getString(columnIndexName);
                    if(!fname.startsWith(".")){
//                    if (fname != null || !fname.equalsIgnoreCase("null")) {
//                    Log.e("array size", "" + ids.size() + "===" + album.bucket_id + " >>>>> " + cursor.getString(columnIndexName) + " >>> " + getCameraCover("" + album.bucket_id).size());
                        if (!ids.contains(album.getBucketId())) {
                            columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                            album.setBucketName(cursor.getString(columnIndex));
                            if (cursor.getString(columnIndex)!=null){
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                String result = cursor.getString(column_index);
                                String ParentPath = GetParentPath(result);
                                album.setBucketPath(ParentPath);

                                columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                album.setId(cursor.getString(columnIndex));
                                album.pathlist = getCameraCover("" + album.getBucketId()); //----get four image path arraylist
                                album.setType(0);
                                album.setFolderName(result);
//                                Log.e("TAG", "GetPhotoAlbumLis: " + album.getBucketName());
//                            if (album.foldername != null || !album.foldername.equalsIgnoreCase("null")) {
                                if (album.pathlist.size() > 0) {
//                        if (album.pathlist.size() > 0 && album.foldername.trim().length()>0) {

                                        folderListArray.add(album);
                                        ids.add(album.getBucketId());

                                }
//                            }
                                TotalPhotos += album.pathlist.size();
                            }
                        }
                    }
                }
                cursor.close();

                if (Utils.SORTING_TYPE.equals(getString(R.string.no_of_photos))) {
                    Collections.sort(folderListArray, new NameNoComparator());
                }
                if (Utils.SORTING_TYPE.equals(getString(R.string.name))) {
                    Collections.sort(folderListArray, (Comparator<BaseModel>) (lhs, rhs) -> {
                        return lhs.getBucketName().toLowerCase().compareTo(rhs.getBucketName().toLowerCase());
                    });
                }

                if (Utils.SORTING_TYPE2.equals(getString(R.string.descending))) {
                    Collections.reverse(folderListArray);
                }
//                Log.e("array size folder", "" + folderListArray.size());
                Message message = new Message();
                message.what = 21;
                message.obj = folderListArray;
                MainActivity.album_handler.sendMessage(message);
            }

        } catch (Exception e) {
        }
    }


    public ArrayList<String> getCameraCover(String id) {

        String data = null;
        ArrayList<String> result = new ArrayList<String>();
        final String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {id};
        String orderBy = MediaStore.Images.ImageColumns.DATE_MODIFIED + " DESC";
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                orderBy);

        if (cursor.moveToFirst()) {

            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final int name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            do {
                data = cursor.getString(dataColumn);
                File filePath = new File(data);
                double length = filePath.length();
                if (length > 0) {
                    if(!filePath.getName().startsWith(".")) {
                        result.add(data);
                    }
                }
                //---------------------------------------------
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }


    public String GetParentPath(String path) {
        File file = new File(path);
        return file.getAbsoluteFile().getParent();
    }

}
