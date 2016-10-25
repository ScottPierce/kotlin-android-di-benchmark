package kinject

import java.io.Serializable
import java.util.*

inline fun objectGraph(create: ObjectGraph.Builder.() -> Unit): ObjectGraph {
    val builder = ObjectGraph.Builder()
    builder.create()
    return builder.build()
}

class ObjectGraph private constructor(val bindingsHashTable: BindingsHashTable, val bindings: List<Binding>) {
    inline fun <reified T : Any> get(tag: String? = null): T {
        return get(T::class.java, tag)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>, tag: String? = null): T {
        val className = clazz.name
        val binding = bindingsHashTable.get(className, tag) ?:
                throw BindingNotFoundException("No binding found for class '${clazz.name}'"
                        + if (tag == null) "" else " with tag '$tag'")
        return binding.get(this) as T
    }

    inline fun <reified T : Any> lazy(tag: String? = null): Lazy<T> {
        return lazy(T::class.java, tag)
    }

    fun <T : Any> lazy(clazz: Class<T>, tag: String? = null): Lazy<T> {
        return LazyGet(this, clazz, tag)
    }

    class Builder {
        private var extendsBindingIndex = 0
        private val bindings: MutableList<Binding> = ArrayList()

        infix fun extends(objectGraph: ObjectGraph) {
            bindings.addAll(extendsBindingIndex, objectGraph.bindings)
            extendsBindingIndex += objectGraph.bindings.size
        }

        fun <T> singleton(clazz: Class<T>, tag: String? = null, isOverride: Boolean = false,
                          provider: ObjectGraph.() -> T) {
            bindings += SingletonLazyBinding(clazz.name, tag, isOverride, provider)
        }

        inline fun <reified T : Any> singleton(tag: String? = null, isOverride: Boolean = false,
                                               noinline provider: ObjectGraph.() -> T) {
            singleton(T::class.java, tag, isOverride, provider)
        }

        fun <T : Any> singleton(instance: T, tag: String? = null, isOverride: Boolean = false,
                                bindType: Class<*> = instance.javaClass) {
            bindings += SingletonInstanceBinding(bindType.name, tag, isOverride, instance)
        }

        fun <T : Any> factory(clazz: Class<T>, tag: String? = null, isOverride: Boolean = false,
                              provider: ObjectGraph.() -> T) {
            bindings += FactoryBinding(clazz.name, tag, isOverride, provider)
        }

        inline fun <reified T : Any> factory(tag: String? = null, isOverride: Boolean = false,
                                             noinline provider: ObjectGraph.() -> T) {
            factory(T::class.java, tag, isOverride, provider)
        }

        fun build(): ObjectGraph {
            val bindingsHashTable = BindingsHashTable(bindings.size)

            var overrideBindings: MutableList<Binding>? = null

            for (binding in bindings) {
                if (binding.isOverride) {
                    if (overrideBindings == null) overrideBindings = ArrayList(4)
                    overrideBindings.add(binding)
                } else {
                    bindingsHashTable.put(binding)
                }
            }

            if (overrideBindings != null) {
                for (binding in overrideBindings) {
                    bindingsHashTable.put(binding, true)
                }
            }

            return ObjectGraph(bindingsHashTable, bindings)
        }
    }

    private class LazyGet<out T : Any>(
            objectGraph: ObjectGraph,
            clazz: Class<T>,
            private var tag: String?) : Lazy<T>, Serializable {

        private object UNINITIALIZED_VALUE

        private var objectGraph: ObjectGraph? = objectGraph
        private var clazz: Class<T>? = clazz
        private var _value: Any? = UNINITIALIZED_VALUE

        override val value: T
            get() {
                if (_value === UNINITIALIZED_VALUE) {
                    _value = objectGraph!!.get(clazz!!, tag)
                    objectGraph = null
                    clazz = null
                    tag = null
                }
                @Suppress("UNCHECKED_CAST")
                return _value as T
            }

        override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

        override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
    }
}

internal interface Binding {
    companion object {
        fun hashCode(className: String, tag: String?): Int {
            var result = className.hashCode()
            result = 31 * result + (tag?.hashCode() ?: 0)
            return result
        }
    }

    val className: String
    val tag: String?
    val isOverride: Boolean

    fun get(objectGraph: ObjectGraph): Any
}

internal class SingletonLazyBinding<T>(
        override val className: String,
        override val tag: String?,
        override val isOverride: Boolean,
        _provider: ObjectGraph.() -> T) : Binding {

    private var instance: T? = null
    private var provider: (ObjectGraph.() -> T)? = _provider
    private var isCreating = false

    override fun get(objectGraph: ObjectGraph): Any {
        if (instance == null) {
            synchronized(this) {
                val _provider = provider
                if (_provider != null) {
                    if (isCreating) {
                        throw CyclicDependencyException("A cyclic dependency was found for '$className'" +
                                "${if (tag == null) "" else " with tag '$tag'"}.\n" +
                                "Such a relationship is not recommended, but ObjectGraph.lazy() can be used as a " +
                                "work around if necessary.")
                    }
                    isCreating = true

                    instance = objectGraph._provider()
                    provider = null

                    isCreating = false
                }

            }
        }

        return instance!!
    }
}

internal class SingletonInstanceBinding<out T : Any>(
        override val className: String,
        override val tag: String?,
        override val isOverride: Boolean,
        val instance: T) : Binding {

    override fun get(objectGraph: ObjectGraph): Any {
        return instance
    }
}

class FactoryBinding<T : Any>(
        override val className: String,
        override val tag: String?,
        override val isOverride: Boolean,
        var provider: (ObjectGraph.() -> T)) : Binding {

    override fun get(objectGraph: ObjectGraph): Any {
        return objectGraph.provider()
    }
}

private fun Int.isPrime(): Boolean {
    if (this > 2 && this % 2 == 0) {
        return false
    }

    val top = Math.sqrt(this.toDouble()).toInt() + 1
    var i = 3
    while (i < top) {
        if (this % i == 0) {
            return false
        }
        i += 2
    }

    return true
}

internal class BindingsHashTable(size: Int) {
    val bindings: Array<Binding?>
    private val tableSize: Int

    init {
        // Table size must be at least 30% larger than the storage size and also a prime number
        var tableSize = (size + (size * .30) + 1).toInt()
        while (!tableSize.isPrime()) tableSize++
        this.tableSize = tableSize

        bindings = arrayOfNulls<Binding>(tableSize)
    }

    private fun lookup(className: String, tag: String?): Int {
        var hash = Binding.hashCode(className, tag)

        var index: Int
        do {
            hash = hash * 57 + 43
            index = Math.abs(hash % tableSize)
        } while (bindings[index] != null
                && (bindings[index]!!.className != className || bindings[index]!!.tag != tag))

        return index
    }

    fun get(className: String, tag: String?): Binding? {
        val index = lookup(className, tag)
        return bindings[index]
    }

    fun put(binding: Binding, overridePreviousBindings: Boolean = false) {
        val i = lookup(binding.className, binding.tag)
        if (bindings[i] == null || overridePreviousBindings) {
            bindings[i] = binding
        } else {
            throw IllegalStateException("Multiple bindings for class '${binding.className}'"
                    + if (binding.tag == null) "" else " with tag '${binding.tag}'")
        }
    }
}

class BindingNotFoundException(message: String) : RuntimeException(message)
class CyclicDependencyException(message: String) : RuntimeException(message)
