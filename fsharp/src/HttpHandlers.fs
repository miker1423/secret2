module Handlers

open Giraffe
open Microsoft.AspNetCore.Http
open FSharp.Control.Tasks.V2.ContextInsensitive

open Models
open MongoDB.Driver
open Database

let getPosts =
    fun (next : HttpFunc) (ctx : HttpContext) ->
        task {
            let! model = ctx.BindModelAsync<PostRequest>()
            let collection = ctx.GetService<IMongoCollection<Post>>()
            let! results = search(collection, model.location, model.range, model.continuationToken)
            let newToken = if(results.Length < 10) then 0 else model.continuationToken + results.Length
            let response = { posts = results; continuationToken = newToken }
            return! json response next ctx
        }

let createPost =
    fun (next : HttpFunc) (ctx : HttpContext) ->
        task {
            ctx.SetStatusCode 201
            let! model = ctx.BindModelAsync<Post>()
            let collection = ctx.GetService<IMongoCollection<Post>>()
            create(collection, model) |> ignore
            return! json model next ctx
        }