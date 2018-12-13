package com.ydw.base



class AppPreferences {
    companion object {
        var isFirstStartup = true//是否第一次启动应用
        var isLogin = false//是否已登录
    }

    class PrefKeys{
        companion object {
            const val pref_key_isFirstStartup = "pref_key_isFirstStartup"
            const val pref_key_isLogin = "pref_key_isLogin"
        }
    }
}