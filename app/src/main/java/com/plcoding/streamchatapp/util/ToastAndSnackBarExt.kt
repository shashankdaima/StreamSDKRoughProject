package com.plcoding.streamchatapp.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.makeSnackBar(string: String) {
    Snackbar.make(this, string, Snackbar.LENGTH_LONG).show()
}

fun Fragment.makeSnackBar(string: String) {
    Snackbar.make(this.requireView(), string, Snackbar.LENGTH_LONG).show()
}

fun Context.makeToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}