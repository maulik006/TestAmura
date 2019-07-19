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

class SignupActivity : AppCompatActivity() {

    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var btnSignIn: Button? = null
    var btnSignUp: Button? = null
    var progressBar: ProgressBar? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        btnSignIn = findViewById(R.id.sign_in_button) as Button
        btnSignUp = findViewById(R.id.sign_up_button) as Button
        inputEmail = findViewById(R.id.email) as EditText
        inputPassword = findViewById(R.id.password) as EditText
        progressBar = findViewById(R.id.progressBar) as ProgressBar

        btnSignIn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })
        btnSignUp?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val email = inputEmail?.getText().toString().trim()
                val password = inputPassword?.getText().toString().trim()
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
                    return
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
                    return
                }
                if (password.length < 6) {
                    Toast.makeText(
                        applicationContext,
                        "Password too short, enter minimum 6 characters!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                progressBar?.setVisibility(View.VISIBLE)
                //create user
                auth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this@SignupActivity, object : OnCompleteListener<AuthResult> {
                        override fun onComplete(@NonNull task: Task<AuthResult>) {
                            Toast.makeText(
                                this@SignupActivity,
                                "createUserWithEmail:onComplete:" + task.isSuccessful(),
                                Toast.LENGTH_SHORT
                            ).show()
                            progressBar?.setVisibility(View.GONE)
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(
                                    this@SignupActivity, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                var i = Intent(this@SignupActivity, MainActivity::class.java)
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(i)
                                finish()
                            }
                        }
                    })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        progressBar?.setVisibility(View.GONE);
    }
}
