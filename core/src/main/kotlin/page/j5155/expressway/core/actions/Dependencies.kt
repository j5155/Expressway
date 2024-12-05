@file:JvmName("Dependencies")
package page.j5155.expressway.core.actions

import com.acmerobotics.roadrunner.Action

interface Interruptible : Action {
    fun onInterrupt()
}

interface Dependency {
    var inUse: Boolean
}

interface Dependent : Interruptible {
    val dependencies: MutableList<Dependency>

    fun initializeDependencies() {
        dependencies.forEach { it.inUse = true }
    }

    fun resolve() {
        onInterrupt()
        dependencies.forEach { it.inUse = false }
    }
}