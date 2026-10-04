package com.example.myfirstapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var tvFormula: TextView

    private var expression: String = "0"
    private var formulaText: String = ""
    private var isResultState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.a123)

        tvDisplay = findViewById(R.id.tvDisplay)
        tvFormula = findViewById(R.id.tvFormula)

        setupButtons()
    }

    private fun setupButtons() {
        // 數字鍵 (0-9)
        val numberButtons = mapOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9",
        )

        for ((id, value) in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                appendDigit(value)
            }
        }

        // 小數點
        findViewById<Button>(R.id.btnDot).setOnClickListener {
            appendDot()
        }

        // 運算符按鈕 (+, -, ×, ÷)
        val operatorButtons = mapOf(
            R.id.btnAdd to "+",
            R.id.btnSubtract to "-",
            R.id.btnMultiply to "×",
            R.id.btnDivide to "÷",
        )

        for ((id, op) in operatorButtons) {
            findViewById<Button>(id).setOnClickListener {
                appendOperator(op)
            }
        }

        // 等號按鈕 (=)
        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            calculateResult()
        }

        // 全部清除 (AC)
        findViewById<Button>(R.id.btnAC).setOnClickListener {
            clearAll()
        }

        // 退格 / 刪除按鈕 (C 與 Backspace 圖示均執行單字刪除)
        findViewById<Button>(R.id.btnC).setOnClickListener {
            deleteLastChar()
        }

        findViewById<ImageButton>(R.id.btnBackspace).setOnClickListener {
            deleteLastChar()
        }
    }

    /**
     * 輸入數字 (0-9)
     */
    private fun appendDigit(digit: String) {
        if (isResultState || expression == "Error") {
            expression = digit
            formulaText = ""
            isResultState = false
        } else if (expression == "0") {
            expression = digit
        } else {
            expression += digit
        }
        updateDisplay()
    }

    /**
     * 輸入小數點 (.)
     */
    private fun appendDot() {
        if (isResultState || expression == "Error") {
            expression = "0."
            formulaText = ""
            isResultState = false
        } else if (canAppendDot(expression)) {
            expression += if (expression.isEmpty() || isOperator(expression.last())) {
                "0."
            } else {
                "."
            }
        }
        updateDisplay()
    }

    /**
     * 輸入運算符 (+, -, ×, ÷)
     */
    private fun appendOperator(op: String) {
        if (expression == "Error") {
            clearAll()
            return
        }

        if (isResultState) {
            formulaText = ""
            isResultState = false
        }

        if (expression.isEmpty()) {
            expression = "0$op"
        } else if (isOperator(expression.last())) {
            // 替換最後一個運算符
            expression = expression.dropLast(1) + op
        } else if (expression.last() == '.') {
            expression = expression.dropLast(1) + op
        } else {
            expression += op
        }
        updateDisplay()
    }

    /**
     * 計算結果 (=)
     */
    private fun calculateResult() {
        if (expression == "Error" || isResultState) return

        var evalExpr = expression
        while (evalExpr.isNotEmpty() && (isOperator(evalExpr.last()) || (evalExpr.last() == '.'))) {
            evalExpr = evalExpr.dropLast(1)
        }

        if (evalExpr.isEmpty()) return

        val result = evaluateExpression(evalExpr)

        formulaText = "$evalExpr ="
        expression = if (result == null || result.isNaN() || result.isInfinite()) {
            "Error"
        } else {
            formatNumber(result)
        }
        isResultState = true
        updateDisplay()
    }

    /**
     * 逐字刪除 (退格 / 刪除最後一個字元)
     */
    private fun deleteLastChar() {
        if (isResultState || (expression == "Error")) {
            clearAll()
            return
        }

        expression = if (expression.length > 1) {
            expression.dropLast(1)
        } else {
            "0"
        }
        updateDisplay()
    }

    /**
     * 全部清除 (AC)
     */
    private fun clearAll() {
        expression = "0"
        formulaText = ""
        isResultState = false
        updateDisplay()
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '×' || c == '÷'
    }

    private fun canAppendDot(expr: String): Boolean {
        var i = expr.length - 1
        while (i >= 0) {
            val c = expr[i]
            if (isOperator(c)) break
            if (c == '.') return false
            i--
        }
        return true
    }

    /**
     * 解析並計算算式字串 (支援 +, -, ×, ÷，以及運算優先順序)
     */
    private fun evaluateExpression(expr: String): Double? {
        val sanitized = expr.replace("×", "*").replace("÷", "/")
        return try {
            parseAndEval(sanitized)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseAndEval(str: String): Double {
        val tokens = mutableListOf<String>()
        var i = 0
        val sb = StringBuilder()

        while (i < str.length) {
            val c = str[i]
            if ((c in '0'..'9') || (c == '.')) {
                sb.append(c)
            } else if (c in listOf('+', '-', '*', '/')) {
                if (sb.isNotEmpty()) {
                    tokens.add(sb.toString())
                    sb.clear()
                } else if ((c == '-') && (tokens.isEmpty() || (tokens.last() in listOf("+", "-", "*", "/")))) {
                    sb.append(c)
                    i++
                    continue
                }
                tokens.add(c.toString())
            }
            i++
        }
        if (sb.isNotEmpty()) {
            tokens.add(sb.toString())
        }

        if (tokens.isEmpty()) return 0.0

        // 第一階段：計算乘除 (*, /)
        val values = mutableListOf<Double>()
        val ops = mutableListOf<String>()

        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if ((token == "*") || (token == "/")) {
                val nextToken = tokens.getOrNull(idx + 1) ?: break
                if (values.isEmpty()) return Double.NaN
                val left = values.removeAt(values.size - 1)
                val right = nextToken.toDoubleOrNull() ?: return Double.NaN
                if ((token == "/") && (right == 0.0)) return Double.NaN
                val res = if (token == "*") left * right else left / right
                values.add(res)
                idx += 2
            } else if ((token == "+") || (token == "-")) {
                ops.add(token)
                idx++
            } else {
                val num = token.toDoubleOrNull() ?: return Double.NaN
                values.add(num)
                idx++
            }
        }

        // 第二階段：計算加減 (+, -)
        if (values.isEmpty()) return 0.0
        var result = values[0]
        for (j in ops.indices) {
            val op = ops[j]
            val nextVal = values.getOrNull(j + 1) ?: break
            if (op == "+") result += nextVal
            if (op == "-") result -= nextVal
        }

        return result
    }

    private fun formatNumber(num: Double): String {
        if (num.isNaN() || num.isInfinite()) return "Error"
        if (num % 1.0 == 0.0 && num >= Long.MIN_VALUE.toDouble() && num <= Long.MAX_VALUE.toDouble()) {
            return num.toLong().toString()
        }
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#.########", symbols)
        df.roundingMode = RoundingMode.HALF_UP
        return df.format(num)
    }

    private fun updateDisplay() {
        tvDisplay.text = expression
        tvFormula.text = formulaText
    }
}



