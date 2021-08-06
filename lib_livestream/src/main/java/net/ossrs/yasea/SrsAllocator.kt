package net.ossrs.yasea

class SrsAllocator(var individualAllocationSize: Int, initialAllocationCount: Int = 0) {
    @Volatile
    private var availableSentinel = initialAllocationCount + 10
    private var availableAllocations = arrayOfNulls<Allocation?>(availableSentinel)

    init {
        for (i in 0 until availableSentinel) {
            availableAllocations[i] = Allocation(individualAllocationSize)
        }
    }

    @Synchronized
    fun allocate(size: Int): Allocation? {
        for (i in 0 until availableSentinel) {
            if (availableAllocations[i]!!.size() >= size) {
                val ret = availableAllocations[i]
                availableAllocations[i] = null
                return ret
            }
        }
        return Allocation(if (size > individualAllocationSize) size else individualAllocationSize)
    }

    @Synchronized
    fun release(allocation: Allocation) {
        allocation.clear()
        for (i in 0 until availableSentinel) {
            if (availableAllocations[i]!!.size() == 0) {
                availableAllocations[i] = allocation
                return
            }
        }
        if (availableSentinel + 1 > availableAllocations.size) {
            availableAllocations = availableAllocations.copyOf(availableAllocations.size * 2)
        }
        availableAllocations[availableSentinel++] = allocation
    }

    class Allocation(var size: Int = 0) {
        private var data = ByteArray(size)

        fun size(): Int {
            return size
        }

        fun appendOffset(offset: Int) {
            size += offset
        }

        fun clear() {
            size = 0
        }

        fun put(b: Byte) {
            data[size++] = b
        }

        fun put(b: Byte, pos: Int) {
            var index = pos
            data[index++] = b
            size = if (pos > size) pos else size
        }

        fun put(s: Short) {
            put(s.toByte())
            put(((s.toInt()) ushr 8).toByte())
        }

        fun put(i: Int) {
            put(i.toByte())
            put((i ushr 8).toByte())
            put((i ushr 16).toByte())
            put((i ushr 24).toByte())
        }

        fun put(bs: ByteArray) {
            System.arraycopy(bs, 0, data, size, bs.size)
            size += bs.size
        }
    }


}