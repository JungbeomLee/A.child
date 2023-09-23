package com.woojun.ai.util

import androidx.room.*

@Dao
interface UserInfoDAO {
    @Insert
    fun insertUser(user: UserInfo)

    @Update
    fun updateUser(user: UserInfo)

    @Query("SELECT * FROM UserInfo")
    fun getUser(): UserInfo

    @Delete
    fun deleteUser(user: UserInfo)
}

@Dao
interface FindChildDAO {
    @Insert
    fun insertFindChild(findChild: SimilarDistanceUid)

    @Update
    fun updateFindChild(findChild: SimilarDistanceUid)

    @Query("SELECT * FROM SimilarDistanceUid")
    fun getFindChild(): SimilarDistanceUid

    @Delete
    fun deleteFindChild(findChild: SimilarDistanceUid)
}