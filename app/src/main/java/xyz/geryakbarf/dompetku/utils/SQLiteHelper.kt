package xyz.geryakbarf.dompetku.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "dompetku"
        val DB_VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val sql =
            "CREATE TABLE IF NOT EXISTS catatan (id INTEGER PRIMARY KEY AUTOINCREMENT,pemasukan INTEGER,pengeluaran INTEGER)"
        p0?.execSQL(sql)

        val sql2 =
            "CREATE TABLE IF NOT EXISTS detail_catatan (tanggal TEXT,nama TEXT, jenis TEXT, saldo INTEGER)"
        p0?.execSQL(sql2)
    }

    fun addFirstCatatan(nama: String, saldo: Int, tanggal: String, jenis: String): Boolean {
        val db = this.writableDatabase
        val status: Boolean
        val value = ContentValues()
        when (jenis) {
            "Pemasukan" -> {
                value.put("pemasukan", saldo)
                value.put("pengeluaran", 0)
            }
            "Pengeluaran" -> {
                value.put("pengeluaran", saldo)
                value.put("pemasukan", 0)
            }
        }
        val result1 = db.insert("catatan", null, value)
        status = if (result1 < 0)
            false
        else {
            val detail = ContentValues()
            detail.put("tanggal", tanggal)
            detail.put("nama", nama)
            detail.put("jenis", jenis)
            detail.put("saldo", saldo)
            val result2 = db.insert("detail_catatan", null, detail)
            result2 >= 0
        }
        return status
    }

    fun addCatatan(nama: String, saldo: Int, tanggal: String, jenis: String) {
        val db = this.writableDatabase
        var sql = ""
        val value = ContentValues()
        when (jenis) {
            "Pemasukan" -> {
                sql = "UPDATE catatan SET pemasukan = pemasukan + $saldo"
            }
            "Pengeluaran" -> {
                sql = "UPDATE catatan SET pengeluaran = pengeluaran + $saldo"
            }
        }
        db.execSQL(sql)
        val detail = ContentValues()
        detail.put("tanggal", tanggal)
        detail.put("nama", nama)
        detail.put("jenis", jenis)
        detail.put("saldo", saldo)
        db.insert("detail_catatan", null, detail)
    }

    fun getSaldo(): Int {
        var saldo: Int
        var masuk = 0
        var keluar = 0
        val db = this.writableDatabase
        val sql = db.rawQuery("SELECT * FROM catatan", null)
        if (sql.count == 0)
            saldo = 0
        else {
            while (sql.moveToNext()) {
                masuk = sql.getInt(1)
                keluar = sql.getInt(2)
            }
            saldo = masuk - keluar
        }
        return saldo
    }

    fun isNotEmpty(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM catatan", null)
    }

    fun getTodayNote(today: String): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM detail_catatan WHERE tanggal LIKE '%$today%'", null)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}