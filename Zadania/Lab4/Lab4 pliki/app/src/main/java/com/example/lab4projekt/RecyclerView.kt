package com.example.lab4projekt

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecyclerViewFun(listItems: List<List<String>>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    LazyColumn (
        modifier = modifier.background(Color.Gray)
    ) {
        items(listItems) { item ->
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.height(32.dp).background(Color.LightGray)
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                var name = item[0]
                if (name.length > 18) {
                    name = name.substring(0, 15) + "..."
                }

                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.weight(0.47f).padding(4.dp).fillMaxHeight()
                ) {
                    Text(
                        text = name,
                        fontSize = 16.sp
                    )
                }

                val phoneNumber = item[1]

                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.weight(0.47f).padding(4.dp).fillMaxHeight()
                ) {
                    Text(
                        text = "   $phoneNumber",
                        fontSize = 13.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Zadzwoń do $phoneNumber",
                    tint = Color.Green,
                    modifier = Modifier
                        .weight(0.09f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clickable {
                            if (phoneNumber.isBlank()) {
                                Toast.makeText(context, "Nieprawidłowy numer telefonu!", Toast.LENGTH_SHORT).show()
                            } else {
                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phoneNumber")
                                }
                                context.startActivity(callIntent)
                            }
                        }
                )

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Wyślij wiadomość do $phoneNumber",
                    tint = Color.Magenta,
                    modifier = Modifier
                        .weight(0.09f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clickable {
                            if (phoneNumber.isBlank()) {
                                Toast.makeText(context, "Nieprawidłowy numer telefonu!", Toast.LENGTH_SHORT).show()
                            } else {
                                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:$phoneNumber")
                                }
                                context.startActivity(smsIntent)
                            }
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }

    Spacer(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Gray))
}
