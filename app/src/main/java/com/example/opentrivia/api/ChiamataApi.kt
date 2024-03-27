package com.example.opentrivia.api

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.text.HtmlCompat

class ChiamataApi(
    val tipo: String,
    val categoria: String,
    var difficolta: String
) {

var domanda: String = ""
 var risposta_corretta: String = ""
 var risposta_sbagliata_1: String = ""
 var risposta_sbagliata_2: String = ""
 var risposta_sbagliata_3: String = ""
    private var callback: TriviaQuestionCallback? = null

    fun fetchTriviaQuestion(callback: TriviaQuestionCallback) {
        this.callback = callback
        val questionsApi = RetrofitHelper.getInstance().create(ApiOpenTriviaInterface::class.java)

        GlobalScope.launch {
            val result = questionsApi.getTriviaQuestion(1, categoria.toInt(), difficolta, tipo)
            if (result != null) {



                if (tipo.equals("multiple")) {
                    domanda = decodificaHtml(result.body()?.results?.get(0)?.question.toString())
                    risposta_corretta = decodificaHtml(result.body()?.results?.get(0)?.correct_answer.toString())
                    risposta_sbagliata_1 = decodificaHtml(result.body()?.results?.get(0)?.incorrect_answers?.get(0).toString())
                    risposta_sbagliata_2 = decodificaHtml(result.body()?.results?.get(0)?.incorrect_answers?.get(1).toString())
                    risposta_sbagliata_3 = decodificaHtml(result.body()?.results?.get(0)?.incorrect_answers?.get(2).toString())
                    callback.onTriviaQuestionFetched(tipo,domanda, risposta_corretta, risposta_sbagliata_1, risposta_sbagliata_2, risposta_sbagliata_3)

                }





                if (tipo.equals("boolean")) {

                     domanda = result.body()?.results?.get(0)?.question.toString()

                     risposta_corretta =
                        result.body()?.results?.get(0)?.correct_answer.toString()

                     risposta_sbagliata_1 =

                        result.body()?.results?.get(0)?.incorrect_answers?.get(0).toString()
                    callback.onTriviaQuestionFetched(tipo,domanda, risposta_corretta, risposta_sbagliata_1,"","")
                }
            }
        }
    }



    fun decodificaHtml(frasedaDecodificare: String): String {
        val fraseDecodificata = HtmlCompat.fromHtml(frasedaDecodificare, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        return fraseDecodificata
    }
    interface TriviaQuestionCallback {
        fun onTriviaQuestionFetched(tipo:String,domanda: String, risposta_corretta: String, risposta_sbagliata_1: String, risposta_sbagliata_2: String, risposta_sbagliata_3: String)
    }


}