package bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.R
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.databinding.ActivityFrameBinding
import bestgalleryadfree.gallerylock.galleryvault.freegallerypro.Util.Utils
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.lyrebirdstudio.croppylib.main.CroppyTheme
import com.lyrebirdstudio.croppylib.util.file.FileCreator
import com.lyrebirdstudio.croppylib.util.file.FileOperationRequest

class FrameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_frame)

        startCroppy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_CROP_IMAGE) {
            data?.data?.let {
//                Log.e("TEST", it.toString())
                Utils.mEditedURI=it;
                Utils.IsFramed=true;
//                binding.imageViewCropped.setImageURI(it)
                Utils.CaptureImage(it,applicationContext)
            }
        }
        else{
            Utils.IsFramed=false;
        }
        val `in` = Intent(applicationContext, ViewImageActivity::class.java)
        startActivity(`in`)
        finish()
    }

    override fun onBackPressed() {

        Utils.IsFramed=false;
        val `in` = Intent(applicationContext, ViewImageActivity::class.java)
        startActivity(`in`)
        finish()
    }

    fun startCroppy() {
//        val uri = Uri.Builder()
//                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//                .authority(resources.getResourcePackageName(R.drawable.sample))
//                .appendPath(resources.getResourceTypeName(R.drawable.sample))
//                .appendPath(resources.getResourceEntryName(R.drawable.sample))
//                .build()
//
//        //Saves to external and return uri
//        val externalCropRequest = CropRequest.Auto(
//                sourceUri = uri,
//                requestCode = RC_CROP_IMAGE
//        )
//
//        //Saves to cache and return uri
//        val cacheCropRequest = CropRequest.Auto(
//                sourceUri = uri,
//                requestCode = RC_CROP_IMAGE,
//                storageType = StorageType.CACHE
//        )
//
//        // Save to given destination uri.
        val destinationUri =
                FileCreator
                        .createFile(FileOperationRequest.createRandom(), application.applicationContext)
                        .toUri()

//        val manualCropRequest = CropRequest.Manual(
//                sourceUri = uri,
//                destinationUri = destinationUri,
//                requestCode = RC_CROP_IMAGE
//        )
//
//        val excludeAspectRatiosCropRequest = CropRequest.Manual(
//                sourceUri = uri,
//                destinationUri = destinationUri,
//                requestCode = RC_CROP_IMAGE,
//                excludedAspectRatios = arrayListOf(AspectRatio.ASPECT_FREE)
//        )

        val themeCropRequest = CropRequest.Manual(
                sourceUri = Utils.mEditedURI,
                destinationUri = destinationUri,
                requestCode = RC_CROP_IMAGE,
                croppyTheme = CroppyTheme(R.color.blue)
        )

        Croppy.start(this, themeCropRequest)
    }

    companion object {
        private const val RC_CROP_IMAGE = 102

    }
}