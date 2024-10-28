object HeapSort extends App {
  def heapSort(elements: Array[Int]): Unit = {
    val length = elements.length

   
    for (i <- length / 2 - 1 to 0 by -1) {
      heapify(elements, length, i)
    }

    for (i <- length - 1 to 1 by -1) {
      val temp = elements(0)
      elements(0) = elements(i)
      elements(i) = temp
      heapify(elements, i, 0)
    }
  }

  def heapify(elements: Array[Int], n: Int, i: Int): Unit = {
    var largest = i        
    val left = 2 * i + 1    
    val right = 2 * i + 2   

    if (left < n && elements(left) > elements(largest)) {
      largest = left
    }

    if (right < n && elements(right) > elements(largest)) {
      largest = right
    }

    if (largest != i) {
      val swap = elements(i)
      elements(i) = elements(largest)
      elements(largest) = swap
      heapify(elements, n, largest)
    }
  }

  val elements = Array(6, 563, 92092, -393, 38383, 122, 9803, 32, 4, 56)
  heapSort(elements)
  println("Sorted array using Heap Sort: " + elements.mkString(", "))
}
