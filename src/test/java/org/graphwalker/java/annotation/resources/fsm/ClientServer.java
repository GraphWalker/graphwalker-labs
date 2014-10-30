package org.graphwalker.java.annotation.resources.fsm;

/*
 * #%L
 * GraphWalker Java
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import org.graphwalker.java.annotation.Transition;

import java.io.Serializable;

/**
 * Created by krikar on 9/8/14.
 */

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
