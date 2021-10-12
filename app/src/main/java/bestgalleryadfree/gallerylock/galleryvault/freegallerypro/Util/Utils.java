package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity.ViewImageActivity;
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Model.BaseModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils{

    public static ArrayList<BaseModel> mFolderDialogList = new ArrayList<>();
    public static String VIEW_TYPE="List";
    public static String SORTING_TYPE = "";
    public static String SORTING_TYPE2 = "";
    public static int COLUMN = 2;
    public static boolean IsUpdate=false;
    public static File mOriginalFile;
    public static String mEditpath;
    public static Bitmap mOriginalBitmap;
    public static Bitmap mEditedBitmap;
    public static Uri mEditedURI;
    public static boolean IsFramed=false;
    public static boolean IsCropped=false;
    public static Context c;

    public static int GetRandomNumber(){
        int flag=0;
        flag=new Random().nextInt(1000);
        return flag;
    }

    public static String getSize(long size) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        long tera = giga * kilo;
        String s = "";
        double kb = (double)size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if(size < kilo) {
            s = size + " Bytes";
        } else if(size >= kilo && size < mega) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= mega && size < giga) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= giga && size < tera) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= tera) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    public static String convertTimeFromUnixTimeStamp(String date) {

        try {
            DateFormat inputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zz yyy");
            Date d = null;
            try {
                d = inputFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // DateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

            Locale loc = new Locale("en", "US");
            DateFormat outputFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, loc);
            return outputFormat.format(d);
        }catch (Exception e){
            return "not vaialble";
        }
    }

    public static boolean delete(final Context context,final File file) {

        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[]{
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }

    public static File CaptureImage(Uri uri,Context context) {
        c=context;
        Bitmap bitmap=null;
        File f = null;
        try {
            // create bitmap screen capture
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver() , uri);
            }
            catch (Exception e)
            {
                //handle exception
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            //----------------dsestination path--------
            String ParentPath=Utils.mOriginalFile.getParentFile().getPath();
//            Log.e("Parent path:",ParentPath + "*");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Uri destination = Uri.fromFile(new File(ParentPath + "/" + timeStamp + ".jpg"));
            String NewImagePath=destination.getPath();
//            Log.e("New path:",NewImagePath + "*");
            //-----------------------------------------
            if (NewImagePath != null) {
                f = new File(NewImagePath);
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //------------insert into media list----
                File newfilee = new File(destination.getPath());
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, destination.getPath());
                values.put(MediaStore.Images.Media.DATE_TAKEN, newfilee.lastModified());
                scanPhoto(newfilee.getPath());
                Toast.makeText(c,"Image saved successfully!!!",Toast.LENGTH_SHORT).show();
                Uri uri1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri1 = FileProvider.getUriForFile(c.getApplicationContext(), c.getPackageName() + ".provider", newfilee);
                } else {
                    uri1 = Uri.fromFile(newfilee);
                }
                c.getContentResolver().notifyChange(uri1, null);
                Utils.IsUpdate=true;
                ViewImageActivity.list.add(0,NewImagePath);
            }
        } catch (Exception e) {
        }
        return f;

    }

    public static MediaScannerConnection msConn;
    public static void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(c, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
            }
        });
        msConn.connect();
    }

}
