The `FsmFactory` is inspired by the NModel project from Microsoft Research, https://nmodel.codeplex.com/, which later developed into the SpecExplorer. `FsmFactory` does only one thing: It creates a finite state chart from a class using the `@Transition` annotaion.

Consider following code: (See also book Model-based Software Testing and Analysis with C#, the example with Client and Server. http://www.cambridge.org/se/academic/subjects/computer-science/software-engineering-and-development/model-based-software-testing-and-analysis-c?format=PB)
~~~java
        FsmFactory factory = new FsmFactory();
        Model model = factory.create(new ClientServer());
        System.out.println(DotFileFactory.createDot(model));
~~~
and where the `ClientServer` class looks like:
~~~java
public class ClientServer implements Serializable {

    public enum Socket { None, Created, Bound, Listening, Connecting,
        Connected, Disconnected, Closed }

    public enum Phase { Send, ServerReceive, ClientReceive }

    // Control state
    public Socket serverSocket = Socket.None;
    public Socket clientSocket = Socket.None;
    public Phase phase = Phase.Send;

    // Server enabling conditions and actions
    public boolean OnServerSocket() {
        return (serverSocket == Socket.None);
    }

    @Transition
    public void ServerSocket() {
        serverSocket = Socket.Created;
    }

    public boolean OnServerBind() {
        return (serverSocket == Socket.Created);
    }

    @Transition
    public void ServerBind()
    {
        serverSocket = Socket.Bound;
    }

    public boolean OnServerListen()
    {
        return (serverSocket == Socket.Bound);
    }

    @Transition
    public void ServerListen()
    {
        serverSocket = Socket.Listening;
    }

    public boolean OnServerAccept()
    {
        return (serverSocket == Socket.Listening
            && clientSocket == Socket.Connecting);
    }

    @Transition
    public void ServerAccept()
    {
        serverSocket = Socket.Connected; clientSocket = Socket.Connected;
    }

    public boolean OnServerReceive()
    {
        return (serverSocket == Socket.Connected
            && phase == Phase.ServerReceive);
    }


    // No parameter needed here, client always sends same thing
    @Transition
    public void ServerReceive()
    {
        phase = Phase.Send;
    }

    public boolean OnServerSend()
    {
        return (serverSocket == Socket.Connected
            && phase == Phase.Send
            && clientSocket == Socket.Connected);
    }

    // Parameter here, server can send different temperatures
    @Transition
    public void ServerSend()
    {
        phase = Phase.ClientReceive;
    }

    public boolean OnServerCloseConnection()
    {
        return (serverSocket == Socket.Connected);
    }

    @Transition
    public void ServerCloseConnection()
    {
        serverSocket = Socket.Disconnected;
    }

    // Prevent Client crashing - does sending to closed partner crash?
    public boolean OnServerClose()
    {
        return (serverSocket != Socket.None
            // && serverSocket != Socket.Listening
            && serverSocket != Socket.Connected
            && serverSocket != Socket.Closed);
    }

    @Transition
    public void ServerClose()
    {
        serverSocket = Socket.Closed;
    }

    // Client enabling conditions and actions

    public boolean OnClientSocket()
    {
        return (clientSocket == Socket.None);
    }

    @Transition
    public void ClientSocket()
    {
        clientSocket = Socket.Created;
    }

    public boolean OnClientConnect()
    {
        return (clientSocket == Socket.Created
            && serverSocket == Socket.Listening);
    }

    @Transition
    public void ClientConnect()
    {
        clientSocket = Socket.Connecting;
    }

    public boolean OnClientSend()
    {
        return (clientSocket == Socket.Connected
            && phase == Phase.Send);
    }

    // No parameter needed here, client always sends the same thing
    @Transition
    public void ClientSend()
    {
        phase = Phase.ServerReceive;
    }

    public boolean OnClientReceive()
    {
        return (clientSocket == Socket.Connected
            && phase == Phase.ClientReceive);
    }

    // Return value needed here, server sends different values
    @Transition
    public void ClientReceive()
    {
        phase = Phase.Send;
    }

    public boolean OnClientClose()
    {
        return (clientSocket == Socket.Connected
            && phase == Phase.Send);
    }

    @Transition
    public void ClientClose() {
        clientSocket = Socket.Closed;
    }
}
~~~
The `FsmFactory` will create a state chart, when written and visialized by graphviz and dot, will look like:
(using `dot -Tsvg clientserver.dot > login.svg`)
![Alt text](https://raw.githubusercontent.com/GraphWalker/graphwalker-labs/master/doc/img/clientServer.png)

