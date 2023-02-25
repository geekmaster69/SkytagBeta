package com.example.skytagbeta.presentation.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytagbeta.base.utils.IdentifierKey
import com.example.skytagbeta.databinding.ActivityLoginBinding
import com.example.skytagbeta.presentation.login.viewmodel.LoginViewModel
import com.example.skytagbeta.presentation.main.MainActivity
import io.paperdb.Paper


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val mLoginViewModel: LoginViewModel by viewModels()
    private val identifierKey = IdentifierKey()
    private lateinit var identifierKey1: String
    private lateinit var identifierKey2: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)

        identifierKey1 = identifierKey.getIdentifierKey(this).take(10) // Para enviar Coordenadas
        identifierKey2 = identifierKey.getIdentifierKey(this) // Para copiar y mandar

        checkLogin()

        binding.btnLogin.setOnClickListener {
            login()

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

      mLoginViewModel.onLogin(LoginUserInfo(mensaje = "usuario", usuario = user, contrasena = password))

        mLoginViewModel.loginModel.observe(this){
            when(it.estado){

                200 ->{
                    Toast.makeText(this, "Bienvenido ${it.usuario.usuario}", Toast.LENGTH_SHORT).show()
                    Paper.book().write("user", user)
                    Paper.book().write("contrasena", password)
                    Paper.book().write("identificador", identifierKey1)
                    Paper.book().write("active", true)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                400 ->{
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()

                    Paper.book().write("active", false)
                }
                else -> Toast.makeText(this, "Error desconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}