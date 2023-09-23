package com.woojun.ai.util

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

class TypeConverter {
    @TypeConverter
    fun fromChildInfoList(childInfoList: ArrayList<ChildInfo>): String {
        return Gson().toJson(childInfoList)
    }

    @TypeConverter
    fun toChildInfoList(childInfoListString: String): ArrayList<ChildInfo> {
        val listType = object : TypeToken<ArrayList<ChildInfo>>() {}.type
        return Gson().fromJson(childInfoListString, listType)
    }

    @TypeConverter
    fun fromSimilarDistanceUid(similarDistanceUid: List<List<String>>): String {
        return Gson().toJson(similarDistanceUid)
    }

    @TypeConverter
    fun toSimilarDistanceUid(similarDistanceUidString: String): List<List<String>> {
        val listType = object : TypeToken<List<List<String>>>() {}.type
        return Gson().fromJson(similarDistanceUidString, listType)
    }
}

@Entity
data class SimilarDistanceUid(
    @PrimaryKey
    val id: Int,
    var similarDistanceUid: List<List<String>> = listOf()
)

@Parcelize
data class FindChildImageResult(
    val distance_list: List<Double>,
    val similar_distance_list: List<List<Double>>,
    val similar_distance_uid: List<List<String>>
): Parcelable

data class PagerItem(
    val image: Int,
    val title: String,
    val subject: String
)

data class ResultSearchKeyword(
    var documents: List<Place>
)

data class Place(
    var place_name: String,
    var address_name: String,
    var road_address_name: String,
    var x: String,
    var y: String,
)

@Entity
data class UserInfo(
    @PrimaryKey
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var policeCheck: Boolean = false,
    var children: ArrayList<ChildInfo> = arrayListOf(),
    var registrationCheck: Boolean = false
)

@Parcelize
data class ChildInfo(
    val id: String = "",
    var name: String = "",
    var birthDate: String = "",
    var sex: String = "",
    var characteristics: String = "",
    var photo: String = "",
    var lastIdentityDate: String = "",
    var parentPhoneNumber: String = ""
): Parcelable

@Parcelize
data class AiResult(
    val age: Int?,
    val ageNow: Int?,
    val alldressingDscd: String?,
    val etcSpfeatr: String?,
    val msspsnIdntfccd: Int?,
    val nm: String?,
    val occrAdres: String?,
    val occrde: String?,
    val rnum: Int?,
    val sexdstnDscd: String?,
    val tknphotoFile: String?,
    val tknphotolength: Int?,
    val writngTrgetDscd: String?
): Parcelable

class AiResultList: ArrayList<AiResult>()

enum class ChildInfoType {
    NEW, DEFAULT, SEARCH
}

@Parcelize
enum class CameraType : Parcelable {
    ChildRegister, Find
}

@Parcelize
enum class MyChildAdapterType : Parcelable {
    DEFAULT, Find
}