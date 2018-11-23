# secret2

### Installation
The app and the environment needed for its execution are stored inside a container through Docker. We first need to clone the repository and modify the Database.fs file, you will need to fill all the params for either MongoDB or CosmosDB. You will need to create an Azure account and get a subscription to cognitive services, specifically the text analysis API, you can find the tutorial here, after getting the API key, it should be placed at the Program.fs file at line 45, and if you choose another datacenter then south central US, you should change the URL at line 47.
Furthermore, you need to build the docker file, which is located at fsharp/src/, the command to build is:
```
docker build -t secret2-backend
```
After building, you need to run the container with the following command:
```
docker run -d -p 80:5000 --name secret2 secret2-backend
```
