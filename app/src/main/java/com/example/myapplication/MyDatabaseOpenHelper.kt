package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper private constructor(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    init {
        instance = this
    }

    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: MyDatabaseOpenHelper(ctx)

        @JvmStatic
        fun saveDB(name:String,content:String){
            database.use {
                insert("table_dic",
                        "name" to name,"content" to content);
            }
        }

        @JvmStatic
        fun updateDB(name:String,content:String){
            database.use {
                update("table_dic","content" to content)
                        .whereArgs("name = {name}", "name" to name)
                        .exec();
            }

        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("table_dic", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "name" to TEXT,
                "content" to TEXT
                )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
//        db.dropTable("XX", true)
    }


}


val database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(App.me);


class Dic(name: String, content: String){
    var name:String;
    var content:String;

    init {
        this.name=name
        this.content=content
    }

    override fun toString(): String {
        return name+","+content;
    }

    override fun equals(other: Any?): Boolean {
        return other is Dic && name.equals(other.name)&& content?.equals(other.content)
    }

}