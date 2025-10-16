package com.example.tiptime

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}
@StringRes val tipRoundingText : Int = R.string.round_up_tip
@Composable
fun TipTimeLayout() {
    val context = LocalContext.current
    val defaultTip : Int = 15

    var isRounded by remember {mutableStateOf(false)}
    var amountInput by remember { mutableStateOf("") }
    var tipPercentageAmount by remember { mutableStateOf(defaultTip) }
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val initialTip = calculateTip(amount,tipPercentageAmount,isRounded)
    var tip by remember { mutableStateOf(initialTip) }



    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            value = amountInput,
            onValueChanged = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        EditTipAmountField(
            modifier = Modifier,
            onValueChanged = {tipPercentageAmount = it.toIntOrNull() ?: 0},
            value = tipPercentageAmount.toString()
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            Text(text = stringResource(tipRoundingText), modifier = Modifier.padding(16.dp))
            androidx.compose.material3.Switch(
                                            checked = isRounded,
                                            onCheckedChange = {
                                                isRounded = !isRounded
                                            })
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box {
            Button(onClick = {
                if(tipPercentageAmount <= 0 || amount <= 0) {
                   showEmptyAlert(context,tipPercentageAmount)
                }
                else {
                    tip = calculateTip(amount, tipPercentageAmount, isRounded)
                }
            }) {
                Text(stringResource(R.string.tip_calculate))
            }
        }

        Spacer(modifier = Modifier.height(150.dp))

        Text(
            text = stringResource(R.string.tip_amount,tip),
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(150.dp))
    }
}
fun showEmptyAlert(context:Context,tipPercentageAmount:Int){
    val alert = if(tipPercentageAmount <= 0) "tip percentage" else "bill amount"
    val alertText = ContextCompat.getString(context,R.string.greater_than)
    //stringResource() es función composable y solamente puede ser accesado desde una, para eso usa ContextCompat
    val formattedAlertText = String.format(alertText,alert)

    Toast.makeText(
        context,
        formattedAlertText,
        Toast.LENGTH_SHORT
    ).show()
}
@Composable
fun EditTipAmountField(modifier:Modifier,onValueChanged: (String) -> Unit,value: String){

    TextField(
        value = value,
        onValueChange = onValueChanged,
        label = {Text(stringResource(R.string.tip_percentage,value))},
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = modifier.fillMaxWidth()
    )
}
@Composable
fun EditNumberField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(stringResource(R.string.bill_amount)) },
        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
        )
    )
}

private fun calculateTip(amount : Double, tipPercent : Int,roundTip : Boolean): String {
    val tipCalculation = (tipPercent.toDouble() / 100)* amount
    val roundedTip = kotlin.math.ceil(tipCalculation)
    val formattedNumber =
        if(roundTip)
            NumberFormat.getCurrencyInstance().format(roundedTip)
        else
            NumberFormat.getCurrencyInstance().format(tipCalculation)

    return  formattedNumber
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}

