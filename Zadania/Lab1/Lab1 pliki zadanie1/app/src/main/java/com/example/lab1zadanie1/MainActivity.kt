package com.example.lab1zadanie1

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab1zadanie1.ui.theme.Lab1Zadanie1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab1Zadanie1Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(24, 24, 24)).padding(16.dp)
    ) {
        val spacerWidth16 = Modifier.width(16.dp)
        val spacerHeight16 = Modifier.height(16.dp)
        val colorGreen = Color(63, 175, 50)
        val colorLightRed = Color(255, 75, 0)
        val colorWhite = Color(255, 255, 255)
        val colorLightGray = Color(48, 48, 48)
        val colorLightLightGray = Color(80, 80, 80)
        val buttonModifierMaxWidth = Modifier.fillMaxWidth().aspectRatio(1f)
        val context = LocalContext.current
        var textCount by remember { mutableStateOf("") }
        var textResult by remember { mutableStateOf("0") }

        Spacer(spacerHeight16)

        Row(
            modifier = Modifier.fillMaxWidth().defaultMinSize(128.dp, 64.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = textCount, color = colorWhite, fontSize = 48.sp, textAlign = TextAlign.End)
        }

        Spacer(spacerHeight16)

        Row(
            modifier = Modifier.fillMaxWidth().defaultMinSize(128.dp, 64.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = textResult, color = colorLightLightGray, fontSize = 48.sp, textAlign = TextAlign.End)
        }

        Spacer(spacerHeight16)

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(colorLightLightGray).height(2.dp),
                    horizontalArrangement = Arrangement.Center
                ) { }

                Spacer(spacerHeight16)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.weight(3f).align(Alignment.Bottom)
                    ) {
//                        CalculatorButton(
//                            modifier = buttonModifierMaxWidth,
//                            text = "|",
//                            textColor = colorGreen,
//                            fontSize = 36.sp,
//                            backgroundColor = colorLightGray,
//                            onClick = {
//                                if (textCount.length >= 13) {
//                                    showToast(context, "Limit znaków przekroczony!")
//                                } else {
//                                    textCount += "|"
//                                    textResult = evaluateExpression(textCount)
//                                }
//                            }
//                        )
//
//                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "7",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "7"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "4",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "4"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "1",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "1"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "⌫",
                            textColor = colorWhite,
                            fontSize = 36.sp,
                            backgroundColor = colorGreen,
                            onClick = {
                                if (textCount.isNotEmpty() && textCount.length > 1) {
                                    val x = textCount.substring(textCount.length - 2, textCount.length - 1).all { it.isDigit() }
                                    val y = textCount.substring(textCount.length - 1).all { it.isDigit() }
                                    textCount = textCount.substring(0, textCount.length - 1)
                                    textResult = if ((x && y) ||(!y && x)) {
                                        evaluateExpression(textCount)
                                    } else {
                                        "N/A"
                                    }
                                } else {
                                    textCount = ""
                                    textResult = "0"
                                }
                            }
                        )
                    }

                    Spacer(spacerWidth16)

                    Column(
                        modifier = Modifier.weight(3f).align(Alignment.Bottom)
                    ) {
//                        CalculatorButton(
//                            modifier = buttonModifierMaxWidth,
//                            text = "( ",
//                            textColor = colorGreen,
//                            backgroundColor = colorLightGray,
//                            onClick = {
//                                if (textCount.length >= 13) {
//                                    showToast(context, "Limit znaków przekroczony!")
//                                } else {
//                                    textCount += "("
//                                    textResult = evaluateExpression(textCount)
//                                }
//                            }
//                        )
//
//                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "8",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "8"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "5",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "5"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "2",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "2"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "0",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "0"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )
                    }

                    Spacer(spacerWidth16)

                    Column(
                        modifier = Modifier.weight(3f).align(Alignment.Bottom)
                    ) {
//                        CalculatorButton(
//                            modifier = buttonModifierMaxWidth,
//                            text = " )",
//                            textColor = colorGreen,
//                            backgroundColor = colorLightGray,
//                            onClick = {
//                                if (textCount.length >= 13) {
//                                    showToast(context, "Limit znaków przekroczony!")
//                                } else {
//                                    textCount += ")"
//                                    textResult = evaluateExpression(textCount)
//                                }
//                            }
//                        )
//
//                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "9",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "9"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "6",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "6"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "3",
                            textColor = colorWhite,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else {
                                    textCount += "3"
                                    textResult = evaluateExpression(textCount)
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "C",
                            textColor = colorLightRed,
                            backgroundColor = colorLightGray,
                            onClick = {
                                textCount = ""
                                textResult = "0"
                            }
                        )

//                        CalculatorButton(
//                            modifier = buttonModifierMaxWidth,
//                            text = ",",
//                            textColor = colorWhite,
//                            backgroundColor = colorLightGray,
//                            onClick = {
//                                if (textCount.length >= 13) {
//                                    showToast(context, "Limit znaków przekroczony!")
//                                } else {
//                                    textCount += ","
//                                    textResult = evaluateExpression(textCount)
//                                }
//                            }
//                        )
                    }

                    Spacer(spacerWidth16)

                    Column(
                        modifier = Modifier.weight(3f).align(Alignment.Bottom)
                    ) {
                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "÷",
                            textColor = colorGreen,
                            fontSize = 48.sp,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else if (textCount.isEmpty()) {
                                    textCount = ""
                                } else if (textCount.substring(textCount.length - 1) != "+"
                                    && textCount.substring(textCount.length - 1) != "−"
                                    && textCount.substring(textCount.length - 1) != "×"
                                    && textCount.substring(textCount.length - 1) != "÷") {
                                    textCount += "÷"
                                    textResult = "N/A"
                                } else {
                                    textCount = textCount.substring(0, textCount.length - 1) + "÷"
                                    textResult = "N/A"
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "×",
                            textColor = colorGreen,
                            fontSize = 48.sp,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else if (textCount.isEmpty()) {
                                    textCount = ""
                                } else if (textCount.substring(textCount.length - 1) != "+"
                                    && textCount.substring(textCount.length - 1) != "−"
                                    && textCount.substring(textCount.length - 1) != "×"
                                    && textCount.substring(textCount.length - 1) != "÷") {
                                    textCount += "×"
                                    textResult = "N/A"
                                } else {
                                    textCount = textCount.substring(0, textCount.length - 1) + "×"
                                    textResult = "N/A"
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "–",
                            textColor = colorGreen,
                            fontSize = 48.sp,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else if (textCount.isEmpty()) {
                                    textCount = ""
                                } else if (textCount.substring(textCount.length - 1) != "+"
                                    && textCount.substring(textCount.length - 1) != "−"
                                    && textCount.substring(textCount.length - 1) != "×"
                                    && textCount.substring(textCount.length - 1) != "÷") {
                                    textCount += "−"
                                    textResult = "N/A"
                                } else {
                                    textCount = textCount.substring(0, textCount.length - 1) + "−"
                                    textResult = "N/A"
                                }
                            }
                        )

                        Spacer(spacerHeight16)

                        CalculatorButton(
                            modifier = buttonModifierMaxWidth,
                            text = "+",
                            textColor = colorGreen,
                            fontSize = 48.sp,
                            backgroundColor = colorLightGray,
                            onClick = {
                                if (textCount.length >= 13) {
                                    showToast(context, "Limit znaków przekroczony!")
                                } else if (textCount.isEmpty()) {
                                    textCount = ""
                                } else if (textCount.substring(textCount.length - 1) != "+"
                                    && textCount.substring(textCount.length - 1) != "−"
                                    && textCount.substring(textCount.length - 1) != "×"
                                    && textCount.substring(textCount.length - 1) != "÷") {
                                    textCount += "+"
                                    textResult = "N/A"
                                } else {
                                    textCount = textCount.substring(0, textCount.length - 1) + "+"
                                    textResult = "N/A"
                                }
                            }
                        )

//                        Spacer(spacerHeight16)
//
//                        CalculatorButton(
//                            modifier = buttonModifierMaxWidth,
//                            text = "⌫",
//                            textColor = colorWhite,
//                            fontSize = 36.sp,
//                            backgroundColor = colorGreen,
//                            onClick = {
//                                if (textCount.isNotEmpty() && textCount.length > 1) {
//                                    textCount = textCount.substring(0, textCount.length - 1)
//                                    textResult = evaluateExpression(textCount)
//                                } else {
//                                    textCount = ""
//                                    textResult = "0"
//                                }
//                            }
//                        )
                    }
                }
            }
        }
    }
}

