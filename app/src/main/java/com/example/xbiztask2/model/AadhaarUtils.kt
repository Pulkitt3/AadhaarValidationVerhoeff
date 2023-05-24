package com.example.xbiztask2.model

import android.text.TextUtils
import android.widget.TextView
import java.util.regex.Pattern

class AadhaarUtils {
    companion object {
        var _multiplicationTable = arrayOf(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            intArrayOf(1, 2, 3, 4, 0, 6, 7, 8, 9, 5),
            intArrayOf(2, 3, 4, 0, 1, 7, 8, 9, 5, 6),
            intArrayOf(3, 4, 0, 1, 2, 8, 9, 5, 6, 7),
            intArrayOf(4, 0, 1, 2, 3, 9, 5, 6, 7, 8),
            intArrayOf(5, 9, 8, 7, 6, 0, 4, 3, 2, 1),
            intArrayOf(6, 5, 9, 8, 7, 1, 0, 4, 3, 2),
            intArrayOf(7, 6, 5, 9, 8, 2, 1, 0, 4, 3),
            intArrayOf(8, 7, 6, 5, 9, 3, 2, 1, 0, 4),
            intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1, 0))

        var _permutationTable = arrayOf(intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            intArrayOf(1, 5, 7, 6, 2, 8, 3, 0, 9, 4),
            intArrayOf(5, 8, 0, 3, 7, 9, 6, 1, 4, 2),
            intArrayOf(8, 9, 1, 6, 0, 4, 3, 5, 2, 7),
            intArrayOf(9, 4, 5, 3, 1, 2, 6, 8, 7, 0),
            intArrayOf(4, 2, 8, 6, 5, 7, 3, 9, 0, 1),
            intArrayOf(2, 7, 9, 3, 8, 0, 6, 4, 1, 5),
            intArrayOf(7, 0, 4, 6, 9, 1, 3, 2, 5, 8))

        var _inverseTable = intArrayOf(0, 4, 3, 2, 1, 5, 6, 7, 8, 9)


        fun generateVerhoeff(num: String?): String {
            var count = 0
            val strRevArray = stringToReversedIntArray(num!!)
            for (i in strRevArray.indices) {
                count = _multiplicationTable[count][_permutationTable[(i + 1) % 8][strRevArray[i]]]
            }
            return _inverseTable[count].toString()
        }

        fun validateVerhoeff(num: String?): Boolean {
            var count = 0
            val strRevArray = stringToReversedIntArray(num!!)
            for (i in strRevArray.indices) {
                count = _multiplicationTable[count][_permutationTable[i % 8][strRevArray[i]]]
            }
            return count == 0
        }


        private fun stringToReversedIntArray(num: String): IntArray {
            var intArray = IntArray(num.length)
            for (i in 0 until num.length) {
                intArray[i] = num.substring(i, i + 1).toInt()
            }
            intArray = reverse(intArray)
            return intArray
        }

        private fun reverse(myArray: IntArray): IntArray {
            val reversed = IntArray(myArray.size)
            for (i in myArray.indices) {
                reversed[i] = myArray[myArray.size - (i + 1)]
            }
            return reversed
        }

        fun findAadhaar(inputText: String, textView: TextView): List<String> {
            val aadhaarNumbers: MutableList<String> = ArrayList()
            // Define the regular expression pattern for a 12-digit Aadhaar number
            val trimmedText = inputText.trim() // Trim leading and trailing whitespace
            val sanitizedText = trimmedText.replace("[\n\r\t]".toRegex(),
                "") // Remove line breaks, carriage returns, and tabs

            val regexPattern = "\\b[0-9OoBbZzIi]{4}[- ]?[0-9OoBbZzIi]{4}[- ]?[0-9OoBbZzIi]{4}\\b"

            // Create a Pattern object with the regular expression
            val pattern = Pattern.compile(regexPattern)

            // Create a Matcher object to search for matches
            val matcher = pattern.matcher(sanitizedText)

            // Find all matches and validate each Aadhaar number using the Verhoeff algorithm
            while (matcher.find()) {
                var matchedNumber = matcher.group().replace("[Oo]".toRegex(), "0")
                    .replace("[Bb]".toRegex(), "8")
                    .replace("[Zz]".toRegex(), "2")
                    .replace("[Ii]".toRegex(), "1")
                    .replace("[- ]".toRegex(), "") // Remove visual characters
                if (validateVerhoeff(matchedNumber)) {
                    aadhaarNumbers.add(matchedNumber)
                }
            }
            displayResult(aadhaarNumbers, textView)
            return aadhaarNumbers
        }

        private fun displayResult(
            aadhaarNumbers: kotlin.collections.List<String?>,
            textView: TextView,
        ) {
            if (aadhaarNumbers.isEmpty()) {
                textView.text = "No Aadhaar numbers found"
            } else {
                val result = TextUtils.join("\n", aadhaarNumbers)
                textView.text = result
            }
        }


    }


}