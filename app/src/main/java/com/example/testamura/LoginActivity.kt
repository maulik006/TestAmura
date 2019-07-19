package com.example.testamura

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var auth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    var btnSignup: Button? = null
    var btnLogin: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if (auth?.getCurrentUser() != null) {
            var i = Intent(this@LoginActivity, MainActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            finish()
        }
        // set the view now
        setContentView(R.layout.activity_login)
        /*val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        */
        inputEmail = findViewById(R.id.email) as EditText
        inputPassword = findViewById(R.id.password) as EditText
        progressBar = findViewById(R.id.progressBar) as ProgressBar
        btnSignup = findViewById(R.id.btn_signup) as Button
        btnLogin = findViewById(R.id.btn_login) as Button
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
        btnSignup?.setOnClickListener { startActivity(Intent(this@LoginActivity, SignupActivity::class.java)) }
        btnLogin?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val email = inputEmail?.getText().toString()
                val password = inputPassword?.getText().toString()
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show()
                    return
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show()
                    return
                }
                progressBar?.visibility = View.VISIBLE
                //authenticate user
                auth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this@LoginActivity, object : OnCompleteListener<AuthResult> {
                        override fun onComplete(@NonNull task: Task<AuthResult>) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            progressBar?.setVisibility(View.GONE)
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length < 6) {
                                    inputPassword?.setError(getString(R.string.minimum_password))
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        getString(R.string.auth_failed),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                var i = Intent(this@LoginActivity, MainActivity::class.java)
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(i)
                                finish()
                            }
                        }
                    })
            }
        })
    }
}
