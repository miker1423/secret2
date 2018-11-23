module Analyzer

open Microsoft.Azure.CognitiveServices.Language.TextAnalytics
open Microsoft.Azure.CognitiveServices.Language.TextAnalytics.Models
open Microsoft.Rest
open System.Net.Http
open System.Collections.Generic
open System.Threading
open System.Threading.Tasks
open FSharp.Control.Tasks.V2.ContextInsensitive

type ApiKeyServiceClientCredentials(credential:string) = 
    inherit ServiceClientCredentials()

    override this.ProcessHttpRequestAsync(request: HttpRequestMessage, cancellationToken: CancellationToken) : Task =
        request.Headers.Add("Ocp-Apim-Subscription-Key", credential)
        base.ProcessHttpRequestAsync(request, cancellationToken)
    
let getScore(client: ITextAnalyticsClient, message: string) =
    let batch = new MultiLanguageBatchInput()
    batch.Documents <- new List<MultiLanguageInput>()
    batch.Documents.Add(new MultiLanguageInput("es", "0", message))
    task {
        let! result = client.SentimentWithHttpMessagesAsync(batch)
        let documentScore = result.Body.Documents.[0].Score
        let score = if documentScore.HasValue then documentScore.Value else 0.0
        return score
    }