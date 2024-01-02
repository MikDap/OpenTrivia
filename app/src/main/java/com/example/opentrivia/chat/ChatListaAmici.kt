package com.example.opentrivia.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.gms.tasks.Task



class ChatListaAmici : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val userKeyMap = mutableMapOf<String, String>()  // Chiave: ID utente, Valore: Nome amico
    private val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var displayname = FirebaseAuth.getInstance().currentUser?.displayName.toString()
    private var amiciRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("amici")
    private var userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
    private var chatMiaRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("chat")
    private lateinit var chatID: String
    private var chatRef = FirebaseDatabase.getInstance().getReference("chat")
    private lateinit var userIDOther: String
    private lateinit var usernameOther: String
    private lateinit var userRefListener: ValueEventListener
    private lateinit var amiciRefListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_lista_amici, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFriends)

        //INSERIRE CHE QUANDO SCHIACCIO AMICO MI APRE CHATFRAGMENT
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager


        val adapter = ChatListaAmiciAdapter(userKeyMap, object : OnAmicoClickListener {

            override fun onAmicoClick(userId: String, username: String) {

                userIDOther = userId
                usernameOther = username


               userRefListener = userRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(user: DataSnapshot) {

                        if (user.hasChild("chat")) {
                            val listachat = user.child("chat")

                            val listachatIterator = listachat.children.iterator()

                            controllaChat(listachatIterator, userId, false) {
                                ->

                                val chatFragment = ChatFragment()
                                val bundle = Bundle()
                                bundle.putString("userId", userId)
                                bundle.putString("username", username)
                                bundle.putString("chatID", chatID)
                                chatFragment.arguments = bundle

                                val transaction =
                                    requireActivity().supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.fragmentContainerViewChat, chatFragment)
                                transaction.addToBackStack(null)
                                transaction.commit()
                            }
                        } else {

                            var userOtherRef = FirebaseDatabase.getInstance().getReference("users")
                                .child(userIDOther)

                            val nuovaChatRef = userRef.child("chat").push()
                            chatID = nuovaChatRef.key.toString()


                            userOtherRef.child("chat").child(chatID).child("partecipante")
                                .child(uid).setValue(displayname)
                            val settato = userRef.child("chat").child(chatID).child("partecipante")
                                .child(userIDOther).setValue(usernameOther)

                            settato.addOnSuccessListener {

                                // Ottieni la chiave univoca generata da Firebase

                                Log.d("chatidELSE", chatID)

                                setPartecipanti()

                                val chatFragment = ChatFragment()
                                val bundle = Bundle()
                                bundle.putString("userId", userId)
                                bundle.putString("username", username)
                                bundle.putString("chatID", chatID)
                                chatFragment.arguments = bundle

                                val transaction =
                                    requireActivity().supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.fragmentContainerViewChat, chatFragment)
                                transaction.addToBackStack(null)
                                transaction.commit()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Gestisci l'errore, se necessario
                    }
                }.also { listener ->
                        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                super.onDestroy(owner)
                                amiciRef.removeEventListener(listener) // Rimuovi il listener quando la vista è distrutta
                            }
                        })
                    })


            }
        })

        recyclerView.adapter = adapter


        // Aggiungi un ValueEventListener per ottenere i dati degli amici da Firebase
       amiciRefListener = amiciRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userKeyMap.clear() // Svuota la lista prima di popolarla con i nuovi dati
                for (amicoSnapshot in snapshot.children) {

                    val idAmico = amicoSnapshot.key.toString()
                    val nomeAmico = amicoSnapshot.getValue(String::class.java).toString()
                    if (idAmico != null) {
                        userKeyMap.put(idAmico, nomeAmico)
                    }
                }
                adapter.notifyDataSetChanged() // Aggiorna la RecyclerView quando i dati cambiano
            }

            override fun onCancelled(error: DatabaseError) {
                // Gestisci l'errore, se necessario
            }
        })


    }


    interface OnAmicoClickListener {
        fun onAmicoClick(userId: String, username: String)
    }





    fun setPartecipanti(){
Log.d("entraSetParte","si")
                chatRef.child(chatID).child("partecipanti").child(uid).setValue(displayname)
                chatRef.child(chatID).child("partecipanti").child(userIDOther).setValue(usernameOther)

    }




    fun controllaChat(listaChatIterator: Iterator<DataSnapshot>, userId: String,chatTrovata:Boolean, callback: () -> Unit) {

        if (!listaChatIterator.hasNext()) {

            if (!chatTrovata) {
                var userOtherRef = FirebaseDatabase.getInstance().getReference("users").child(userIDOther)
                val nuovaChatRef = userRef.child("chat").push()

                chatID = nuovaChatRef.key.toString()
                userOtherRef.child("chat").child(chatID).child("partecipante").child(uid).setValue(displayname)
                userRef.child("chat").child(chatID).child("partecipante").child(userIDOther).setValue(usernameOther)
                setPartecipanti()

            }
            callback()
            return
        }

        val chat = listaChatIterator.next()

            if (chat.child("partecipante").hasChild(userId)){
                chatID = chat.key.toString()
                controllaChat(listaChatIterator, userId,true, callback)
            }
        else {
                controllaChat(listaChatIterator, userId,chatTrovata, callback)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        userRef.removeEventListener(userRefListener)
        amiciRef.removeEventListener(amiciRefListener)
    }
}