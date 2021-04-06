# hasura-metrics-demo
Spring boot application that pulls metrics data from Hasura Pro Metrics Service and notifies a downstream client (eg. Slack, etc,.)

## Pre-requisites

* Access to a Hasura Pro Backend (LUX) service, especially the Metrics service URL and its admin-secret.
* Slack App/Channel to post the error messages and User/Bot token to authenticate with the Slack API ([Reference](https://api.slack.com/authentication/basics)) having correct [scopes](https://api.slack.com/scopes).

## Running the application

* Add the Hasura Metrics service Graphql API URl, Metrics servcie admin secret, Slack USer/Bot token and Slack channel/App id to _application.properties_ file.
* Build and run the Spring boot application.

## How it works!
* The class [ErrorFetchingService.java](https://github.com/SandeepSamba/hasura-metrics-demo/blob/main/src/main/java/com/hasura/errorNotifier/ErrorFetchingService.java) has the  funtion that queries the metrics API to get the errors that occurred in the last 10 seconds. 
* The class [ErrorNotificationJob.java](https://github.com/SandeepSamba/hasura-metrics-demo/blob/main/src/main/java/com/hasura/errorNotifier/ErrorNotificationJob.java) has the scheduler that runs the above query every 10 seconds (Configurable in code). For Non empty responses , it calls the `notify` method in  [SlackNotifier.java](https://github.com/SandeepSamba/hasura-metrics-demo/blob/main/src/main/java/com/hasura/errorNotifier/SlackNotifier.java) to post the error to Slack.

## Extending for other metrics
* The metrics service of LUX is itself a Hasura graphql engine and the Hasura console is exposed (by default) on port 8080 of the container/pod. The `graphiql` view of metrics service is self-documenting about the metrics exposed as graphql-apis. They can be consumed just like any other Graphql resources from other applications. 
    * An easy way to access the metrics service's console is to use `kubectl port-forward` ([Reference](https://kubernetes.io/docs/tasks/access-application-cluster/port-forward-access-application-cluster/#forward-a-local-port-to-a-port-on-the-pod)). To map local port 8090  to port 8080 of the metrics pod (by default has name matching the wildcard `metrics-*`, for example `metrics-54ffd6d96d-k5c6h`), use `kubectl port-forward metrics-54ffd6d96d-k5c6h 8090:8080` . Now the Hasura console for metrics is availble at `http://localhost:8090` .