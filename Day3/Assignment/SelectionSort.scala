object SelectionSort extends App {
  def selectionSort(elements: Array[Int]): Unit = {
    val length = elements.length
    for (i <- 0 until length - 1) {
      var minIndex = i
      for (j <- i + 1 until length) {
        if (elements(j) < elements(minIndex)) {
          minIndex = j
        }
      }
      if (minIndex != i) {
        val temp = elements(i)
        elements(i) = elements(minIndex)
        elements(minIndex) = temp
      }
    }
  }

  val elements = Array(6, 563, 92092, -393, 38383, 122, 9803, 32, 4, 56)
  selectionSort(elements)
  println("Sorted array using Selection Sort: " + elements.mkString(", "))
}
