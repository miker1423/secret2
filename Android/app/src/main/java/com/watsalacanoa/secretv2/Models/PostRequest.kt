package com.watsalacanoa.secretv2.models

data class PostRequest(val location:Point, val range:Int, var continuationToken:Int)