package com.xegami.voicemod

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var operations: EditText
    private var resultPressed: Boolean =
        false // este switch nos permite saber si el usuario quiere empezar una nueva operacion o continuar con la anterior
    private val df: DecimalFormat =
        DecimalFormat("0.#") // evitamos decimales innecesarios (ej: 10.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        operations = findViewById(R.id.et_screen)
    }

    // nos ponemos a la escucha de cualquier boton presionado
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.btn_c -> et_screen.text.clear()
            R.id.btn_0 -> appendNumber("0")
            R.id.btn_1 -> appendNumber("1")
            R.id.btn_2 -> appendNumber("2")
            R.id.btn_3 -> appendNumber("3")
            R.id.btn_4 -> appendNumber("4")
            R.id.btn_5 -> appendNumber("5")
            R.id.btn_6 -> appendNumber("6")
            R.id.btn_7 -> appendNumber("7")
            R.id.btn_8 -> appendNumber("8")
            R.id.btn_9 -> appendNumber("9")
            R.id.btn_plus -> appendOperator("+")
            R.id.btn_minus -> appendOperator("-")
            R.id.btn_multiply -> appendOperator("×")
            R.id.btn_divide -> appendOperator("÷")
            R.id.btn_dot -> appendDot()
            R.id.btn_result -> {
                resultPressed = true
                operate()
            }
            R.id.btn_backspace -> et_screen.setText(et_screen.text.dropLast(1))
        }
    }

    private fun operate() {
        val numbers: List<String> = et_screen.text.split(Regex("([+])|([-])|([×])|([÷])"), 0)
        if (numbers[0] == "" || numbers[1] == "") return

        when {
            et_screen.text.contains("+") -> et_screen.setText(
                df.format(numbers[0].toDouble() + numbers[1].toDouble()).toString()
            )
            et_screen.text.contains("-") -> et_screen.setText(
                df.format(numbers[0].toDouble() - numbers[1].toDouble()).toString()
            )
            et_screen.text.contains("×") -> et_screen.setText(
                df.format(numbers[0].toDouble() * numbers[1].toDouble()).toString()
            )
            et_screen.text.contains("÷") -> et_screen.setText(
                df.format(numbers[0].toDouble() / numbers[1].toDouble()).toString()
            )
        }
    }

    private fun appendNumber(number: String) {
        // si el usuario quiso saber el resultado y no ha puesto ningun simbolo, reseteamos la pantalla
        if (resultPressed && et_screen.text.matches(Regex("^.*\\d$"))) et_screen.text.clear()
        resultPressed = false

        et_screen.text.append(number)
    }

    private fun appendOperator(symbol: String) {
        resultPressed = false

        /* en caso de querer hacer operaciones consecutivas, habria que remover esta linea y
        operar solo con el boton de Result (realizando los debidos cambios a la funcion operate()) */
        if (et_screen.text.contains(Regex("([+])|([-])|([×])|([÷])"))) operate()

        // evitamos que se puedan poner simbolos consecutivos y lo reemplazamos en el caso de que se intente
        if (et_screen.text.matches(Regex("^.*\\d$"))) {
            et_screen.text.append(symbol)
        } else {
            et_screen.setText(et_screen.text.replaceFirst(Regex(".$"), symbol))
        }
    }

    // nos aseguramos de que no se pueden poner puntos seguidos ni junto a simbolos
    private fun appendDot() {
        if (et_screen.text.matches(Regex("^.*\\d$"))) {
            if (et_screen.text.contains(".") && et_screen.text.contains(Regex("([+])|([-])|([×])|([÷])"))) {
                val rightSide: String = et_screen.text.split(Regex("([+])|([-])|([×])|([÷])"), 0)[1]
                if (!rightSide.contains(".")) et_screen.text.append(".")
            } else {
                et_screen.text.append(".")
            }
        }
    }
}