package com.example.bloggerapp

import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bloggerapp.Model.userData
import com.example.bloggerapp.databinding.ActivitySignInAndRegistrationBinding
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class signInAndRegistrationActivity : AppCompatActivity() {
    private val binding:ActivitySignInAndRegistrationBinding by lazy {
        ActivitySignInAndRegistrationBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var storage:FirebaseStorage
    private val PICK_IMAGE_REQUEST =1
    private var imageUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Initialize firebase authentication
        auth=FirebaseAuth.getInstance()   //you can create users here
        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // for visibility of fields
        val action= intent.getStringExtra("action")
        //adjust visibility and for login
        if(action=="login"){
            binding.loginEmailAddress.visibility=View.VISIBLE
            binding.loginPassword.visibility=View.VISIBLE
            binding.loginButton.visibility=View.VISIBLE
            binding.registerButton.isEnabled=false
            binding.registerButton.alpha=0.5f
            binding.registerNewHere.isEnabled=false
            binding.registerNewHere.alpha=0.5f
            binding.registerEmail.visibility=View.GONE
            binding.registerPassword.visibility=View.GONE
            binding.cardView.visibility=View.GONE
            binding.registerName.visibility=View.GONE
        }
        else if(action=="register"){
            binding.loginButton.isEnabled=false
            binding.loginButton.alpha=0.5f

            binding.registerButton.setOnClickListener {
                //get data from edittext field
                val registerEmail=binding.registerEmail.text.toString()
                val registerPassword=binding.registerPassword.text.toString()
                val registerName=binding.registerName.text.toString()

                if(registerName.isEmpty() ||registerPassword.isEmpty() ||registerPassword.isEmpty()){
                    Toast.makeText(this,"Please fill all the details",Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.createUserWithEmailAndPassword(registerEmail,registerPassword)
                        .addOnCompleteListener { task->
                            if(task.isSuccessful){
                                val user =auth.currentUser
                                user?.let {

                                    //save user data into Firebase realtime database
                                    val userReference=database.getReference("users")
                                    val userId=user.uid
                                    val userData=userData(registerName,registerEmail)
                                    userReference.child(userId).setValue(userData)

                                        .addOnSuccessListener{
                                            Log.d("TAG","onCreate: data saved")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("TAG","onCreate:Error saving data ${e.message}")
                                        }
                                    //upload image to firebase storage
                                    val storageReference=storage.reference.child("profile_image/$userId.jpg")
                                    storageReference.putFile(imageUri!!)
                                    Toast.makeText(this,"User Registered Successfully",Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                Toast.makeText(this,"User Registration Failed",Toast.LENGTH_SHORT).show()
                            }
                        }

                    // set on clickListener for choose image
                    binding.cardView.setOnClickListener {
                        var intent =Intent()
                        intent.type="image/*"
                        intent.action=Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent,"select image"),
                            PICK_IMAGE_REQUEST
                        )
                    }
                }
            }

        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if(resultCode==PICK_IMAGE_REQUEST &&resultCode== RESULT_OK && data!=null &&data.data!=null)
            imageUri=data.data
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.registerUserImage)
    }
}