module Models
open MongoDB.Bson

[<CLIMutable>]
type Point = { lat:double; long:double }

[<CLIMutable>]
type Post = { id:ObjectId; text:string; location:Point; score:float }

[<CLIMutable>]
type PostRequest = { location:Point; range:float; continuationToken:int }

[<CLIMutable>]
type AndroidPost = { text:string; location:Point }

[<CLIMutable>]
type PostResponse = { posts:AndroidPost list; continuationToken:int }