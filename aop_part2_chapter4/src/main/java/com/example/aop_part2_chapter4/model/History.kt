package com.example.aop_part2_chapter4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//- History 클래스 자체를 Room 그대로 테이블로 할 것임
@Entity
data class History(
    @PrimaryKey val uid : Int?,  //Unique id- primaryKey tag를 달아줌
    @ColumnInfo(name = "expression") val expression : String?,   //계산식(표현식)
    @ColumnInfo(name = "result") val result : String?,
)