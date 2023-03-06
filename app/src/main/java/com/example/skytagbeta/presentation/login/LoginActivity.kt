package com.example.skytagbeta.presentation.login

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.skytagbeta.R
import com.example.skytagbeta.presentation.login.model.LoginUserInfo
import com.example.skytagbeta.base.utils.IdentifierKey
import com.example.skytagbeta.base.utils.showToast
import com.example.skytagbeta.databinding.ActivityLoginBinding
import com.example.skytagbeta.presentation.login.viewmodel.LoginViewModel
import com.example.skytagbeta.presentation.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import io.paperdb.Paper


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels()
    private val identifierKey = IdentifierKey()
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){ }
    }
    private lateinit var identifierKey1: String
    private lateinit var identifierKey2: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)

        setupTextFlied()

        identifierKey1 = identifierKey.getIdentifierKey(this).take(10) // Para enviar Coordenadas
        identifierKey2 = identifierKey.getIdentifierKey(this) // Para copiar y mandar

        checkLogin()

        binding.btnLogin.setOnClickListener {

            if(checkAllRequiredPermissions()){
                login()
            }else{
                showToast(this, getString(R.string.loginactivity_permissions_toas))
            }
        }

        binding.tvIdentificador.setOnClickListener { copyToClipboard() }

    }

    private fun copyToClipboard() {
        val clipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
        val clip = ClipData.newPlainText("Id Compuesto", identifierKey2)
        clipboardManager.setPrimaryClip(clip)
        Toast.makeText(this, "Identificador Copiado", Toast.LENGTH_SHORT).show()
        Log.i("Identificador", identifierKey2)
    }

    private fun checkLogin() {
        val active = Paper.book().read<Boolean>("active") ?: false
        if (active){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else {
            val remember = Paper.book().read<Boolean>("remember") ?: false
            if (remember){
                binding.etUser.text = Paper.book().read("user")
                binding.etPassword.text = Paper.book().read("contrasena")
                binding.cbRemember.isChecked = true
            }
        }
    }

    private fun login() {
        binding.pbProbresBar.visibility = View.VISIBLE

        val user = binding.etUser.text.toString()
        val password = binding.etPassword.text.toString()

        if (binding.cbRemember.isChecked){
            Paper.book().write("user", user)
            Paper.book().write("contrasena", password)
            Paper.book().write("remember", true)
        }else{
            Paper.book().write("user", user)
            Paper.book().write("contrasena", password)
            Paper.book().write("remember", false)
        }

        if (validateFields(binding.tilUser, binding.tilPassword)){
            mLoginViewModel.onLogin(LoginUserInfo(mensaje = "usuario", usuario = user, contrasena = password))


            mLoginViewModel.loginModel.observe(this){
                when(it.estado){

                    200 ->{
                        Toast.makeText(this, "Welcome ${it.usuario.usuario}", Toast.LENGTH_SHORT).show()
                        Paper.book().write("user", user)
                        Paper.book().write("contrasena", password)
                        Paper.book().write("identificador", identifierKey1)
                        Paper.book().write("active", true)
                        binding.pbProbresBar.visibility = View.GONE

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }

                    400 ->{
                        Toast.makeText(this, getString(R.string.loginactivity_login_error), Toast.LENGTH_SHORT).show()
                        binding.pbProbresBar.visibility = View.GONE

                        Paper.book().write("active", false)
                    }
                    else -> {Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                        Paper.book().write("active", false)
                        binding.pbProbresBar.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun checkAllRequiredPermissions(): Boolean {

        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION)
        }

        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, requiredPermissions, 111)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            111 -> finishIfRequiredPermissionsNotGranted(grantResults)
            else -> {
            }
        }
    }

    private fun finishIfRequiredPermissionsNotGranted(grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {

                if (grantResult == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }

                } else {

                    Toast.makeText(this, getString(R.string.loginactivity_all_permission_be_required), Toast.LENGTH_LONG).show()
                    finish()
                    break
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.loginactivity_all_permission_be_required), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }

        if (!isValid)
            showToast(this, getString(R.string.edit_store_message_valid))
        binding.pbProbresBar.visibility = View.GONE

        return isValid
    }

    private fun setupTextFlied(){
        with(binding){
            etUser.addTextChangedListener { validateFields(tilUser) }
            etPassword.addTextChangedListener { validateFields(tilPassword) }
        }
    }
}