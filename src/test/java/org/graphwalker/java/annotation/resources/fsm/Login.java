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
 * Created by krikar on 9/6/14.
 */
public class Login implements Serializable {

    // Types for each state variable
    public enum Credentials { Valid, Invalid };
    public enum Phase { Stopped, Starting, Started };

    // State variables, initial state
    public Credentials credentials = Credentials.Invalid;
    public boolean rememberMe = false;
    public Phase phase = Phase.Stopped;


    public boolean OnStart() {
        return (phase == Phase.Stopped);
    }

    @Transition
    public void Start() {
        if (rememberMe && credentials == Credentials.Valid) {
            phase = Phase.Started;
        } else {
            phase = Phase.Starting;
        }
    }

    public boolean OnRememberMe() {
        return (phase == Phase.Starting);
    }

    @Transition
    public void RememberMe() {
        rememberMe = !rememberMe;
    }

    public boolean OnInValidLogin() {
        return (phase == Phase.Starting);
    }

    @Transition
    public void InValidLogin() {
        credentials = Credentials.Invalid;
    }

    public boolean OnValidLogin() {
        return (phase == Phase.Starting);
    }

    @Transition
    public void ValidLogin() {
        credentials = Credentials.Valid;
        phase = Phase.Started;
    }

    public boolean OnLogout() {
        return (phase == Phase.Started);
    }

    @Transition
    public void Logout() {
        credentials = Credentials.Invalid;
        phase = Phase.Starting;
    }

    public boolean OnExit() {
        return (phase == Phase.Started || phase == Phase.Starting );
    }

    @Transition
    public void Exit() {
        phase = Phase.Stopped;
    }
}
