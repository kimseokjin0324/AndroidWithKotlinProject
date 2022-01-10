package com.example.aop_part2_chapter4.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.aop_part2_chapter4.model.History

//- History Entity 를 어떻게 저장, 조회, 삭제를 하는지 정의하는것.
@Dao
interface HistoryDao {

    //- 1. History를 전부 가져온다.(SELECT)
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    // -2. INSERT
    @Insert
    fun insertHistory(history: History)

    // - 3.DELETE
    @Query("DELETE FROM history")
    fun deleteALL()

    //- 3-1. 해당 히스토리만 삭제
    @Delete
    fun delte(history: History)

    // - 특정 조건 (result)에 대한 리스트만 반환하고싶을때
    @Query("SELECT * FROM history WHERE result LIKE :result")
    fun findByResult(result : String) : List<History>
    // - 특정조건(result)를 만족하는 것만 반환하고싶을때
    @Query("SELECT * FROM history WHERE result LIKE :result LIMIT 1")
    fun findByResultOne(result : String) : History
}