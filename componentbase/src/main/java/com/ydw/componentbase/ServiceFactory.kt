package com.ydw.componentbase

class ServiceFactory private constructor(){
    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    companion object {
        val instance:ServiceFactory by lazy{Inner.serviceFactory}
    }

    private object Inner {
        val serviceFactory = ServiceFactory()
    }


}