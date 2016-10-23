package kinject

import java.util.*

inline fun objectGraph(create: ObjectGraph.Builder.() -> Unit): ObjectGraph {
    val builder = ObjectGraph.Builder()
    builder.create()
    return builder.build()
}

class ObjectGraph private constructor(val bindingsHashTable: BindingsHashTable) {
    inline fun <reified T : Any> instance(tag: String? = null): T {
        return instance(T::class.java, tag)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> instance(clazz: Class<T>, tag: String? = null): T {
        val className = clazz.name
        val binding = bindingsHashTable.get(className, tag) ?:
                throw BindingNotFoundException("No binding found for class '${clazz.name}'"
                        + if (tag == null) "" else " with tag '$tag'")
        return binding.get() as T
    }

    class Builder {
        private val bindings: MutableList<Binding> = ArrayList()

        infix fun extends(objectGraph: ObjectGraph) {
            objectGraph.bindingsHashTable.bindings
                    .asSequence()
                    .filterNotNull()
                    .forEach { bindings += it }
        }

        fun <T> singleton(clazz: Class<T>, tag: String? = null, provider: () -> T) {
            bindings += SingletonBinding(clazz.name, tag, provider, null)
        }

        inline fun <reified T : Any> singleton(tag: String? = null, noinline provider: () -> T) {
            singleton(T::class.java, tag, provider)
        }

        fun <T : Any> singleton(instance: T, tag: String? = null, bindType: Class<*> = instance.javaClass) {
            bindings += SingletonBinding(bindType.name, tag, null, instance)
        }

        fun build(): ObjectGraph {
            return ObjectGraph(BindingsHashTable(bindings))
        }
    }
}

internal interface Binding {
    val className: String
    val tag: String?

    fun get(): Any

    companion object {
        fun hashCode(className: String, tag: String?): Int {
            var result = className.hashCode()
            result = 31 * result + (tag?.hashCode() ?: 0)
            return result
        }
    }
}

/**
 * provider or instance should be given, not both.
 */
internal class SingletonBinding<T>(
        override val className: String,
        override val tag: String?,
        var provider: (() -> T)?,
        var instance: T?) : Binding {

    override fun get(): Any {
        if (instance == null) {
            synchronized(this) {
                if (provider != null) {
                    instance = provider!!()
                    provider = null
                }
            }
        }

        return instance!!
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

internal class BindingsHashTable(_bindings: List<Binding>) {
    val bindings: Array<Binding?>
    private val tableSize: Int

    init {
        val size = _bindings.size
        // Table size must be at least 30% larger than the storage size and also a prime number
        var tableSize = (size + (size * .30) + 1).toInt()
        while (!tableSize.isPrime()) tableSize++
        this.tableSize = tableSize

        bindings = arrayOfNulls<Binding>(tableSize)

        for (binding in _bindings) {
            put(binding)
        }
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

    fun put(binding: Binding) {
        val i = lookup(binding.className, binding.tag)
        bindings[i] = binding
    }
}

class BindingNotFoundException(message: String) : RuntimeException(message)
