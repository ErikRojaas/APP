package com.example.myapplication.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CONFIG_FILE_NAME = "config.xml";
    private WebSocket mWebSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inici); // El layout inicial

        // Cargar configuración cuando se inicia la aplicación
        cargarConfig();
    }

    // Método para guardar configuración en XML
    public void guardarConfig(View view) {
        EditText urlEditText = findViewById(R.id.Url);
        EditText nombreEditText = findViewById(R.id.Nom);

        String url = urlEditText.getText().toString();
        String nom = nombreEditText.getText().toString();

        if (url.isEmpty() || nom.isEmpty()) {
            Toast.makeText(this, "Completa tots els camps, si us plau", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Conectar al servidor WebSocket
            connectToProxmoxWebSocket();

            // Cambiar a otra actividad si es necesario
            Intent intent = new Intent(MainActivity.this, PruebasActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la configuració", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectToProxmoxWebSocket() {
        OkHttpClient client = new OkHttpClient();
        String serverUrl = "wss://barretina5.ieti.site";

        // Crear una solicitud WebSocket
        Request request = new Request.Builder()
                .url(serverUrl)
                .build();

        // Crear un WebSocketListener para manejar los eventos de WebSocket
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Conectado a Proxmox", Toast.LENGTH_SHORT).show();
                    // Solicitar el archivo PRODUCTES.XML
                    mWebSocket = webSocket;
                    mWebSocket.send("request_productes_xml"); // Enviar la solicitud para obtener el archivo PRODUCTES.XML
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                // Enviar el archivo XML a la actividad de pruebas
                Intent intent = new Intent(MainActivity.this, PruebasActivity.class);
                intent.putExtra("productes_xml", text); // Pasa el XML como string
                startActivity(intent);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error al conectar a Proxmox: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        };

        // Conectar al servidor WebSocket
        client.newWebSocket(request, listener);
    }

    // Método para cargar la configuración desde un archivo XML
    public void cargarConfig() {
        try {
            // Intentar leer el archivo XML de configuración
            FileInputStream fileInputStream = openFileInput(CONFIG_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(inputStreamReader));

            // Obtener los elementos del XML
            Element root = document.getDocumentElement();
            String url = root.getElementsByTagName("url").item(0).getTextContent();
            String nombre = root.getElementsByTagName("nombre").item(0).getTextContent();

            // Mostrar los datos en los EditText
            EditText urlEditText = findViewById(R.id.Url);
            EditText nombreEditText = findViewById(R.id.Nom);
            urlEditText.setText(url);
            nombreEditText.setText(nombre);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No es va poder carregar la configuració", Toast.LENGTH_SHORT).show();
        }
    }
}
