# 18749
## Usage

### Start acitve server

```shell
cd ~/src/server
javac Server.java Bank.java
cd .. // return to src diretory
java server.Server 8080 s1// args[0] - portNumber args[1] - Server ID
```
### Start passive server

```shell
cd ~/src/server
javac Server.java Bank.java
cd .. // return to src diretory
java server.Server 8082 s2 PASSIVE// args[0] - portNumber args[1] - Server ID
```

### Start primary passive server

```shell
cd ~/src/server
javac Server.java Bank.java
cd .. // return to src diretory
java server.Server 8081 S1 localhost 9001 9002 3000 1000// args[0] - portNumber args[1] - Server ID args[2] - Passive Server HostName args[4] - Passive Server1 portNumber args[5] - Passive Server2 portNumber args[6] - SyncUp interval args[7] - SyncUp timeout
```

### Start client

```shell
cd ~/src/client
javac Client.java
cd ..  // return to src diretory
java client.Client localhost 8080 8081 8082 C1 100 //args[0]-Server hostName, args[1]-Server1 portNumber, args[2]-Server2 portNumber, args[3]-Server3 portNumber, args[4]-clientID, args[5]-base request Id
```

### Start LFD

```shell
8081 localhost 3000 1000 S1 LFD1 9091 localhost //args[0]-Server portNumber, args[1]-Server1 hostName, args[2]-interval, args[3]-timeout, args[4]-Serve ID, args[5]-LFD ID, args[6]-GFD portNumber, args[7]-GFD hostName
```

### Start GFD

```shell
9091 //args[0]- portNumber
```

