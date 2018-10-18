module Models
open MongoDB.Bson

[<CLIMutable>]
type Point = { lat:double; long:double }

[<CLIMutable>]
type Post = { id:ObjectId; text:string; location:Point }

[<CLIMutable>]
type PostRequest = { location:Point; range:float; continuationToken:int }

[<CLIMutable>]
type PostResponse = { posts:Post list; continuationToken:int }