package com.watsalacanoa.secretv2.services

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson

import com.watsalacanoa.secretv2.models.Post
import com.watsalacanoa.secretv2.models.PostRequest
import com.watsalacanoa.secretv2.models.PostResponse

/**
 * Created by CLN-BRA on 05/09/2018.
 */

class PostService (private val defaultUrl: String){
    private val gson = Gson()
    private var token = 0

    fun createPost(post: Post): Task<Boolean> {
        val future = TaskCompletionSource<Boolean>()
        val connection = URL("$defaultUrl/posts").openConnection() as HttpURLConnection
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.connect()
        val json = gson.toJson(post).toByteArray(Charsets.UTF_8)
        connection.outputStream.write(json)

        future.setResult(connection.responseCode == 201)

        return future.task
    }

    fun getPosts(request:PostRequest): Task<List<Post>> {
        val future = TaskCompletionSource<List<Post>>()
        request.continuationToken = token

        val connection = URL("$defaultUrl/posts").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.connect()
        val json = gson.toJson(request).toByteArray(Charsets.UTF_8)
        connection.outputStream.write(json)
        if(connection.responseCode == 200) {
            val str = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val response = gson.fromJson(str,PostResponse::class.java)

            token = response.continuationToken
            future.setResult(response.posts)
        } else {
            token = 0
            future.setResult(null)
        }

        return future.task
    }
}