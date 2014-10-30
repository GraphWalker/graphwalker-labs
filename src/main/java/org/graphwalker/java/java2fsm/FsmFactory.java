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

import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.java.annotation.Transition;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by krikar on 9/6/14.
 */
public class FsmFactory {
    private List<Map.Entry<Method, Boolean>> visitedMethods = new ArrayList<>();
    private Model model = null;

    public Model create(Object object) {
        model = new Model();
        List<Map.Entry<Method, Method>> guardMethodList = getEntries(object);
        execute(object, guardMethodList);
        return model;
    }

    private List<Map.Entry<Method, Method>> getEntries(Object object) {
        Method[] methods = object.getClass().getMethods();
        List<Map.Entry<Method, Method>> guardMethodList = new ArrayList<>();

        for (Method method : methods) {
            Transition transition = method.getAnnotation(Transition.class);
            if (transition != null) {
                Method[] guards = object.getClass().getMethods();
                for (Method guard : methods) {
                    String guardName = "On" + method.getName();
                    if (guard.getName().equals(guardName)) {
                        Map.Entry<Method, Method> pair = new AbstractMap.SimpleEntry<>(guard, method);
                        guardMethodList.add(pair);
                        Map.Entry<Method, Boolean> visitedPair = new AbstractMap.SimpleEntry<>(method, false);
                        visitedMethods.add(visitedPair);
                    }
                }
            }
        }
        return guardMethodList;
    }

    private void execute(Object object, List<Map.Entry<Method, Method>> guardMethodList) {
        List<Method> availableTransitions = getAvailableMethods(object, guardMethodList);
        for (Method method : availableTransitions) {

            byte[] snapshot = getSnapShot(object);
            Edge e = new Edge().setName(method.getName()).setSourceVertex(getVertex(object));
            try {
                method.invoke(object);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }

            if (existStateInModel(object)) {
                e.setTargetVertex(getVertex(object));
                addEdge(e);
                object = restoreSnapShot(object, snapshot);
                continue;
            } else {
                e.setTargetVertex(getVertex(object));
                addEdge(e);
                execute(object, guardMethodList);
            }

            object = restoreSnapShot(object, snapshot);
        }
    }

    private Object restoreSnapShot(Object object, byte[] snapshot) {
        try {
            object = Serializer.deserialize(snapshot);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return object;
    }

    private byte[] getSnapShot(Object object) {
        byte[] snapshot = null;
        try {
            snapshot = Serializer.serialize(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snapshot;
    }

    private void addEdge(Edge edge) {
        Boolean hasEdge = false;
        for (Edge e : model.getEdges()) {
            if (e.getSourceVertex().getId() == edge.getSourceVertex().getId() &&
                e.getTargetVertex().getId() == edge.getTargetVertex().getId() &&
                e.getName().equals(edge.getName())) {
                hasEdge = true;
            }
        }
        if (hasEdge == false) {
            model.addEdge(edge);
        }
    }

    private boolean existStateInModel(Object object) {
        Integer hash = getHash(object);
        for (Vertex v : model.getVertices()) {
            if (v.getId().equals(hash.toString())) {
                return true;
            }
        }
        return false;
    }

    private Vertex getVertex(Object object) {
        Vertex candidate = new Vertex().setId(getHash(object).toString()).setName(getObjectAttributes(object));
        for (Vertex v : model.getVertices()) {
            if (v.getId().equals(candidate.getId())) {
                return v;
            }
        }
        model.addVertex(candidate);
        return candidate;
    }

    private List<Method> getAvailableMethods(Object object, List<Map.Entry<Method, Method>> guardMethodList) {
        List<Method> availableTransitions = new ArrayList<>();

        for (Map.Entry<Method, Method> pair : guardMethodList) {
            try {
                if ((boolean) pair.getKey().invoke(object)) {
                    availableTransitions.add(pair.getValue());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return availableTransitions;
    }

    private String getObjectAttributes(Object object) {
        String attributes = "";
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // You might want to set modifier to public first.
            Object value = null;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null) {
                attributes += field.getName() + "=" + value + System.lineSeparator();
            }
        }
        return attributes;
    }

    private Integer getHash(Object object) {
        byte[] snapshot = getSnapShot(object);
        return Arrays.hashCode(snapshot);
    }
}
