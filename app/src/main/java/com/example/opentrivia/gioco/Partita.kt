package com.example.opentrivia.gioco

data class Partita(
    val idPartita: String,
    val modalita: String,
    val difficolta: String,
    val idPlayer1: String,
    val namePlayer1: String,
    val idPlayer2: String,
    val namePlayer2: String,
    val turno: String? = null //  il campo `turno` puo essere nullable (se la modalità è arg singolo o a tempo)
) {
    constructor(
        idPartita: String,
        modalita: String,
        difficolta: String,
        idPlayer1: String,
        namePlayer1: String,
        idPlayer2: String,
        namePlayer2: String
    ) : this(idPartita, modalita, difficolta, idPlayer1, namePlayer1, idPlayer2, namePlayer2, null)
}
