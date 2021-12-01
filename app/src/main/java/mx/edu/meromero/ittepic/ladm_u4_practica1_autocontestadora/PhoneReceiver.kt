package mx.edu.meromero.ittepic.ladm_u4_practica1_autocontestadora

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PhoneReceiver : BroadcastReceiver() {
    val baseRemota = FirebaseFirestore.getInstance()
    var telephonyManager: TelephonyManager? = null
    var status = false
    var contacto = ""
    var activado=false


    override fun onReceive(context: Context, intent: Intent?) {

        telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


                    val listenerPhone: PhoneStateListener = object : PhoneStateListener() {

                        override fun onCallStateChanged(state: Int, phoneNumber: String) {
                            super.onCallStateChanged(state, phoneNumber)
                            //Toast.makeText(context,state.toString() + " "+phoneNumber,Toast.LENGTH_LONG).show()

                            //status = false

                            when (state) {
                                TelephonyManager.CALL_STATE_RINGING -> {
                                    Toast.makeText(
                                        context,
                                        "Telefono: " + phoneNumber,
                                        Toast.LENGTH_LONG
                                    ).show()


                                    baseRemota.collection("listaNegra")
                                        .whereEqualTo("telefono", phoneNumber)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            contacto = ""
                                            for (document in documents) {
                                                contacto = document.getString("nombre")!!
                                            }
                                            if (contacto.equals("")) {
                                            } else {
                                                envioSMS(
                                                    phoneNumber,
                                                    "NO DEVOLVERE TU LLAMADA, POR FAVOR NO INSISTAS"
                                                )

                                                var datosInsertar = hashMapOf(
                                                    "nombre" to contacto,
                                                    "telefono" to phoneNumber

                                                )
                                                baseRemota.collection("noDeseadas")
                                                    .add(datosInsertar)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Se agrego a llamadas no deseadas",
                                                            Toast.LENGTH_LONG
                                                        )
                                                    }

                                                Toast.makeText(
                                                    context,
                                                    "Se envio en SMS",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                        }

                                    baseRemota.collection("listaBlanca")
                                        .whereEqualTo("telefono", phoneNumber)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            contacto = ""
                                            for (document in documents) {
                                                contacto = document.getString("nombre")!!
                                            }
                                            if (contacto.equals("")) {
                                            } else {
                                                envioSMS(
                                                    phoneNumber,
                                                    "ESTOY OCUPADO, TE LLAMO EN UN MOMENTO"
                                                )

                                                Toast.makeText(
                                                    context,
                                                    "Se envio en SMS",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                        }

                                }
                            }
                        }
                    }

                    if (!isLitening) {
                        telephonyManager!!.listen(
                            listenerPhone,
                            PhoneStateListener.LISTEN_CALL_STATE
                        )
                        isLitening = true
                    }



        }
        companion object {
            var isLitening = false
        }

    private fun envioSMS(numero:String,mensaje:String) {
        SmsManager.getDefault().sendTextMessage(numero,null,
            mensaje,null,null)
    }
}