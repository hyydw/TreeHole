package com.ydw.multicomponent

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.Logger
import com.ydw.base.ARouterPath
import com.ydw.base.AppPreferences
import com.ydw.base.AppPreferences.Companion.isFirstStartup
import com.ydw.base.AppPreferences.Companion.isLogin
import com.ydw.base.BaseActivity
import com.ydw.base.LogTag
import com.ydw.base.viewCtrls.pathAnimatorCtrl.AsyncTextPathView

@Route(path = ARouterPath.ACTIVITY_URL_PRELOGIN)
class PreLoginActivity :BaseActivity(){
    private lateinit var sharedPref :SharedPreferences
    private lateinit var handler:Handler
    private lateinit var runnable:Runnable
    private lateinit var asyncText: AsyncTextPathView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prelogin)
        asyncText = findViewById(R.id.asyncText)
        asyncText.startAnimation(0f,1f)//启动文字动画
        Logger.t(LogTag.AppTag_App).d("PreLoginActivity_onCreate")
    }

    override fun onResume() {
        super.onResume()
        sharedPref = this.getSharedLoginPref()
        isFirstStartup = sharedPref.getBoolean(AppPreferences.PrefKeys.pref_key_isFirstStartup,true)
        isLogin = sharedPref.getBoolean(AppPreferences.PrefKeys.pref_key_isLogin,false)

        handler = Handler()
        runnable = Runnable {
            if(isFirstStartup){
                ARouter.getInstance().build(ARouterPath.ACTIVITY_URL_LOGIN).navigation()
            }else{
                if(isLogin){
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_URL_MAIN).navigation()
                }else{
                    ARouter.getInstance().build(ARouterPath.ACTIVITY_URL_LOGIN).navigation()
                }
            }
            finish()
        }

        asyncText.setPathAnimatorDefaultListener {
            animationEnd {
                asyncText.showFillColorText()
                handler.postDelayed(runnable,1000)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if(isFirstStartup){
            with(sharedPref.edit()) {
                putBoolean(AppPreferences.PrefKeys.pref_key_isFirstStartup,false)
                apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        handler.removeCallbacks(runnable)
    }
}