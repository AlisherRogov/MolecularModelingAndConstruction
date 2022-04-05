package lab1

import java.io.File

class Graph {
    private var numberOfVertices: Int = 0
    private var numberOfEdges: Int = 0
    val vertices: ArrayList<Vertex> = ArrayList()
    val adjVertices: LinkedHashMap<Vertex, MutableList<Pair<Vertex, Int>>> = LinkedHashMap()
    val cycles = ArrayList<HashSet<Int>>()

    fun addVertex(vertex: Vertex) {
        numberOfVertices++
        adjVertices[vertex] = ArrayList()
        vertices.add(vertex)
    }

    private fun addEdge(vertex1: Vertex, vertex2: Vertex, bonds: Int) {
        numberOfEdges++
        if (adjVertices[vertex1]!!.filter { it.first.id == vertex2.id }.isEmpty()) {
            adjVertices[vertex1]!!.add(Pair(vertex2, bonds))
            adjVertices[vertex2]!!.add(Pair(vertex1, bonds))
        }
    }

    fun addEdge(id1: Int, id2: Int, bonds: Int) {
        val vertex1 = vertices[id1]
        val vertex2 = vertices[id2]
        addEdge(vertex1, vertex2, bonds)
    }

    fun getByIDAdjecents(id: Int) = adjVertices.entries.first { it.key.id == id }.value
    fun getByIdVertex(id: Int) = vertices.first { it.id == id }
    fun getConnection(id1: Int, id2: Int) = getByIDAdjecents(id1).first { it.first.id == id2 }.second

    fun clone(): Graph {
        val newGraph = Graph()
        for (vertex in vertices) {
            newGraph.addVertex(vertex.copy())
        }
        for (entry in adjVertices) {
            for (pair in entry.value) {
                newGraph.addEdge(entry.key.id, pair.first.id, pair.second)
            }
        }
        return newGraph
    }

    fun sortGraph() {
        vertices.sortBy { it.weight }
        adjVertices.entries.forEach { entry -> entry.value.sortBy { it.first.weight } }
    }

    fun findCycle(): ArrayList<HashSet<Int>> {
        val marked = Array<Boolean>(numberOfVertices, init = { numb -> !vertices.any { it.id == numb } })
        var count = 0
        for (n in 3..numberOfVertices) {
            for (i in 0..(numberOfVertices - n)) {
                count = DFS(marked.clone(), i, n - 1, i, count, hashSetOf(i))
            }
        }
        return cycles
    }

    fun DFS(marked: Array<Boolean>, vertId: Int, n: Int, startId: Int, count: Int, path: HashSet<Int>): Int {
        var count1 = count
        marked[vertId] = true
        if (n == 0) {
            marked[vertId] = false
            return if (getByIDAdjecents(vertId).any { it.first.id == startId } && !cycles.contains(path)) {
                cycles.add(path.toHashSet())
                count1 + 1
            } else {
                count1
            }
        }
        for (entry in getByIDAdjecents(vertId)) {
            if (!marked[entry.first.id]) {
                val newPath = path.clone() as HashSet<Int>
                newPath.add(entry.first.id)
                count1 = DFS(marked, entry.first.id, n - 1, startId, count, newPath)
            }
        }
        marked[vertId] = false
        return count1
    }

    fun graphviz() {
        val file = File("graph")
        val str = StringBuilder("graph{")
        for (entry in adjVertices) {
            for (edges in entry.value) {
                if (!str.contains("${edges.first.name}${edges.first.id} -- ${entry.key.name}${entry.key.id}")) {
                    str.append("\n\t${entry.key.name}${entry.key.id} -- ${edges.first.name}${edges.first.id}")
                    if (edges.second == 2) {
                        str.append(" [color=\"black:invis:black\"]")
                    }
                }
            }
        }
        str.append("\n}")
        file.writeText(str.toString())
    }
}

