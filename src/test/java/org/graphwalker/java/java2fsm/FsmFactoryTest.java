package org.graphwalker.java.java2fsm;

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


import org.graphwalker.core.model.Model;
import org.graphwalker.java.annotation.resources.fsm.ClientServer;
import org.graphwalker.java.annotation.resources.fsm.Login;
import org.graphwalker.java.annotation.resources.fsm.NewsReader;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * Created by krikar on 9/6/14.
 */
public class FsmFactoryTest {
    @Test
    public void Login() {
        FsmFactory factory = new FsmFactory();
        Model model = factory.create(new Login());
        Assert.assertThat(model.getVertices().size(), is(10));
        Assert.assertThat(model.getEdges().size(), is(24));
        System.out.println(DotFileFactory.createDot(model));
    }

    @Test
    public void NewsReader() {
        FsmFactory factory = new FsmFactory();
        Model model = factory.create(new NewsReader());
        Assert.assertThat(model.getVertices().size(), is(8));
        Assert.assertThat(model.getEdges().size(), is(14));
        System.out.println(DotFileFactory.createDot(model));
    }

    @Test
    public void ClientServer() {
        FsmFactory factory = new FsmFactory();
        Model model = factory.create(new ClientServer());
        Assert.assertThat(model.getVertices().size(), is(24));
        Assert.assertThat(model.getEdges().size(), is(39));
        System.out.println(DotFileFactory.createDot(model));
    }
}
