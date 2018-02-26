package kr.or.payot.poin.Activities.Splash

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import kr.or.payot.poin.Activities.BaseActivity
import kr.or.payot.poin.Activities.Login.LoginActivity
import kr.or.payot.poin.App
import kr.or.payot.poin.R

/**
 * Created by yongheekim on 2018. 2. 15..
 */
class SplashActivity : BaseActivity() {

    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preference = App.component.sharedPreference()

        // 모든 권한이 승인 되었다면
        if (allowPermission()) {
            autoLogin()
        } else {
            // 권한을 요청
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 4000)
        }
    }

    // 모든 권한이 승인되었는지 확인
    fun allowPermission(): Boolean {
        val camera = checkPermission(Manifest.permission.CAMERA)
        val location = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        return camera && location
    }

    // 권한 체크
    fun checkPermission(permission: String): Boolean =
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 4000) {
            val allowPermissions = grantResults.map { it == PackageManager.PERMISSION_GRANTED }
                    .filter { it }.size

            if (allowPermissions != 2) {
                // 권한을 획득하였다면
                autoLogin()
            } else {
                // 권한을 획득하지 못했다면
                finish()
            }
        }
    }

    // 자동 로그인
    fun autoLogin() {
        if (preference.getBoolean("auto_login", false)) {
            // 로그인이 되었다면 메인으로

            // 그렇지 않다면 로그인페이지로
        } else {
            // 로그인 페이지로
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}