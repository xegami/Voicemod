package com.xegami.voicemod

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import java.util.*
import java.util.function.Predicate
import kotlin.collections.ArrayList

/**
 * @author Enmanuel (Xegami) Dominguez
 * Calculadora capaz de realizar cualquier operacion basica (y con decimales)
 * He tratado de hacer el codigo en la menor cantidad de lineas posible, aunque seguro tiene abanico para mejoras
 * En cuanto a mantenimiento y escalabilidad: he modularizado cada accion de la calculadora en diferentes funciones
 * haciendo que solo tengan que llamarse desde el listener funcionando este como un controlador
 * Ademas, para separar las operaciones matematicas de la logica, he creado una clase estatica (Operations.kt)
 * dentro del mismo paquete.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var operations: EditText

    // switch que permite saber si el usuario quiere empezar una nueva operacion o continuar con la anterior
    private var resultPressed = false

    // evitamos decimales innecesarios (ej: 10.0)
    private val df = DecimalFormat("0.#")
    private val operatorsRegex = Regex("([+])|([-])|([×])|([÷])")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        operations = findViewById(R.id.et_screen)
    }

    /**
     * listener a la escucha de cualquier boton presionado
     */
    override fun onClick(p0: View?) {
        when (p0?.id) {
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
            R.id.btn_add -> appendOperator("+")
            R.id.btn_subtract -> appendOperator("-")
            R.id.btn_multiply -> appendOperator("×")
            R.id.btn_divide -> appendOperator("÷")
            R.id.btn_dot -> appendDot()
            R.id.btn_result -> {
                resultPressed = true
                operate()
            }
            R.id.btn_backspace -> backspace()
            R.id.btn_parenthesis -> snack("Not implemented yet.")
            R.id.btn_percent -> snack("Not implemented yet.")
        }
    }

    /**
     * funcion basica donde se efectuan los calculos
     */
    private fun operate() {
        var isNegative = false
        if (!et_screen.text.contains(operatorsRegex)
            || et_screen.text.endsWith(".")
        ) return

        if (et_screen.text.startsWith("-")) isNegative = true

        val numbers = ArrayList(et_screen.text.split(operatorsRegex, 0))
        numbers.removeAll(listOf(null, ""))

        if (numbers.size < 2) return
        if (isNegative) numbers[0] = "-" + numbers[0]

        when {
            et_screen.text.contains("+") -> et_screen.setText(
                df.format(Operations.add(numbers[0].toDouble(), numbers[1].toDouble()))
            )
            et_screen.text.contains("-") -> et_screen.setText(
                df.format(Operations.subtract(numbers[0].toDouble(), numbers[1].toDouble()))
            )
            et_screen.text.contains("×") -> et_screen.setText(
                df.format(Operations.multiply(numbers[0].toDouble(), numbers[1].toDouble()))
            )
            et_screen.text.contains("÷") -> et_screen.setText(
                df.format(Operations.divide(numbers[0].toDouble(), numbers[1].toDouble()))
            )
        }
    }

    /**
     * agrega un numero a la operacion
     */
    private fun appendNumber(number: String) {
        // si el usuario quiso saber el resultado y no ha puesto ningun simbolo, reseteamos la pantalla
        if (resultPressed && et_screen.text.matches(Regex("^.*\\d$"))) et_screen.text.clear()
        resultPressed = false

        et_screen.text.append(number)
    }

    /**
     * agrega un operador a la operacion
     */
    private fun appendOperator(symbol: String) {
        resultPressed = false

        /* en caso de querer hacer operaciones consecutivas, habria que remover esta linea y
        operar solo con el boton de Result (realizando los debidos cambios a la funcion operate()) */
        if (et_screen.text.contains(operatorsRegex)) operate()

        // evitamos que se puedan poner simbolos consecutivos y lo reemplazamos en el caso de que se intente
        if (et_screen.text.matches(Regex("^.*\\d$"))) {
            et_screen.text.append(symbol)
        } else {
            et_screen.setText(et_screen.text.replaceFirst(Regex(".$"), symbol))
        }
    }

    /**
     * agrega un punto decimal a la operacion
     */
    private fun appendDot() {
        // se controla que no se puedan poner varios puntos seguidos ni detras de simbolos
        if (!et_screen.text.contains(".")) {
            et_screen.text.append(".")
        } else if (et_screen.text.contains(operatorsRegex)) {
            val rightSide = et_screen.text.split(operatorsRegex, 0)[1]
            if (!rightSide.contains(".")) {
                et_screen.text.append(".")
            }
        }
    }

    /**
     * deshace un digito/simbolo
     */
    private fun backspace() {
        et_screen.setText(et_screen.text.dropLast(1))
    }

    private fun snack(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
}