object BubbleSort extends App{
    def bubbleSort(elements:Array[Int]):Unit = {
        val length = elements.length
        for(i<-0 until length - 1){
            for(j<-0 until length-i-1){
                if (elements(j)>elements(j+1)){
                    val temp = elements(j)
                    elements(j) = elements(j+1)
                    elements(j+1) = temp
                }
            }
        }
    }
    val elements = Array(6,563,92092,-393,38383,122,9803,32,4,56)
    bubbleSort(elements)
    println("Sorted array using Bubble Sort: " + elements.mkString(", "))
}