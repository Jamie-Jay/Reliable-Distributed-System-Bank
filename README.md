# 18749
## Usage

### Start server

```shell
cd ~/src/server
javac Server.java Bank.java
cd .. // return to src diretory
java server.Server 8080 s1// args[0] - portNumber args[1] - Server ID
```

### Start client

```shell
cd ~/src/client
javac Client.java
cd ..  // return to src diretory
java client.Client localhost 8080 1 //args[0]-Server hostName, args[1]-Server portNumber,args[2]-clientID, args[3]-base request Id
```

