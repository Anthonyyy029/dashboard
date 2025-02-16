package com.example.signin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 1

        // Table Name
        private const val TABLE_USERS = "users"

        // Column Names
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // 🔹 Hash Password Function
    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // 🔹 Insert User (Now with Logging)
    fun insertUser(name: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val hashedPassword = hashPassword(password)  // Hash password before storing
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, hashedPassword)
        }

        val result = db.insert(TABLE_USERS, null, values)
        db.close()

        if (result == -1L) {
            Log.e("DatabaseHelper", "Failed to insert user")
        } else {
            Log.d("DatabaseHelper", "User registered successfully: $email")
        }

        return result != -1L
    }

    // 🔹 Check User Login (Now with Logging)
    fun checkUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val hashedPassword = hashPassword(password) // Hash entered password before checking
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email, hashedPassword))

        val userExists = cursor.count > 0

        if (userExists) {
            Log.d("DatabaseHelper", "Login success for: $email")
        } else {
            Log.e("DatabaseHelper", "Login failed: No matching user found for $email")
        }

        cursor.close()
        db.close()

        return userExists
    }
}
