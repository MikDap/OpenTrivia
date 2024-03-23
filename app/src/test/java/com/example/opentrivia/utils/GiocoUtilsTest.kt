package com.example.opentrivia.utils

import org.junit.Test
import org.junit.Assert.*


class GiocoUtilsTest {


    //L'obiettivo di questo test è verificare che la funzione getCategoria(topic), ritorna un
    //numero corrispondente al topic passato alla funzione, i numeri sono presi dal server API
    // per cercare le domande relative al topic.
    @Test
    fun getCategoriaTest() {

        // Test per la categoria "culturaPop"
        val categorieCulturaPop = arrayOf("9","10","11","12","13","14","15","16","29","31","32")
        assertTrue("culturaPop",categorieCulturaPop.contains(GiocoUtils.getCategoria("culturaPop")))

        // Test per la categoria "sport"
        assertEquals("sport", "21", GiocoUtils.getCategoria("sport"))

        // Test per la categoria "storia"
        assertEquals("storia", "23", GiocoUtils.getCategoria("storia"))

        // Test per la categoria "geografia"
        assertEquals("geografia", "22", GiocoUtils.getCategoria("geografia"))

        // Test per la categoria "arte"
        assertEquals("arte", "25", GiocoUtils.getCategoria("arte"))

        // Test per la categoria "scienze"
        val categorieScienze = arrayOf("17", "18", "19", "30")
        assertTrue("scienze",categorieScienze.contains(GiocoUtils.getCategoria("scienze")))

    }

    //L'obiettivo di questo test è verificare che getCategoria(topic)
    //lanci un eccezione nel caso l'argomento passato non è un topic
    @Test
    fun getCategoriaTest2(){

        // Test per un argomento non valido
        try {
            GiocoUtils.getCategoria("argomentoNonValido")
            fail("Dovrebbe lanciare un'eccezione per un argomento non valido")
        } catch (e: IllegalArgumentException) {
            // Se l'eccezione è stata lanciata correttamente, il test ha successo
        }
    }
}