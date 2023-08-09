package com.example.opentrivia.listaAmici

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.MenuProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.opentrivia.R


class ListaAmiciFragment : Fragment() {
private lateinit var addFriendButton: Button
// private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.lista_amici, container, false)
        addFriendButton = view.findViewById(R.id.buttonAddFriend)
     //   recyclerView = view.findViewById(R.id.recyclerViewFriends)
  //      val layoutManager = LinearLayoutManager(this)
 //       recyclerView.layoutManager = layoutManager

  //      val friendsList: List<String> = // Inserisci la tua lista di amici qui

   //     val adapter = FriendsAdapter(friendsList)
   //     recyclerView.adapter = adapter


        addFriendButton.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_listaAmiciFragment_to_aggiungiAmico) }
return view
    }


}