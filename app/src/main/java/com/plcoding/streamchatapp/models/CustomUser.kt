package com.plcoding.streamchatapp.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomUser (
    val name:String
): Parcelable