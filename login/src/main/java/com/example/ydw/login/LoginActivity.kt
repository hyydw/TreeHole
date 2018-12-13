package com.example.ydw.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.SharedPreferences
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.Logger
import com.ydw.base.ARouterPath
import com.ydw.base.AppPreferences
import com.ydw.base.BaseActivity
import com.ydw.base.LogTag

import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
@Route(path = ARouterPath.ACTIVITY_URL_LOGIN)
class LoginActivity : BaseActivity() {
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Logger.t(LogTag.AppTag_Login).d("LoginActivity_onCreate")
    }

    override fun onResume() {
        super.onResume()
        sharedPref = this.getSharedLoginPref()
        username_sign_in_button.setOnClickListener {
            if(username.text.toString() == "qwe" && password.text.toString() == "123") {
                ARouter.getInstance().build(ARouterPath.ACTIVITY_URL_MAIN).navigation()
                with(sharedPref.edit()) {
                    putBoolean(AppPreferences.PrefKeys.pref_key_isLogin,true)
                    apply()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
