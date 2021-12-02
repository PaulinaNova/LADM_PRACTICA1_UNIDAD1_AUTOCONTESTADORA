package mx.edu.meromero.ittepic.ladm_u4_practica1_autocontestadora

import android.content.BroadcastReceiver
import android.content.Context
import android.telephony.TelephonyManager

import android.telephony.PhoneStateListener

import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


class RecibidorLlamadas : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {


            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            telephonyManager.listen(
                PhoneCallListener(context),
                PhoneStateListener.LISTEN_CALL_STATE
            )

    }

    /*private fun call(context: Context) {
        val phoneListener = PhoneCallListener(context)
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }*/

    private class PhoneCallListener(val context: Context) : PhoneStateListener() {
        var isPhoneCalling = false
        var wasRinging = false
        var activador =true
        var contacto = ""
        val baseRemota = FirebaseFirestore.getInstance()
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                if(activador) {
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
                    activador = false
                }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                isPhoneCalling = true
            }
            if (TelephonyManager.CALL_STATE_IDLE == state) {
                isPhoneCalling = false

            }
        }

        private fun envioSMS(numero:String,mensaje:String) {
            SmsManager.getDefault().sendTextMessage(numero,null,
                mensaje,null,null)
        }
    }


}