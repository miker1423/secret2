package com.watsalacanoa.secretv2.models

data class PostResponse(val posts:List<Post>, val continuationToken:Int)