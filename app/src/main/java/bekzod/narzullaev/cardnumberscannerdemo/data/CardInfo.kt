package bekzod.narzullaev.cardnumberscannerdemo.data

import java.io.Serializable


data class CardInfo(
    val cardNumber: String,
    val expireDate: String
) : Serializable