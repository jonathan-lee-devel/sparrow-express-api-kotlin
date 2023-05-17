package io.jonathanlee.sparrowexpressapikotlin.util


interface ListUtil {
    companion object {
        fun <E> removeDuplicatesFromList(list: List<E>): ArrayList<E> {
            val set: HashSet<E> = HashSet(list)
            return ArrayList(set)
        }
    }
}

