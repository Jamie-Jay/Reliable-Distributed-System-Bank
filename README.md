# 18749
## Usage

### Start acitve server

```shell
team.group26.activeReplica.PrimaryServer
8080 s1 // args[0] - portNumber args[1] - Server ID
```
### Start backup passive server

```shell
team.group26.activeReplica.PrimaryServer
8082 s2 PASSIVE // args[0] - portNumber args[1] - Server ID
```

### Start primary passive server

```shell
team.group26.activeReplica.PrimaryServer
8081 S1 localhost 9001 9002 3000 1000 // args[0] - portNumber args[1] - Server ID args[2] - Passive Server HostName args[4] - Passive Server1 portNumber args[5] - Passive Server2 portNumber args[6] - SyncUp interval args[7] - SyncUp timeout
```

### Start client

```shell
team.group26.client.Client
localhost 8080 8081 8082 C1 100 //args[0]-Server hostName, args[1]-Server1 portNumber, args[2]-Server2 portNumber, args[3]-Server3 portNumber, args[4]-client ID, args[5]-base request Id
```

### Start LFD

```shell
team.group26.activeReplica.LocalFaultDetector
8081 localhost 3000 1000 S1 LFD1 9091 localhost //args[0]-Server portNumber, args[1]-Server1 hostName, args[2]-interval, args[3]-timeout, args[4]-Server ID, args[5]-LFD ID, args[6]-GFD portNumber, args[7]-GFD hostName
```

### Start GFD

```shell
team.group26.globalFaultDetector.GFD
9091 //args[0]- portNumber
```

