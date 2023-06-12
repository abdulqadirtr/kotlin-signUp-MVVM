package com.example.loginviewmvvm.ui.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ConfirmationData(
    val firstName: String,
    val email: String,
    val website: String,
    val photoUri: Uri?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Uri::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(email)
        parcel.writeString(website)
        parcel.writeParcelable(photoUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ConfirmationData> {
        override fun createFromParcel(parcel: Parcel): ConfirmationData {
            return ConfirmationData(parcel)
        }

        override fun newArray(size: Int): Array<ConfirmationData?> {
            return arrayOfNulls(size)
        }
    }
}