package com.github.ihsg.demo.util

import android.text.TextUtils
import com.github.ihsg.demo.util.SecurityUtil.encrypt
import com.github.ihsg.demo.util.SharedPreferencesUtil.Companion.instance

open class PatternHelper {

    // http req
    private var httpUtils:HttpUtils? = null

    var message: String? = null
        private set
    private var storagePwd: String? = null
    private var tmpPwd: String? = null
    private var times = 0
    var isFinish = false
        private set
    var isOk = false
        private set
    // 记录已经成功验证的次数
    var cnt = 0

    fun validateForSetting(hitIndexList: List<Int>?) {
        // 只上传不需要
        httpUtils = HttpUtils()

        isFinish = false
        isOk = false
        //illegal PWD
        if (hitIndexList == null || hitIndexList.size < MAX_SIZE) {
            cnt= 0
            tmpPwd = null
            message = sizeErrorMsg
            return
        }

        //1. draw first time
        if (TextUtils.isEmpty(tmpPwd)) {
            tmpPwd = convert2String(hitIndexList)
            message = reDrawMsg
            cnt++
            isOk = true
            return
        }
        //2. draw rest times
        if (tmpPwd == convert2String(hitIndexList)) {
            cnt++
            isOk = true
            if(cnt >= 5){
                message = settingSuccessMsg
                saveToStorage(tmpPwd)
                isFinish = true
            }
            return
        } else {
            cnt = 0
            tmpPwd = null
            message = diffPreErrorMsg
            return
        }

    }

    //hitIndexList为当前用户绘制的密码
    fun validateForChecking(hitIndexList: List<Int>?) {
        httpUtils = HttpUtils()

        isOk = false

        //验证失败
        if (hitIndexList == null || hitIndexList.size < MAX_SIZE) {
            times++
            isFinish = times >= MAX_SIZE
            message = pwdErrorMsg
            return
        }

        //得到设置好的密码
        storagePwd = fromStorage
        // 还需验证声信号通过
        // 通过请求远程服务器来得到api
        // isFiseRight()
        if (!TextUtils.isEmpty(storagePwd) && storagePwd == convert2String(hitIndexList)) {
            //验证成功
            message = checkingSuccessMsg
            isOk = true
            isFinish = true
        } else {
            //验证失败
            times++
            isFinish = times >= MAX_SIZE
            message = pwdErrorMsg
        }
    }

    private val remainTimes: Int
        get() = if (times < 5) MAX_TIMES - times else 0
    private val reDrawMsg: String = "请再次绘制解锁图案"
    //private val reDrawMsg: String = "Please draw the unlock pattern again"
    private val settingSuccessMsg: String = "手势解锁图案设置成功！"
    //private val settingSuccessMsg: String = "Successfully set the pattern password！"
    private val checkingSuccessMsg: String = "解锁成功！"
    //private val checkingSuccessMsg: String = "Successfully unlock！"
    private val sizeErrorMsg: String = String.format("至少连接%d个点，请重新绘制", MAX_SIZE)
    //private val sizeErrorMsg: String = String.format("Connect at least %d points", MAX_SIZE)
    private val diffPreErrorMsg: String = "与上次绘制不一致，请重新绘制"
    //private val diffPreErrorMsg: String = "Inconsistent with the previous drawing, please redraw"
    private val pwdErrorMsg: String
        get() = String.format("密码错误，还剩%d次机会", remainTimes)
        //get() = String.format("Incorrect password, %d chances left", remainTimes)


    private fun convert2String(hitIndexList: List<Int>): String {
        return hitIndexList.toString()
    }

    private fun saveToStorage(gesturePwd: String?) {
        val encryptPwd = encrypt(gesturePwd!!)
        instance!!.saveString(GESTURE_PWD_KEY, encryptPwd)
        // !!断言对象不会为空
    }

    private val fromStorage: String? = instance?.getString(GESTURE_PWD_KEY)?.let { SecurityUtil.decrypt(it) }


    companion object {
        const val MAX_SIZE = 4
        const val MAX_TIMES = 5
        private const val GESTURE_PWD_KEY = "gesture_pwd_key"
    }
}