package lab1

import java.util.*

class MorganAlg(private var graph: Graph) {
    private var prevGraph: Graph = graph.clone()

    fun run() : Graph {
        do {
            updatePrevGraph()
            updateVertexWeights()
        }while (calculateGraphsUniqueWeights(graph) != calculateGraphsUniqueWeights(prevGraph))
        graph.sortGraph()
        graph.findCycle()
        return graph
    }

    private fun updateVertexWeights() {
        for (entry in prevGraph.adjVertices) {
            var newWeight = 0
            for (pair in entry.value) {
                newWeight += pair.first.weight
            }
            graph.vertices[entry.key.id].weight = newWeight
        }
    }

    private fun updatePrevGraph() {
        prevGraph = graph.clone()
    }

    companion object {
        fun calculateGraphsUniqueWeights(graph: Graph): Int {
            val unique = TreeSet<Int>()
            for (vertex in graph.vertices) {
                unique.add(vertex.weight)
            }
            return unique.size
        }
    }
}