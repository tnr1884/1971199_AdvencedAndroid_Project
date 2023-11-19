package com.example.week9

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UpdateDialog(private val adapter: CustomAdapter, private val viewModel: MyViewModel, private val pos : Int, private val item: Item) : DialogFragment() {
    private val db : FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("product")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.update_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<EditText>(R.id.updateprice).setText(viewModel.items[pos].price.toString())


        view.findViewById<Button>(R.id.productupdatebutton).setOnClickListener {
            var price = view.findViewById<EditText>(R.id.updateprice).text.toString().toInt()
            var radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
            println(item.id)
            var status = true
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioselled -> status=false
                R.id.radioselling -> status=true
            }
            println(status)
            itemsCollectionRef.document(item.id).update("isSelled", status).addOnSuccessListener {
                viewModel.items[pos].isSelled=status
                itemsCollectionRef.document(item.id).update("price", price).addOnSuccessListener {
                    viewModel.items[pos].price=price
                    adapter.notifyItemChanged(pos)

                }
                adapter.notifyItemChanged(pos)
                println(status)
                Toast.makeText(activity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                dismiss()

            }




        }

    }


}