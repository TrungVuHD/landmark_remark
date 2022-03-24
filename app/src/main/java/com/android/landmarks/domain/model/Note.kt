package com.android.landmarks.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    var id: String = "",
    var userName: String = "",
    var location: Coordinates = Coordinates(),
    var remark: String = ""
) : Parcelable
