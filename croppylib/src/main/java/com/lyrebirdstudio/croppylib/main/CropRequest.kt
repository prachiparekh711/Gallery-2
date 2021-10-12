package com.lyrebirdstudio.croppylib.main

import android.net.Uri
import android.os.Parcelable
import com.lyrebirdstudio.aspectratiorecyclerviewlib.aspectratio.model.AspectRatio
import com.lyrebirdstudio.croppylib.R
import kotlinx.android.parcel.Parcelize

@Parcelize
open class CropRequest(
    open val sourceUri: Uri,
    open val requestCode: Int,
    open val excludedAspectRatios: List<AspectRatio>,
    open val croppyTheme: com.lyrebirdstudio.croppylib.main.CroppyTheme
) : Parcelable {

    @Parcelize
    class Manual(
        override val sourceUri: Uri,
        val destinationUri: Uri,
        override val requestCode: Int,
        override val excludedAspectRatios: List<AspectRatio> = arrayListOf(),
        override val croppyTheme: com.lyrebirdstudio.croppylib.main.CroppyTheme = com.lyrebirdstudio.croppylib.main.CroppyTheme(R.color.blue)
    ) : com.lyrebirdstudio.croppylib.main.CropRequest(sourceUri, requestCode, excludedAspectRatios, croppyTheme)

    @Parcelize
    class Auto(
            override val sourceUri: Uri,
            override val requestCode: Int,
            val storageType: com.lyrebirdstudio.croppylib.main.StorageType = com.lyrebirdstudio.croppylib.main.StorageType.EXTERNAL,
            override val excludedAspectRatios: List<AspectRatio> = arrayListOf(),
            override val croppyTheme: com.lyrebirdstudio.croppylib.main.CroppyTheme = com.lyrebirdstudio.croppylib.main.CroppyTheme(R.color.blue)
    ) : com.lyrebirdstudio.croppylib.main.CropRequest(sourceUri, requestCode, excludedAspectRatios, croppyTheme)

    companion object {
        fun empty(): com.lyrebirdstudio.croppylib.main.CropRequest =
                com.lyrebirdstudio.croppylib.main.CropRequest(Uri.EMPTY, -1, arrayListOf(), com.lyrebirdstudio.croppylib.main.CroppyTheme(R.color.blue))
    }
}


