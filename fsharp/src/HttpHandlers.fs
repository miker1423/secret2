module Handlers

open Giraffe
open Microsoft.AspNetCore.Http
open FSharp.Control.Tasks.V2.ContextInsensitive

open Models
open MongoDB.Driver
open Database
open MongoDB.Bson
open Microsoft.Azure.CognitiveServices.Language.TextAnalytics
open Analyzer

let helloWorld = 
    fun (next: HttpFunc) (ctx: HttpContext) -> 
        task {
            let post = { text="Hello"; location={ lat=12.12; long=12.12}; id=ObjectId.Empty; score=0.0 }
            return! json post next ctx
        }

let getPosts =
    fun (next : HttpFunc) (ctx : HttpContext) ->
        task {
            let! model = ctx.BindModelAsync<PostRequest>()
            let collection = ctx.GetService<IMongoCollection<Post>>()
            let! results = search(collection, model.location, model.range, model.continuationToken)
            let newToken = if(results.Length < 10) then 0 else model.continuationToken + results.Length
            let results = results |> List.map(fun x -> { text=x.text; location=x.location})
            let response = { posts = results; continuationToken = newToken }
            return! json response next ctx
        }

let createPost =
    fun (next : HttpFunc) (ctx : HttpContext) ->
        task {
            ctx.SetStatusCode 201
            let! model = ctx.BindModelAsync<AndroidPost>()
            let collection = ctx.GetService<IMongoCollection<Post>>()
            let analyzer = ctx.GetService<ITextAnalyticsClient>()
            let! result = getScore(analyzer, model.text)
            let dbModel = { text=model.text; location=model.location; id=ObjectId.Empty; score=result }
            create(collection, dbModel) |> ignore
            return! json model next ctx
        }