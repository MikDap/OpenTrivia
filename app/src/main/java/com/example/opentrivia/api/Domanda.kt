package com.example.opentrivia.api

 data class Domanda (
     val category: String,
     val type: String,
     val difficulty: String,
     val question: String,
     val correct_answer: String,
     val incorrect_answers: List<String>
 )