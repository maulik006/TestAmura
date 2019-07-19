package com.example.testamura

import com.google.firebase.database.FirebaseDatabase


class Utils {

    companion object {
        private var mDatabase: FirebaseDatabase? = null

        fun getDatabase(): FirebaseDatabase {
            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance()
                mDatabase?.setPersistenceEnabled(true)
            }
            return mDatabase!!
        }
    }


}