package converter

import java.util.*

enum class MeasurementType {
    LENGTH,
    WEIGHT,
    TEMPERATURE,
    NULL
}

enum class Measurement(
        val searches: Array<String>,
        val singular: String,
        val plural: String,
        private val measurementType: MeasurementType
) {
    // Lengths.
    METER(arrayOf("m", "meter", "meters"), "meter", "meters", MeasurementType.LENGTH),
    KILOMETER(arrayOf("km", "kilometer", "kilometers"), "kilometer", "kilometers", MeasurementType.LENGTH),
    CENTIMETER(arrayOf("cm", "centimeter", "centimeters"), "centimeter", "centimeters", MeasurementType.LENGTH),
    MILLIMETER(arrayOf("mm", "millimeter", "millimeters"), "millimeter", "millimeters", MeasurementType.LENGTH),
    MILE(arrayOf("mi", "mile", "miles"), "mile", "miles", MeasurementType.LENGTH),
    YARD(arrayOf("yd", "yard", "yards"), "yard", "yards", MeasurementType.LENGTH),
    FOOT(arrayOf("ft", "foot", "feet"), "foot", "feet", MeasurementType.LENGTH),
    INCH(arrayOf("in", "inch", "inches"), "inch", "inches", MeasurementType.LENGTH),

    // Weights.
    GRAM(arrayOf("g", "gram", "grams"), "gram", "grams", MeasurementType.WEIGHT),
    KILOGRAM(arrayOf("kg", "kilogram", "kilograms"), "kilogram", "kilograms", MeasurementType.WEIGHT),
    MILLIGRAM(arrayOf("mg", "milligram", "milligrams"), "milligram", "milligrams", MeasurementType.WEIGHT),
    OUNCE(arrayOf("oz", "ounce", "ounces"), "ounce", "ounces", MeasurementType.WEIGHT),
    POUND(arrayOf("lb", "pound", "pounds"), "pound", "pounds", MeasurementType.WEIGHT),

    // Temperatures.
    CELSIUS(arrayOf("c", "dc", "Celsius", "Celsius"), "degree Celsius", "degrees Celsius", MeasurementType.TEMPERATURE),
    FAHRENHEIT(arrayOf("f", "df", "Fahrenheit", "Fahrenheits"), "degree Fahrenheit", "degrees Fahrenheit", MeasurementType.TEMPERATURE),
    KELVIN(arrayOf("k", "kelvin", "kelvins"), "kelvin", "kelvins", MeasurementType.TEMPERATURE),

    NULL(arrayOf(), "???", "???", MeasurementType.NULL);

    companion object {
        fun getUnit(unit: String): Measurement {
            for (enum in values())
                if (enum.searches.map { it.toLowerCase() }.contains(unit.toLowerCase()))
                    return enum

            return NULL
        }

        fun convertLengths(value: Double, unitFrom: Measurement, unitTo: Measurement): Double {
            if (unitTo.measurementType != MeasurementType.LENGTH || unitFrom.measurementType != MeasurementType.LENGTH) {
                throw Exception("Conversion from ${unitFrom.plural} to ${unitTo.plural} is impossible")
            }

            if (value < 0) {
                throw Exception("Length shouldn't be negative")
            }

            val inMeters = when (unitFrom) {
                KILOMETER -> value * 1000
                CENTIMETER -> value * 0.01
                MILLIMETER -> value * 0.001
                MILE -> value * 1609.35
                YARD -> value * 0.9144
                FOOT -> value * 0.3048
                INCH -> value * 0.0254
                else -> value
            }

            return when (unitTo) {
                KILOMETER -> inMeters / 1000
                CENTIMETER -> inMeters / 0.01
                MILLIMETER -> inMeters / 0.001
                MILE -> inMeters / 1609.35
                YARD -> inMeters / 0.9144
                FOOT -> inMeters / 0.3048
                INCH -> inMeters / 0.0254
                else -> inMeters
            }
        }

        fun convertWeights(value: Double, unitFrom: Measurement, unitTo: Measurement): Double {
            if (unitTo.measurementType != MeasurementType.WEIGHT || unitFrom.measurementType != MeasurementType.WEIGHT) {
                throw Exception("Conversion from ${unitFrom.plural} to ${unitTo.plural} is impossible")
            }

            if (value < 0) {
                throw Exception("Weight shouldn't be negative")
            }

            val inGrams = when (unitFrom) {
                KILOGRAM -> value * 1000
                MILLIGRAM -> value * 0.001
                OUNCE -> value * 28.3495
                POUND -> value * 453.592
                else -> value
            }

            return when (unitTo) {
                KILOGRAM -> inGrams / 1000
                MILLIGRAM -> inGrams / 0.001
                OUNCE -> inGrams / 28.3495
                POUND -> inGrams / 453.592
                else -> inGrams
            }
        }

        fun convertTemperatures(value: Double, unitFrom: Measurement, unitTo: Measurement): Double {
            if (unitTo.measurementType != MeasurementType.TEMPERATURE || unitFrom.measurementType != MeasurementType.TEMPERATURE) {
                throw Exception("Conversion from ${unitFrom.plural} to ${unitTo.plural} is impossible")
            }

            val inFahrenheits = when (unitFrom) {
                CELSIUS -> value * 9 / 5 + 32
                KELVIN -> value * 9 / 5 - 459.67
                else -> value
            }

            return when (unitTo) {
                CELSIUS -> (inFahrenheits - 32) * 5 / 9
                KELVIN -> (inFahrenheits + 459.67) * 5 / 9
                else -> inFahrenheits
            }
        }
    }

    fun convertFromUnit(value: Double, unitFrom: Measurement): Double {
        return when (unitFrom.measurementType) {
            MeasurementType.LENGTH -> convertLengths(value, unitFrom, this)
            MeasurementType.WEIGHT -> convertWeights(value, unitFrom, this)
            MeasurementType.TEMPERATURE -> convertTemperatures(value, unitFrom, this)
            else -> 0.0
        }
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    while (true) {
        print("Enter what you want to convert (or exit): ")

        val input = scanner.nextLine()

        if (input == "exit") break

        val value: Double
        val unitFrom: Measurement
        val unitTo: Measurement

        try {
            val inputs = input.split("(?i)(degrees|degree|\\bin\\b|\\bto\\b|\\bconvertTo\\b| )+".toRegex())

            if (inputs.size != 3) {
                println("Parse error")
                continue
            }

            value = inputs[0].toDouble()
            unitFrom = Measurement.getUnit(inputs[1])
            unitTo = Measurement.getUnit(inputs[2])
        } catch (e: Exception) {
            println("Parse error")
            continue
        }

        try {
            if (unitTo == Measurement.NULL || unitFrom == Measurement.NULL) {
                throw Exception("Conversion from ${unitFrom.plural} to ${unitTo.plural} is impossible")
            }

            val resultantValue = unitTo.convertFromUnit(value, unitFrom)

            println("$value ${if ((Math.round(value * 10.0) / 10.0) == 1.0) unitFrom.singular else unitFrom.plural} is $resultantValue ${if (Math.round(resultantValue * 10.0) / 10.0 == 1.0) unitTo.singular else unitTo.plural}")
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
