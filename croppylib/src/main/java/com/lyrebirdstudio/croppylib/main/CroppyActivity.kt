package com.lyrebirdstudio.croppylib.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lyrebirdstudio.croppylib.R
import com.lyrebirdstudio.croppylib.databinding.ActivityCroppyBinding
import com.lyrebirdstudio.croppylib.ui.ImageCropFragment

class CroppyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCroppyBinding

    private lateinit var viewModel: com.lyrebirdstudio.croppylib.main.CroppyActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_croppy)

        viewModel = ViewModelProviders.of(this).get(com.lyrebirdstudio.croppylib.main.CroppyActivityViewModel::class.java)

        val cropRequest = intent.getParcelableExtra(com.lyrebirdstudio.croppylib.main.CroppyActivity.Companion.KEY_CROP_REQUEST) ?: com.lyrebirdstudio.croppylib.main.CropRequest.Companion.empty()

        if (savedInstanceState == null) {
            val cropFragment = ImageCropFragment.newInstance(cropRequest)
                .apply {
                    onApplyClicked = {
                        viewModel.saveBitmap(cropRequest = cropRequest, croppedBitmapData = it)
                    }

                    onCancelClicked = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            supportFragmentManager.beginTransaction()
                .add(R.id.containerCroppy, cropFragment)
                .commitAllowingStateLoss()
        }


        viewModel.getSaveBitmapLiveData().observe(this, Observer {
            setResult(Activity.RESULT_OK, Intent().apply { data = it })
            finish()
        })
    }

    companion object {

        private const val KEY_CROP_REQUEST = "KEY_CROP_REQUEST"

        fun newIntent(context: Context, cropRequest: com.lyrebirdstudio.croppylib.main.CropRequest): Intent {
            return Intent(context, com.lyrebirdstudio.croppylib.main.CroppyActivity::class.java)
                .apply {
                    Bundle()
                        .apply { putParcelable(com.lyrebirdstudio.croppylib.main.CroppyActivity.Companion.KEY_CROP_REQUEST, cropRequest) }
                        .also { this.putExtras(it) }
                }
        }
    }
}