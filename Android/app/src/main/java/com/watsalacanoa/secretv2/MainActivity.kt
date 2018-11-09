package com.watsalacanoa.secretv2

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*

import com.watsalacanoa.secretv2.adapters.Content
import com.watsalacanoa.secretv2.models.Post
import com.watsalacanoa.secretv2.models.PostRequest
import com.watsalacanoa.secretv2.services.LocationService
import com.watsalacanoa.secretv2.services.PostService
import com.watsalacanoa.secretv2.services.VerificarRed

class MainActivity : AppCompatActivity() {

    private val elementsArray = ArrayList<String>()
    private val postService = PostService("http://10.43.40.32:5000")
    private val verificarRed = VerificarRed()
    private lateinit var adapter : Content
    private lateinit var locationService : LocationService

    override fun onCreate(savedInstanceState: Bundle?) {


        locationService = LocationService(this.applicationContext!!)

        super.onCreate(savedInstanceState)


        adapter = Content(this, elementsArray)

        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.hide()

        val listView = findViewById<ListView>(R.id.main_listView)
        listView.adapter = adapter

        val btnAddElement = findViewById<View>(R.id.btnAddNewElement)
        btnAddElement.setOnClickListener{v -> showDialog(v)}

        getPosts()
    }

    private fun getPosts() {
        val location = locationService.getCurrentLocation()
        val request = PostRequest(location, 10000000, 0)

        if (!verificarRed.isConnected(this.applicationContext!!)) {
            return
        }

        AsyncTask.execute {
            val posts = postService.getPosts(request)
            posts.continueWith { response ->
                response.result.reversed().forEach { post ->
                    runOnUiThread {
                        adapter.add(post.text)
                    }
                }
            }
        }
    }

    fun showDialog(view:View){
        val mBuilder = AlertDialog.Builder(this)

        val mView = LayoutInflater.from(this).inflate(R.layout.dialog_new_comment, null)
        val mComment = mView.findViewById<EditText>(R.id.idTextNewComment)
        val btnShare = mView.findViewById<Button>(R.id.btnShare)

        mBuilder.setView(mView)

        val alert = mBuilder.create()
        val btnCancel = mView.findViewById<Button>(R.id.btnCancel)

        btnShare.setOnClickListener{
            val txt = mComment.text.toString()
            val location = locationService.getCurrentLocation()
            val post = Post(txt, location)
            if(!verificarRed.isConnected(this.applicationContext)){
                return@setOnClickListener
            }

            AsyncTask.execute {
                postService.createPost(post).continueWith {
                    result -> if(result.result){
                    runOnUiThread { adapter.add(txt) }
                    Toast
                        .makeText(this.applicationContext, "Uploaded", Toast.LENGTH_SHORT)
                        .show()
                }else{
                    Toast
                        .makeText(this.applicationContext, "Upload failed", Toast.LENGTH_SHORT)
                        .show()
                    }
                }
            }

            alert.cancel()
        }

        btnCancel.setOnClickListener {
            alert.cancel()
        }
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }


}
