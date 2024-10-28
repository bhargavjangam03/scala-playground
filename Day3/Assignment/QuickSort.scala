object QuickSort extends App{
    def quickSort(elements:Array[Int], left:Int,right: Int):Unit = {
        if (left<right){
            var pivot_index=partition(elements:Array[Int], left:Int,right: Int)
            println(pivot_index)
            quickSort(elements,left,pivot_index-1)
            quickSort(elements,pivot_index + 1,right)
        }
    }

    def partition(elements:Array[Int], left:Int,right: Int):Int = {

        var pivot = elements(right)
        var i = left - 1
        for (j<-left until right){
            if(elements(j)<pivot){
                i+=1
                var temp = elements(i)
                elements(i) = elements(j)
                elements(j) = temp
            }
        }
        i+=1
        var temp = elements(i)
        elements(i) = pivot
        elements(right) = temp

        return i


    }
    val elements = Array(6,563,92092,-393,38383,122,9803,32,4,56)
    val length = elements.length
    quickSort(elements,0,length-1)
    println("Sorted array using Quick sort: " + elements.mkString(", "))
}
