package com.example.apptiendavehiculos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Registrar extends AppCompatActivity {

    EditText edtMarca, edtModelo, edtColor, edtPrecio, edtPlaca;

    Button btnGuardar;


    private final String URL = "http://192.168.18.20:3000/vehiculos";

    //Canal de comunicación
    RequestQueue requestQueue;

    private void loadUI(){
        edtMarca = findViewById(R.id.edtMarca);
        edtModelo = findViewById(R.id.edtModelo);
        edtColor  = findViewById(R.id.edtColor);
        edtPrecio  = findViewById(R.id.edtPrecio);
        edtPlaca    = findViewById(R.id.edtPlaca);
        btnGuardar = findViewById(R.id.btnGuardar);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            //Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadUI();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataWS();
            }
        });
    }//onCreate


    private void sendDataWS(){
        requestQueue = Volley.newRequestQueue(this);

        //Crear un objeto JSON con los datos requeridos
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("marca", edtMarca.getText().toString());
            jsonObject.put("modelo",edtModelo.getText().toString());
            jsonObject.put("color",edtColor.getText().toString());
            jsonObject.put("precio", edtPrecio.getText().toString());
            jsonObject.put("placa", edtPlaca.getText().toString());
        }catch (Exception error){
            Log.e("Error en JSON", error.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            String id = jsonObject.getString("id");
                            Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
                        }catch (Exception error) {
                            Log.e("Error en JSON", error.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Error en WS:", volleyError.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);


    }
}