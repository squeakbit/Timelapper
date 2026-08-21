package de.example.timelapse.data
import android.content.Context
import androidx.room.*
@Database(entities=[PhotoEntity::class],version=1,exportSchema=false)
abstract class AppDatabase:RoomDatabase(){
 abstract fun photoDao():PhotoDao
 companion object { @Volatile private var INSTANCE:AppDatabase?=null
  fun getInstance(c:Context)=INSTANCE?: synchronized(this){INSTANCE?:Room.databaseBuilder(c.applicationContext,AppDatabase::class.java,"timelapse.db").build().also{INSTANCE=it}}
 }
}
