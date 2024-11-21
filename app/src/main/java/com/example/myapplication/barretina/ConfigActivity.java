package com.example.myapplication.barretina;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ConfigActivity extends AppCompatActivity {

    private static final String CONFIG_FILE_NAME = "config.xml";
    private EditText urlEditText;
    private EditText nombreEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf);

        // Referencias a los EditText
        urlEditText = findViewById(R.id.Url);
        nombreEditText = findViewById(R.id.Nom);

        // Cargar configuración existente
        cargarConfig();
    }

    public void cargarConfig() {
        try {
            FileInputStream fileInputStream = openFileInput(CONFIG_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(inputStreamReader));

            Element root = document.getDocumentElement();
            String url = root.getElementsByTagName("url").item(0).getTextContent();
            String nombre = root.getElementsByTagName("nombre").item(0).getTextContent();

            urlEditText.setText(url);
            nombreEditText.setText(nombre);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No se pudo cargar la configuración", Toast.LENGTH_SHORT).show();
        }
    }

    public void guardarConfig(View view) {
        String url = urlEditText.getText().toString();
        String nombre = nombreEditText.getText().toString();

        if (url.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos, por favor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("config");
            document.appendChild(root);

            Element urlElement = document.createElement("url");
            urlElement.appendChild(document.createTextNode(url));
            root.appendChild(urlElement);

            Element nombreElement = document.createElement("nombre");
            nombreElement.appendChild(document.createTextNode(nombre));
            root.appendChild(nombreElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("indent", "yes");

            FileOutputStream fileOutputStream = openFileOutput(CONFIG_FILE_NAME, MODE_PRIVATE);
            transformer.transform(new DOMSource(document), new StreamResult(fileOutputStream));

            Toast.makeText(this, "Configuración guardada correctamente", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad al guardar

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la configuración", Toast.LENGTH_SHORT).show();
        }
    }
}
