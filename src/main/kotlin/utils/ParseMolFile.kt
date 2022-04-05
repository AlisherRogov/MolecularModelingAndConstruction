package utils

import lab1.Graph
import lab1.Vertex
import java.io.FileReader

class ParseMolFile(val path: String) {
    var numberOfMolls : Int = 0
    var numberOfBonds : Int = 0
    var data: List<String> = readFileIntoList()

    private fun readFileIntoList(): List<String> {
        var lines = FileReader(path).readLines() as MutableList
        lines = lines.subList(3, lines.size)
        parseMainInfo(lines.removeAt(0))
        return lines
    }

    private fun parseMainInfo(info: String) {
        val data = info.split("\\s+".toRegex()) as MutableList
        data.removeAt(0)
        numberOfMolls = data[0].toInt()
        numberOfBonds = data[1].toInt()
    }

    fun toGraph() : Graph {
        val molGraph = Graph()
        for (i in 0 until numberOfMolls) {
            var vertexData = data[i].split("\\s+".toRegex()).filter { it != "" }
            molGraph.addVertex(Vertex(i,1, vertexData[3]))
        }
        for (i in 0 until numberOfBonds) {
            var edgeData = data[i+numberOfMolls].split("\\s+".toRegex()).filter { it != "" }
            molGraph.addEdge(edgeData[0].toInt()-1, edgeData[1].toInt()-1, edgeData[2].toInt())
        }
        return molGraph
    }
}