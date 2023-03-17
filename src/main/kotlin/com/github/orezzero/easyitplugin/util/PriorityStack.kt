package com.github.orezzero.easyitplugin.util

import com.intellij.util.containers.Stack
import java.util.function.Function

class PriorityStack<T>(private val priorityFunction: Function<T, Int>) : Stack<T>() {

    fun popLowerThan(newPriority: Int): T? {
        while (!this.isEmpty()) {
            val pop = pop()
            val currentPriority = priorityFunction.apply(pop)
            if (currentPriority < newPriority) {
                push(pop)
                return pop
            }
        }
        return null
    }
}