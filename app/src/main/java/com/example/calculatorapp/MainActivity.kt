package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.mariuszgromada.math.mxparser.*
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    private var etInput: EditText? = null
    private var isNumeric = false
    private var isDot = false
    private var hasDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etInput = findViewById(R.id.etInput)
        etInput?.showSoftInputOnFocus = false

    }

    fun onResult(view: View) {
        val input = etInput?.text.toString()
        // convert string to right format
        input.replace("×", "*")
        input.replace("÷", "/")

        // calculate expression via library
        val expression = Expression(input)
        val result: Double = expression.calculate()

        if ((result % 1.0) == 0.0){
            etInput?.setText(result.roundToLong().toString())
            etInput?.setSelection(result.roundToLong().toString().length)
        } else {
            etInput?.setText(result.toString())
            etInput?.setSelection(result.toString().length)
        }
        isDot = false
        isNumeric = true
    }

    fun onBrackets(view: View) {
        val cursorPos = etInput?.selectionStart
        val textLen = etInput?.text?.length
        val input = etInput?.text.toString()

        // count open and close brackets
        var open = 0
        var close = 0
        for (i in 0 until cursorPos!!) {
            if (input.substring(i,i+1) == "(") open++
            if (input.substring(i,i+1) == ")") close++
        }

        // update string by close or open brackets
        if (open == close || textLen?.let { input.substring(textLen-1, it) } == "("){
            updateString("(")
        }
        else if (close < open && textLen?.let { input.substring(textLen-1, it) } != "("){
            updateString(")")
        }
        // update cursor position
        etInput?.setSelection(cursorPos+1)
        isDot = false
        isNumeric = false
    }

    fun onClearAll(view: View) {
        etInput?.setText("")
        isDot = false
        isNumeric = false
        hasDot = false
    }

    fun onDotPress(view: View) {
        if (isNumeric && !isDot && !hasDot)
            updateString((view as Button).text as String)
        isDot = true
        isNumeric = false
        hasDot = true
    }

    fun onDigit (view: View) {
        updateString((view as Button).text as String)
        isDot = false
        isNumeric = true
    }

    fun onOperator(view: View) {
        updateString((view as Button).text as String)
        isDot = false
        isNumeric = false
        hasDot = false
    }

    fun onBackSpace(view: View) {
        // get cursor position
        val cursorPos = etInput?.selectionStart
        if (cursorPos != 0 && etInput?.text.toString().isNotEmpty()) {
            val selection: SpannableStringBuilder = etInput?.text as SpannableStringBuilder
            // replace current character with empty -> delete
            cursorPos?.minus(1)?.let { selection.replace(it, cursorPos, "") }
            etInput?.text = selection
            // update cursor position
            cursorPos?.minus(1)?.let { etInput?.setSelection(it) }
        }
    }

    private fun updateString(str: String) {
        // get cursor position
        val cursorPosition = etInput?.selectionStart
        // split the string into 2 pieces
        val oldStr = etInput?.text.toString()
        val leftStr = cursorPosition?.let { oldStr.substring(0, it) }
        val rightStr = cursorPosition?.let { oldStr.substring(it) }
        // merge them all and set text
        val mergedStr = "$leftStr$str$rightStr"
        etInput?.setText(mergedStr)
        // update cursor position
        cursorPosition?.plus(1)?.let { etInput?.setSelection(it) }
    }

}