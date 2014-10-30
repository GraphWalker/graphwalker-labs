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
public class NewsReader implements Serializable {

    // Types for each state variable
    public enum Page { Topics, Messages };
    public enum Style { WithText, TitlesOnly };
    public enum Sort { ByFirst, ByMostRecent };

    // State variables, initial state
    public Page page = Page.Topics;
    public Style style = Style.WithText;
    public Sort sort = Sort.ByMostRecent;


    public boolean OnSelectMessages() {
        return (page == Page.Topics);
    }

    @Transition
    public void SelectMessages() {
        page = Page.Messages;
    }

    public boolean OnSelectTopics() {
        return (page == Page.Messages);
    }

    @Transition
    public void SelectTopics() {
        page = Page.Topics;
    }

    public boolean OnShowTitles() {
        return (page == Page.Topics && style == Style.WithText);
    }

    @Transition
    public void ShowTitles() {
        style = Style.TitlesOnly;
    }

    public boolean OnShowText() {
        return (page == Page.Topics && style == Style.TitlesOnly);
    }

    @Transition
    public void ShowText() {
        style = Style.WithText;
    }

    public boolean OnSortByFirst() {
        return (page == Page.Topics && style == Style.TitlesOnly && sort == Sort.ByMostRecent);
    }

    @Transition
    public void SortByFirst() {
        sort = Sort.ByFirst;
    }

    public boolean OnSortByMostRecent() {
        return (page == Page.Topics && style == Style.TitlesOnly && sort == Sort.ByFirst);
    }

    @Transition
    public void SortByMostRecent() {
        sort = Sort.ByMostRecent;
    }
}
