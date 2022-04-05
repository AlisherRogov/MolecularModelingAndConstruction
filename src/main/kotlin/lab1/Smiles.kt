package lab1

import utils.ParseMolFile
import java.lang.StringBuilder

fun main() {
//    val molGraph = ParseMolFile("ChEBI_15365.mol").toGraph()
//    val molGraph = ParseMolFile("ChEBI_17578.mol").toGraph()
//    val molGraph = ParseMolFile("ChEBI_32879.mol").toGraph()
    val molGraph = ParseMolFile("ChEBI_60099.mol").toGraph()
    val isomorphicMolGraph = MorganAlg(molGraph).run()
    isomorphicMolGraph.graphviz()
    val min = isomorphicMolGraph.vertices.first().weight
    val possibilities = isomorphicMolGraph.vertices.filter { it.weight == min }
    for (vertex in possibilities) {
        println(buildSMILESFrom(vertex, isomorphicMolGraph))
    }
    println()
}

fun buildSMILESFrom(vertexToStart: Vertex?, graph: Graph): String {
    var smiles = StringBuilder()
    var tmpVertex = vertexToStart
    smiles.append(tmpVertex!!.name)
    val processedVerticesId = ArrayList<Int>()
    var cycleStarted = false

    do {
        processedVerticesId.add(tmpVertex!!.id)

        val vertexWithConnectionSize = graph.getByIDAdjecents(tmpVertex.id)
            .firstOrNull { !processedVerticesId.contains(it.first.id) }

        tmpVertex = vertexWithConnectionSize?.first

        if (tmpVertex == null) {
            if (cycleStarted) {
                smiles.append("${graph.getByIdVertex(processedVerticesId.last()).name}1")
                cycleStarted = false
            } else {
                tmpVertex = graph.getByIdVertex(processedVerticesId[processedVerticesId.size - 2])
                val connection = if (graph.getConnection(tmpVertex.id, processedVerticesId.last()) > 1) {
                    "="
                } else {
                    ""
                }

                smiles = StringBuilder(smiles.removeRange(smiles.lastIndex, smiles.lastIndex + 1))
                if (smiles.last() == '=') {
                    smiles = StringBuilder(smiles.removeRange(smiles.lastIndex, smiles.lastIndex + 1))
                }

                smiles.append("(${connection}${graph.getByIdVertex(processedVerticesId.last()).name})")

                if (processedVerticesId.size == graph.vertices.size) {
                    smiles.append(connection).append(graph.getByIdVertex(processedVerticesId.last()).name)
                }
            }
        } else {
            val connection = if (graph.getConnection(processedVerticesId.last(), tmpVertex.id) > 1) {
                "="
            } else {
                ""
            }
            if (graph.cycles.any { it.contains(tmpVertex.id) } && !cycleStarted) {
                smiles.append("${tmpVertex.name}1")
                cycleStarted = true
            } else {
                if (cycleStarted && !graph.cycles.any { it.contains(tmpVertex.id) }) {
                    smiles.append("${tmpVertex.name}1")
                    cycleStarted = false
                } else {
                    smiles.append(connection).append(tmpVertex.name)
                }
            }
        }
    } while (processedVerticesId.size != graph.vertices.size)
    return smiles.toString()
}