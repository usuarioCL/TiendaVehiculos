package com.example.apptiendavehiculos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Buscar extends AppCompatActivity {

    SearchView svBuscar;
    Button btnBuscar , btnEditar, btnEliminar;
    ListView lstVehiculosEncontrados;

    // URL del servicio
    private final String URL = "http://192.168.18.20:3000/vehiculos";

    //Canal de comunicación
    RequestQueue requestQueue;
    ArrayList<String> vehiculosDisplayList;
    ArrayAdapter<String> adapter;
    private JSONObject currentVehicleData; // Para almacenar los datos del vehículo actual

    private void loadUI(){
        svBuscar = findViewById(R.id.svBuscar);
        btnBuscar = findViewById(R.id.btnBuscar);
        lstVehiculosEncontrados = findViewById(R.id.lstVehiculosEncontrados);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        btnEditar.setEnabled(false); // Deshabilitar el botón Editar
        btnEliminar.setEnabled(false); // Deshabilitar el botón Eliminar
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadUI();

        requestQueue = Volley.newRequestQueue(this);
        vehiculosDisplayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vehiculosDisplayList);
        lstVehiculosEncontrados.setAdapter(adapter);

        btnBuscar.setOnClickListener(view -> performSearchByPlaca());

        //Configurar el Search View para buscar al enviar  el texto
        svBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearchByPlaca();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // Limpiar la Vista si el texto es vacio
                if (newText.isEmpty()){
                    vehiculosDisplayList.clear();
                    adapter.notifyDataSetChanged();

                }
                return false;
            }
        });

        btnEditar.setOnClickListener(view -> {
            if (currentVehicleData != null) {
                Intent intent = new Intent(Buscar.this, Editar.class);
                intent.putExtra("vehiculoJson", currentVehicleData.toString());
                startActivity(intent);
            } else {
                Toast.makeText(Buscar.this, "Primero busque y encuentre un vehículo", Toast.LENGTH_SHORT).show();
            }
        });
    }//onCreate

    private void performSearchByPlaca() {
        String placa = svBuscar.getQuery().toString().trim();
        if (placa.isEmpty()) {
            Toast.makeText(this, "Ingrese la placa a buscar", Toast.LENGTH_SHORT).show();
            vehiculosDisplayList.clear(); // Limpiar resultados si la búsqueda está vacía
            adapter.notifyDataSetChanged();
            return;
        }

        String searchUrl = URL + "/" + Uri.encode(placa);
        Log.d("SearchURL", "Buscando en: " + searchUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                searchUrl,
                null,
                response -> {
                    Log.i("SearchResponse", response.toString());
                    vehiculosDisplayList.clear(); // Limpiar resultados anteriores
                    currentVehicleData = response; // Almacenar los datos del vehículo actual
                    if (response.length() == 0) {
                        Toast.makeText(Buscar.this, "No se encontró vehículo con placa: " + placa, Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            // 'response' es el JSONObject del vehículo encontrado
                            JSONObject vehiculo = response; // Tomamos el primer (y único) elemento

                            String marca = vehiculo.optString("marca", "N/A");
                            String modelo = vehiculo.optString("modelo", "N/A");
                            String color = vehiculo.optString("color", "N/A");
                            String precio = vehiculo.optString("precio", "N/A");
                            String placaEncontrada = vehiculo.optString("placa", "N/A");

                            String displayText = "Placa: " + placaEncontrada +
                                    "\nMarca: " + marca +
                                    "\nModelo: " + modelo +
                                    "\nColor: " + color +
                                    "\nPrecio: $" + precio;
                            vehiculosDisplayList.add(displayText);
                            btnEditar.setEnabled(true); // Habilitar el botón Editar
                            btnEliminar.setEnabled(true); // Habilitar el botón Eliminar

                        } catch (Exception e) {
                            Log.e("SearchParseError", "Error al parsear vehículo: " + e.getMessage());
                            Toast.makeText(Buscar.this, "Error al procesar los datos del vehículo", Toast.LENGTH_SHORT).show();
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notificar al adaptador para que actualice la lista
                },
                error -> {
                    Log.e("SearchError", "Error en búsqueda: " + error.toString());
                    vehiculosDisplayList.clear(); // Limpiar resultados en caso de error
                    adapter.notifyDataSetChanged();
                    currentVehicleData = null; // Restablecer el vehículo actual
                    btnEditar.setEnabled(false); // Deshabilitar el botón Editar
                    btnEliminar.setEnabled(false); // Deshabilitar el botón Eliminar

                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        Toast.makeText(Buscar.this, "Vehículo no encontrado (placa: " + placa + ")", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Buscar.this, "Error en la búsqueda. Verifique la conexión o la placa.", Toast.LENGTH_LONG).show();
                    }
                    vehiculosDisplayList.clear();
                    adapter.notifyDataSetChanged();
                }
        );
        requestQueue.add(jsonObjectRequest);
    }


}