package com.example.myapplication.barretina;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashSet;

public class PruebasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba);

        // Referencia al RadioGroup donde se añadirán las categorías
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        try {
            // Cargar el archivo XML desde los assets
            InputStream inputStream = getAssets().open("PRODUCTES.XML");

            // Parsear el archivo XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            // Obtener todos los elementos "producto"
            NodeList nodeList = document.getElementsByTagName("producto");

            // Usar un HashSet para almacenar categorías únicas
            HashSet<String> categoriasUnicas = new HashSet<>();

            // Iterar sobre cada elemento "producto" para obtener sus categorías
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element producto = (Element) nodeList.item(i);

                // Obtener el atributo "categoria"
                String categoriaAttr = producto.getAttribute("categoria");

                // Las categorías pueden estar separadas por comas, así que dividimos el string
                String[] categorias = categoriaAttr.split(",");

                // Añadir cada categoría al HashSet para evitar duplicados
                for (String categoria : categorias) {
                    categoriasUnicas.add(categoria.trim());
                }
            }

            // Añadir cada categoría única como un RadioButton en el RadioGroup
            for (String categoria : categoriasUnicas) {
                RadioButton radioButton = new RadioButton(this);

                // Convertir la primera letra de cada categoría a mayúscula
                String categoriaFormateada = capitalizeFirstLetter(categoria);

                // Ajustar el texto y tamaño de letra
                radioButton.setText(categoriaFormateada);
                radioButton.setTextSize(20);  // Cambiar el tamaño del texto a 30sp
                radioButton.setTextColor(getResources().getColor(android.R.color.white)); // Texto en blanco

                // Aplicar el fondo circular
                radioButton.setBackgroundResource(R.drawable.circle_background);

                // Ajustar el padding/margen alrededor para evitar recortes en los bordes
                radioButton.setPadding(20, 20, 20, 20);

                // Ajustar el ancho del RadioButton para que ocupe todo el espacio disponible
                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, dpToPx(55))); // 40sp de altura

                // Añadir un margen entre los RadioButtons
                RadioGroup.LayoutParams layoutParams = (RadioGroup.LayoutParams) radioButton.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, dpToPx(16));  // Margen inferior de 16dp

                // Aplicar los parámetros de disposición al RadioButton
                radioButton.setLayoutParams(layoutParams);

                // Añadir el RadioButton al RadioGroup
                radioGroup.addView(radioButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para poner la primera letra en mayúscula
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    // Método para convertir sp a px
    private int dpToPx(int sp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (sp * density + 0.5f); // Convertir sp a px
    }
}
