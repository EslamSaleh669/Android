package intalio.cts.mobile.android.data.model


import com.google.gson.annotations.SerializedName

data class AllNews(

    @SerializedName("description")
    val description: String,
    @SerializedName("name_en")
    var nameEn: String,
    @SerializedName("photo")
    val photo: String

)