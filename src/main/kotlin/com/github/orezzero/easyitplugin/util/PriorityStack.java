package com.github.orezzero.easyitplugin.util;


import com.intellij.util.containers.Stack;

import java.util.function.Function;

public class PriorityStack<T> extends Stack<T> {

    private Function<T, Integer> priorityFunction;

    public PriorityStack(Function<T, Integer> priorityFunction) {
        this.priorityFunction = priorityFunction;
    }

    public T popLowerThan(Integer newPriority) {
        while (!this.isEmpty()) {
            T pop = pop();
            Integer currentPriority = priorityFunction.apply(pop);
            if (currentPriority < newPriority) {
                push(pop);
                return pop;
            }
        }
        return null;
    }
}
