module Database

open Models
open MongoDB.Driver
open System.Security.Authentication
open FSharp.Control.Tasks.V2.ContextInsensitive
open System


let private addCredentials(settings: MongoClientSettings) =
    let identity = new MongoInternalIdentity("secret", "localhost")
    let evidence = new PasswordEvidence("C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==")
    settings.Credential <- new MongoCredential("SCRAM-SHA-1", identity, evidence)
    settings

let private getMongoSettings = 
    let settings = new MongoClientSettings()
    settings.Server <- new MongoServerAddress("localhost", 10255)
    settings.UseSsl <- true
    settings.SslSettings <- new SslSettings()
    settings.SslSettings.EnabledSslProtocols <- SslProtocols.Tls12
    addCredentials settings

let private connectClient = 
    let settings = getMongoSettings
    let client = new MongoClient(settings)
    client.GetDatabase("secret")

let getCollection =
    let db = connectClient
    db.GetCollection<Post>("Posts")

let create (collection:IMongoCollection<Post>, model:Post) =
    task{
        return! collection.InsertOneAsync(model)
    }

let private dist location location2 =
    let toRadians x =
        (x * Math.PI) / 180.0
    
    let R = 6371.0 * 10.0 ** 3.0
    let phi1 = location.lat |> toRadians
    let phi2 = location2.lat |> toRadians

    let deltaPhi1 = (location.lat - location2.lat) |> toRadians
    let deltaPhi2 = (location.long - location2.long) |> toRadians

    let aPt1 = Math.Sin(deltaPhi1/2.0) * Math.Sin(deltaPhi1/2.0)
    let aPt2 = Math.Cos(phi1) * Math.Cos(phi2)
    let aPt3 = Math.Sin(deltaPhi2/2.0) * Math.Sin(deltaPhi2/2.0)

    let a = aPt1 + aPt2 * aPt3

    let c = 2.0 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1.0-a))

    R * c

let private inRange locationA locationB range = 
    let distance = dist locationA locationB
    distance <= range

let search (collection:IMongoCollection<Post>, location:Point, range:float, continuationToken:int) = 
    task {
        let! res = collection.FindAsync(fun x -> true)
        let! prevResults = res.ToListAsync()
        return prevResults 
        |> Seq.where(fun x -> inRange x.location location range )
        |> Seq.skip(continuationToken)
        |> Seq.truncate(10)
        |> Seq.toList
    }