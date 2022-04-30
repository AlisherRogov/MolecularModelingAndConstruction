package lab2

import com.opencsv.CSVReader
import java.io.FileReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.streams.toList

fun main() {
    val data = trainDataCSV()
    val w0 = ArrayList<Double>()
    val b0 = 0.0
    for (i in data.first().second.indices) {
        w0.add(0.0)
    }

    val weights = gradientDescend(w0, b0, data.subList(0, 2 * data.size / 3))
    for (i in 2 * data.size / 3 until data.size) {
        val yhat = scalarMulArrays(data[i].second, weights.subList(0, weights.size).toList() as ArrayList<Double>) + weights.last()
        println(data[i].first - yhat)
    }
}

fun trainDataPolynomial(): ArrayList<Pair<Double, ArrayList<Double>>> {
    val train = ArrayList<Pair<Double, ArrayList<Double>>>()
    val rand = Random()
    val doubles = rand.ints(10).toList()
        .map { if (it < 0) -1 * it.toDouble() % 10 else 1 * it.toDouble() % 10 }

    val res = doubles.map { 2 * it + 1 }
    for (i in res.indices) {
        val arr = ArrayList<Double>()
        arr.add(doubles[i])
        train.add(Pair(res[i], arr))
    }
    return train
}

fun trainDataCSV(): ArrayList<Pair<Double, ArrayList<Double>>> {
    val train = ArrayList<Pair<Double, ArrayList<Double>>>()
    val records: MutableList<List<String?>> = ArrayList()
    CSVReader(FileReader("solubility_dataset.csv")).use { csvReader ->
        var values: Array<String?>? = null
        while (csvReader.readNext().also { values = it } != null) {
            records.add(values!!.toList())
        }
    }
    val ids = records.removeFirst()
    val solubility = ids.indexOf("Solubility")
    val featureFirstIndex = ids.indexOf("MolWt")
    val features = records.map { it.subList(featureFirstIndex, ids.lastIndex + 1).map { str -> str!!.toDouble() } }
    val normalizeF = normalize(features)
    val y_train = records.map { it[solubility]!!.toDouble() }

    for (i in features.indices) {
        train.add(Pair(y_train[i], normalizeF[i]))
    }

    return train
}

fun normalize(features: List<List<Double>>): ArrayList<ArrayList<Double>> {
    val sums = ArrayList<Double>()
    for (i in features.first().indices) {
        sums.add(0.0)
    }
    for (elem in features) {
        for (i in elem.indices) {
            sums[i] += elem[i]
        }
    }
    val means = sums.map { it / features.size }

    val sigma = ArrayList<Double>()
    for (i in features.first().indices) {
        sigma.add(0.0)
    }
    for (elem in features) {
        for (i in elem.indices) {
            sigma[i] += (elem[i] - means[i]).pow(2)
        }
    }
    val sigma1 = sigma.map { sqrt(it / features.size) }

    val newFeatures = ArrayList<ArrayList<Double>>()
    for (f in features) {
        val arr = ArrayList<Double>()
        for (i in f.indices) {
            arr.add((f[i] - means[i]) / sigma1[i])
        }
        newFeatures.add(arr)
    }
    return newFeatures
}

fun gradient(error: Double, features: ArrayList<Double>): ArrayList<Double> {
    val list = features.map { -2 * it * error }.toMutableList()
    list.add(-2 * error)
    return list as ArrayList<Double>
}

fun numbToVecMul(numb: Double, arr: ArrayList<Double>) = arr.map { it * numb }

fun subArrays(mid: ArrayList<Double>, w: ArrayList<Double>): ArrayList<Double> {
    val res = ArrayList<Double>()
    for (i in mid.indices) {
        res.add(mid[i] - w[i])
    }
    return res
}

fun plusArrays(b: ArrayList<Double>, g: ArrayList<Double>): ArrayList<Double> {
    val res = ArrayList<Double>()
    for (i in b.indices) {
        res.add(b[i] + g[i])
    }
    return res
}

fun mulArrays(b: ArrayList<Double>, g: ArrayList<Double>): ArrayList<Double> {
    val res = ArrayList<Double>()
    for (i in b.indices) {
        res.add(b[i] * g[i])
    }
    return res
}

fun scalarMulArrays(b: ArrayList<Double>, g: ArrayList<Double>): Double {
    var res = 0.0
    for (i in b.indices) {
        res += b[i] * g[i]
    }
    return res
}

fun vectorEuclidNorm(vector: ArrayList<Double>): Double {
    var double = 0.0
    for (v in vector) {
        double += abs(v) * abs(v)
    }
    return sqrt(double)
}

fun gradientDescend(
    w0: ArrayList<Double>,
    b0: Double,
    train: List<Pair<Double, ArrayList<Double>>>,
): ArrayList<Double> {
    println("Gradient Descend")
    var k = 0
    var wk = ArrayList(w0)
    var bk = b0
    var gradientValue: ArrayList<Double>
    var error: Double
    val errors = ArrayList<Double>()

    while (k <= 100) {
        for (x in train) {
            error = (x.first - (scalarMulArrays(wk, x.second) + bk))
            errors.add(error)
            gradientValue = gradient(error, x.second)
            val wkNew = subArrays(wk, gradientValue.subList(0, x.second.size).map { it * 0.001 } as ArrayList<Double>)
            val bkNew = bk - gradientValue.last() * 0.001
            wk = wkNew
            bk = bkNew

        }
//        println(errors.sum() / train.size)
        k++
    }
    wk.add(bk)
    return wk
}