package com.example.apptiendavehiculos;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Listar extends AppCompatActivity {

    ListView lstVehiculos;
     private final String URL = "http://192.168.18.20:3000/vehiculos";


     //Canal de comunicación
     RequestQueue requestQueue;

    private void loadUI(){
        lstVehiculos = findViewById(R.id.lstVehiculos);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            //Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadUI();
        getData();


    }//onCreate

    /**
     Obtiene los datos del web service
     */
    private void getData(){
        //1. Habilitar un canal de comunicación
        requestQueue = Volley.newRequestQueue(this);

        //2. Preparar datos del servicio ¿Qué datos enviara el servicio? Rpta Arreglo de datos JSON
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //Log.d("Datos recibidos:",jsonArray.toString());
                        renderData(jsonArray);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Error en el servicio:", volleyError.toString());
                    }
                }

        );


        //3. Ejecutar la solicitud
        requestQueue.add(jsonArrayRequest);
    }//getData()

    private void renderData(JSONArray jsonArray){
        try {
            ArrayAdapter adapter; //Mostrar datos en el ListView
            ArrayList<String> listaVehiculos = new ArrayList<>(); //Contenedor datos

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                listaVehiculos.add(jsonObject.getString("marca") + " " + jsonObject.getString("modelo"));
            }

            //contexto, "plantilla", datos
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaVehiculos);
            lstVehiculos.setAdapter(adapter);
        }catch (Exception error){
            Log.e("Error renderizado:", "Mensaje: " + error.getMessage(), error);
        }
    }
}