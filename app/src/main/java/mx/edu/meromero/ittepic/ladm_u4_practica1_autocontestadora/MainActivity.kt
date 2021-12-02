package mx.edu.meromero.ittepic.ladm_u4_practica1_autocontestadora

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import android.content.ComponentName
import android.view.View


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var pausado = false
    var cambio = false
    private var nTelephonyManager: TelephonyManager?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // nTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        disableBroadcastReceiver()

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS),1)
        val permission = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE),1)
        }
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_CALL_LOG), 1)

        }

       btnRegistrar.setOnClickListener {
           val opcion = findViewById<Spinner>(R.id.spnListas)
           var op = opcion.selectedItemPosition

           when(op){
               0->{
                   subirLista("listaBlanca")
                    Toast.makeText(this,"Seleccionaste lista blanca",Toast.LENGTH_LONG).show()
               }
               1->{
                   subirLista("listaNegra")
                   Toast.makeText(this,"Seleccionaste lista negra",Toast.LENGTH_LONG).show()
               }
           }

       }

        button.setOnClickListener {

            if (cambio){
                cambio = false
                 button.setText("Activar contestadora")
                disableBroadcastReceiver()
                }else{
                cambio = true
                button.setText("Desactivar contestadora")
                enableBroadcastReceiver()
                }
        }

    }



    private fun subirLista(lista:String){
        // Create a new user with a first, middle, and last name
        val contacto = hashMapOf(
            "nombre" to editTextTextPersonName.text.toString(),
            "telefono" to editTextPhone.text.toString()
        )

        // Add a new document with a generated ID
        db.collection(lista)
            .add(contacto)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                editTextTextPersonName.setText("")
                editTextPhone.setText("")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


    private fun envioSMS(numero:String,mensaje:String) {
        SmsManager.getDefault().sendTextMessage(numero,null,
            mensaje,null,null)
        Toast.makeText(this,"Se envio el sms",Toast.LENGTH_LONG).show()
    }

   /* @SuppressLint("Range")
    private fun listaLlamadasPerdidas(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_CALL_LOG),1)
        }else{
            var selection:String = CallLog.Calls.TYPE + "=" + CallLog.Calls.INCOMING_TYPE
            var cursor: Cursor?=null

            try {
                cursor = contentResolver.query(Uri.parse("content://call_log/calls"),null,selection,null,null)
                var registros =""

                while(cursor?.moveToNext()!!){
                    val numero:String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                    val localizacion:String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION))

                    registros = "\nNumero: "+numero+"\nLocalizacion: "+ localizacion+"\n"

                    llamadas.add(registros)
                }
            }catch (ex: Exception){
                Toast.makeText(this,"Error: "+ex,Toast.LENGTH_LONG).show()
            }
            finally {
                listalLamadas.adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,llamadas)
                cursor?.close()
            }
        }
    }*/
   open fun enableBroadcastReceiver() {
       val receiver = ComponentName(this, RecibidorLlamadas::class.java)
       val pm = this.packageManager
       pm.setComponentEnabledSetting(
           receiver,
           PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
           PackageManager.DONT_KILL_APP
       )
       Toast.makeText(this, "Enabled broadcast receiver", Toast.LENGTH_SHORT).show()
   }

    /**
     * This method disables the Broadcast receiver registered in the AndroidManifest file.
     * @param view
     */
    fun disableBroadcastReceiver() {
        val receiver = ComponentName(this, RecibidorLlamadas::class.java)
        val pm = this.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        Toast.makeText(this, "Disabled broadcst receiver", Toast.LENGTH_SHORT).show()
    }

}