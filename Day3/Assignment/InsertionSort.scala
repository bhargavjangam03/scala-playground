object InsertionSort extends App{
    def insertionSort(elements:Array[Int]):Unit = {
        val length = elements.length
        for(i<-1 to length - 1){
            var key = elements(i)
            var j = i-1 
            while (j>=0 && elements(j)>key){
                elements(j+1) = elements(j)
                j-=1
            }
            elements(j+1)=key
        }
    }
    val elements = Array(6,563,92092,-393,38383,122,9803,32,4,56)
    insertionSort(elements)
    println("Sorted array using Insertion sort: " + elements.mkString(", "))
}