// Funkcja do obliczania wartości
fun evaluateExpression(expression: String): String {
//    // Zamiana znaków specjalnych
//    val sanitizedExpression = expression
//        .replace("−", "-")
//        .replace("×", "*")
//        .replace("÷", "/")
//        .replace(",", ".")
//
//    // Sprawdzanie poprawności nawiasów
//    if (!areBracketsBalanced(sanitizedExpression)) {
//        return "Błąd:\nNiezamknięte\nnawiasy"
//    }

    // Kolekcje na znaki i liczby
    val numbers = mutableListOf<Long>()
    val letters = mutableListOf<String>()

    var actualNumber = ""
    for (i in expression.indices) {
        val char = expression[i]

        if (char.isDigit()) {
            actualNumber += char
        } else {
            if (actualNumber.isNotEmpty()) {
                numbers.add(actualNumber.toLong())
                actualNumber = ""
            }
            letters.add(char.toString())
        }
    }

    if (actualNumber.isNotEmpty()) {
        numbers.add(actualNumber.toLong())
    }

    if (numbers.size == 1) {
        return numbers[0].toString()
    } else {
        var i = 0
        while (i < letters.size) {
            val char = letters[i]

            if (char == "÷" || char == "×") {
                val newNumber = try {
                    when (char) {
                        "÷" -> numbers[i] / numbers[i + 1]
                        "×" -> numbers[i] * numbers[i + 1]
                        else -> null
                    }
                } catch (e: ArithmeticException) {
                    return "N/A"
                }
                if (newNumber != null) {
                    numbers[i] = newNumber
                    numbers.removeAt(i + 1)
                    letters.removeAt(i)
                    continue
                }
            }
            i++
        }

        i = 0
        while (i < letters.size) {
            val char = letters[i]
            println(char)
            if (char == "−" || char == "+") {
                val newNumber = when (char) {
                    "−" -> numbers[i] - numbers[i + 1]
                    "+" -> numbers[i] + numbers[i + 1]
                    else -> null
                }
                if (newNumber != null) {
                    numbers[i] = newNumber
                    numbers.removeAt(i + 1)
                    letters.removeAt(i)
                    continue
                }
            }
            i++
        }
    }

    return numbers[0].toString()
}

// // Funkcja pomocnicza do sprawdzania poprawności nawiasów
//fun areBracketsBalanced(expression: String): Boolean {
//    val stack = Stack<Char>()
//    expression.forEach { char ->
//        when (char) {
//            '(' -> stack.push(char)
//            ')' -> if (stack.isEmpty() || stack.pop() != '(') return false
//            '|' -> if (stack.isNotEmpty() && stack.peek() == '|') stack.pop() else stack.push('|')
//        }
//    }
//    return stack.isEmpty()
//}

var currentToast: Toast? = null

fun showToast(context: Context, message: String) {
    currentToast?.cancel()
    currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
    currentToast?.show()
}

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    fontSize: TextUnit = 32.sp,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, fontSize = fontSize, color = textColor, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab1Zadanie1Theme {
        MainScreen()
    }
}
