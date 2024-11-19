package com.example.myapplication.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ListarTags extends AppCompatActivity {

    private List<Producto> productos; // Lista de productos filtrados
    public static List<Producto> productosSeleccionados = new ArrayList<>();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags);

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
            productos = new ArrayList<>();

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

                // Agregar producto a la lista de productos
                String nombre = producto.getElementsByTagName("nombre").item(0).getTextContent();
                double precio = Double.parseDouble(producto.getElementsByTagName("precio").item(0).getTextContent());
                String foto = producto.getElementsByTagName("foto").item(0).getTextContent();
                Producto p = new Producto(nombre, precio, foto, categoriaAttr);
                productos.add(p);
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

                // Añadir un listener para manejar el cambio de selección
                radioButton.setOnClickListener(view -> {
                    // Filtrar productos por categoría
                    List<Producto> productosFiltrados = new ArrayList<>();
                    String categoriaSeleccionada = radioButton.getText().toString().toLowerCase();

                    // Filtrar los productos por la categoría seleccionada
                    for (Producto p : productos) {
                        if (p.getCategoria().toLowerCase().contains(categoriaSeleccionada)) {
                            productosFiltrados.add(p);
                        }
                    }

                    // Mostrar productos en el Popup
                    mostrarPopup(productosFiltrados);
                });

                // Añadir el RadioButton al RadioGroup
                radioGroup.addView(radioButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarPopup(List<Producto> productos) {
        // Crear un AlertDialog con una vista personalizada
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona los productos");

        // Crear un LayoutInflater para inflar una vista personalizada
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View layoutPopup = inflater.inflate(R.layout.popup_productos, null);

        // Inicializar el contenedor del Layout (por ejemplo, un LinearLayout o ListView)
        android.widget.LinearLayout linearLayout = layoutPopup.findViewById(R.id.linearLayoutProductos);

        // Añadir cada producto a la vista del Popup
        for (Producto p : productos) {
            // Inflar el layout de cada producto
            android.view.View productoView = inflater.inflate(R.layout.item_producto, null);
            android.widget.TextView nombreProducto = productoView.findViewById(R.id.nombreProducto);
            android.widget.TextView precioProducto = productoView.findViewById(R.id.precioProducto);

            nombreProducto.setText(p.getNombre());
            precioProducto.setText(p.getPrecio() + "€");

            // Hacer que toda la línea sea clicable
            productoView.setOnClickListener(v -> {
                // Lógica cuando se haga clic en un producto
                Toast.makeText(this, "Producto seleccionado: " + p.getNombre(), Toast.LENGTH_SHORT).show();

                boolean puesto = false;
                for (Producto a : productosSeleccionados) {
                    if (Objects.equals(a.getNombre(), p.getNombre())) {
                        a.setCantidad(a.getCantidad() + 1);
                        //a.setPrecio();
                        puesto = true;
                    }
                }

                if (!puesto) {
                    productosSeleccionados.add(p);
                }
            });

            // Agregar el producto al contenedor en el layout del popup
            linearLayout.addView(productoView);

            // Crear un View como línea separadora
            android.view.View separator = new android.view.View(this);
            separator.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1))); // 1dp de altura para la línea
            separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Color gris para la línea

            // Agregar la línea separadora después de cada producto
            linearLayout.addView(separator);
        }

        // Mostrar el popup
        builder.setView(layoutPopup);
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Aquí puedes manejar la lógica para obtener los productos seleccionados
            Toast.makeText(this, "Productos seleccionados", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        // Crear y mostrar el AlertDialog
        builder.create().show();
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

    public void confirmarComanda(View view) {
        // Finaliza la actividad y regresa a la actividad anterior
        int numMesa = getIntent().getIntExtra("numeroMesa", 0);

        // Obtener los productos actuales de la comanda para la mesa
        List<ListarTags.Producto> productosExistentes = Mesas.comandas.get(numMesa);

        // Si no hay una lista aún, inicializarla
        if (productosExistentes == null) {
            productosExistentes = new ArrayList<>();
        }

        // Agregar los productos seleccionados a los existentes, actualizando cantidades si ya existen
        for (ListarTags.Producto nuevoProducto : productosSeleccionados) {
            boolean productoYaEnLista = false;

            for (ListarTags.Producto productoExistente : productosExistentes) {
                if (productoExistente.getNombre().equals(nuevoProducto.getNombre())) {
                    // Si el producto ya está en la lista, actualizar la cantidad
                    productoExistente.setCantidad(productoExistente.getCantidad() + nuevoProducto.getCantidad());
                    productoYaEnLista = true;
                    break;
                }
            }

            // Si el producto no estaba en la lista, añadirlo
            if (!productoYaEnLista) {
                productosExistentes.add(nuevoProducto);
            }
        }

        // Actualizar la lista de productos de la mesa
        Mesas.comandas.set(numMesa, productosExistentes);

        // Limpiar los productos seleccionados temporalmente
        productosSeleccionados.clear();

        // Finalizar la actividad con un resultado OK
        Intent intent = new Intent();
        setResult(RESULT_OK, intent); // Resultado OK para indicar éxito
        finish();
    }


    // Clase Producto
    public class Producto {
        private String nombre;
        private double precio;
        private String foto;
        private String categoria;
        private int cantidad;

        public Producto(String nombre, double precio, String foto, String categoria) {
            this.nombre = nombre;
            this.precio = precio;
            this.foto = foto;
            this.categoria = categoria;
            this.cantidad = 1;
        }

        public String getNombre() {
            return nombre;
        }

        public double getPrecio() {

            return precio;
        }

        public void setPrecio(double p) {
            this.precio = p;
        }

        public String getFoto() {

            return foto;
        }

        public String getCategoria() {

            return categoria;
        }

        public int getCantidad() {

            return cantidad;
        }

        public void setCantidad(int c) {

            this.cantidad = c;
        }
    }
}
