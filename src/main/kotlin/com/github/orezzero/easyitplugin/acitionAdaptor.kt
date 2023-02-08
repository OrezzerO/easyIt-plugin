package com.github.orezzero.easyitplugin


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.util.Computable
import org.jetbrains.annotations.ApiStatus.Internal


// TODO: this file is same as actions.kt in idea-community
// TODO find out why "Cannot inline bytecode built with JVM target 17 into bytecode that is being built with JVM target
//  11. Please specify proper '-jvm-target' option" and delete this file
inline fun <T> runWriteAction(crossinline runnable: () -> T): T {
    return ApplicationManager.getApplication().runWriteAction(Computable { runnable() })
}

inline fun <T> runUndoTransparentWriteAction(crossinline runnable: () -> T): T {
    return computeDelegated {
        CommandProcessor.getInstance().runUndoTransparentAction {
            ApplicationManager.getApplication().runWriteAction(Runnable { it(runnable()) })
        }
    }
}

inline fun <T> runReadAction(crossinline runnable: () -> T): T {
    return ApplicationManager.getApplication().runReadAction(Computable { runnable() })
}

/**
 * @suppress Internal use only
 */
@Internal
fun <T> invokeAndWaitIfNeeded(modalityState: ModalityState? = null, runnable: () -> T): T {
    val app = ApplicationManager.getApplication()
    if (app.isDispatchThread) {
        return runnable()
    } else {
        return computeDelegated {
            app.invokeAndWait({ it(runnable()) }, modalityState ?: ModalityState.defaultModalityState())
        }
    }
}

@PublishedApi
internal inline fun <T> computeDelegated(executor: (setter: (T) -> Unit) -> Unit): T {
    var resultRef: T? = null
    executor { resultRef = it }
    @Suppress("UNCHECKED_CAST")
    return resultRef as T
}

inline fun runInEdt(modalityState: ModalityState? = null, crossinline runnable: () -> Unit) {
    val app = ApplicationManager.getApplication()
    if (app.isDispatchThread) {
        runnable()
    } else {
        invokeLater(modalityState, runnable)
    }
}

inline fun invokeLater(modalityState: ModalityState? = null, crossinline runnable: () -> Unit) {
    ApplicationManager.getApplication()
        .invokeLater({ runnable() }, modalityState ?: ModalityState.defaultModalityState())
}
