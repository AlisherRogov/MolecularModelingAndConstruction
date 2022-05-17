package lab3

import com.opencsv.CSVReader
import java.io.FileReader
import kotlin.math.exp
import kotlin.math.pow

fun sigmoid(d: Double) = 1 / (exp(-d) + 1)


fun main() {
    val data = trainDataCSV()
    val w0 = ArrayList<Double>()
    val b0 = 0.0
    for (i in data.first().second.indices) {
        w0.add(0.0)
    }

    val weights = gradientDescend(w0, b0, data.subList(0, 2 * data.size / 3))
    var tp = 0
    var fp = 0
    for (i in 2 * data.size / 3 until data.size) {
        var yhat = sigmoid(
            scalarMulArrays(
                data[i].second,
                weights.subList(0, weights.size).toList() as ArrayList<Double>
            ) + weights.last()
        )

//        println(yhat)
        if (yhat > 0.5) {
            yhat = 1.0
//            println("1.0 with ${data[i].first}")
        } else {
            yhat = 0.0
//            println("0.0 with ${data[i].first}")
        }
        if (yhat == data[i].first) {
            tp++
        }
        fp++
    }
    println("Test")
    println(tp.toDouble() / fp.toDouble())

    for (i in 0 until 2 * data.size / 3) {
        var yhat = sigmoid(
            scalarMulArrays(
                data[i].second,
                weights.subList(0, weights.size).toList() as ArrayList<Double>
            ) + weights.last()
        )

//        println(yhat)
        if (yhat > 0.5) {
            yhat = 1.0
//            println("1.0 with ${data[i].first}")
        } else {
            yhat = 0.0
//            println("0.0 with ${data[i].first}")
        }
        if (yhat == data[i].first) {
            tp++
        }
        fp++
    }
    println("Train")
    println(tp.toDouble() / fp.toDouble())
}

fun trainDataCSV(): ArrayList<Pair<Double, ArrayList<Double>>> {
    val train = ArrayList<Pair<Double, ArrayList<Double>>>()
    var records: MutableList<List<String?>> = ArrayList()
    CSVReader(FileReader("tox.csv")).use { csvReader ->
        var values: Array<String?>? = null
        while (csvReader.readNext().also { values = it } != null) {
            records.add(values!!.toList())
        }
    }
    val ids = records.removeFirst()
    records = records.filter { it[it.lastIndex]!!.isNotEmpty() }.toMutableList()
    val zeroes = records.filter { it[it.lastIndex]!!.toDouble() == 0.0 }.toMutableList()
    val ones = records.filter { it[it.lastIndex]!!.toDouble() == 1.0 }.subList(0, zeroes.size).toMutableList()
    zeroes.addAll(ones)
    zeroes.shuffle()
    records = zeroes
    val features = records.map { it.subList(1, ids.lastIndex).map { str -> str!!.toDouble() } }
    val y_train = records.map { it[it.lastIndex]!!.toDouble() }

    for (i in features.indices) {
        train.add(Pair(y_train[i], features[i] as ArrayList))
    }

    return train
}

fun gradient(error: Double, features: ArrayList<Double>): ArrayList<Double> {
    val list = features.map { sigmoid(error.pow(2)) * (1 - sigmoid(error.pow(2))) * (-2 * it * error) }.toMutableList()
    list.add(sigmoid(error.pow(2)) * (1 - sigmoid(error.pow(2))) * (-2 * error))
    return list as ArrayList<Double>
}

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

fun scalarMulArrays(b: ArrayList<Double>, g: ArrayList<Double>): Double {
    var res = 0.0
    for (i in b.indices) {
        res += b[i] * g[i]
    }
    return res
}

fun gradientDescend(
    w0: ArrayList<Double>,
    b0: Double,
    train: List<Pair<Double, ArrayList<Double>>>,
): ArrayList<Double> {
    var k = 0
    var wk = ArrayList(w0)
    var bk = b0
    var gradientValue: ArrayList<Double>
    var error: Double
    val errors = ArrayList<Double>()

    while (k <= 1000) {
        errors.clear()
        for (x in train) {
            error = (x.first - sigmoid(scalarMulArrays(wk, x.second) + bk))
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