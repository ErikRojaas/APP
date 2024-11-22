package com.example.myapplication.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class Comandas extends AppCompatActivity {

    int numMesa = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comandes);

        // Obtener el nombre de la mesa desde el Intent
        String nombreMesa = getIntent().getStringExtra("nombreMesa");
        numMesa = getIntent().getIntExtra("numeroMesa", -1);

        // Establecer el nombre de la mesa en el TextView
        TextView nombreMesaTextView = findViewById(R.id.nombreMesa);
        if (nombreMesa != null) {
            nombreMesaTextView.setText(nombreMesa.toUpperCase());
        }

        actualizarVistaComanda();

        ImageButton novaComanda = findViewById(R.id.novaComanda);
        novaComanda.setOnClickListener(view -> {
            List<ListarTags.Producto> productosMesa = Mesas.comandas.get(numMesa);
            if (productosMesa == null || productosMesa.isEmpty()){
                Toast.makeText(Comandas.this, "No hay ninguna comanda registrada.", Toast.LENGTH_SHORT).show();
            } else {
                Mesas.comandas.get(numMesa).clear();
                actualizarVistaComanda();
            }
        });

        // Configurar el botón AÑADIR para abrir la actividad ListarTags
        Button btnAnadir = findViewById(R.id.btnAnadir);
        btnAnadir.setOnClickListener(v -> {
            Intent intent = new Intent(Comandas.this, ListarTags.class);
            intent.putExtra("numeroMesa", numMesa);
            startActivityForResult(intent, 1);
        });

        // Configurar el botón ENVIAR
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v ->
                insertBase()
                //Toast.makeText(Comandas.this, "Has clicado en enviar", Toast.LENGTH_SHORT).show()
        );

        // Configurar el botón ATRÁS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());
    }

    private void insertBase(String usu){
        new Thread(() -> {
            try {
                //?useSSL=false&logger=com.mysql.cj.log.StandardLogger&logLevel=DEBUG
                Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:33007/BarRetina", "xavierik", "X@v13r1k");
                String query = "INSERT INTO usuario (nombre) VALUES (?)";
                try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, usu);

                    pstmt.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(Comandas.this, "Conectado peta el insert", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(Comandas.this, "falla en el connect", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            actualizarVistaComanda();
        }
    }

    private void actualizarVistaComanda() {
        LinearLayout contenedor = findViewById(R.id.contenedorProductos);
        contenedor.removeAllViews();

        List<ListarTags.Producto> productosMesa = Mesas.comandas.get(numMesa);

        TextView precioFinalTextView = findViewById(R.id.precioFinal);

        if (productosMesa == null || productosMesa.isEmpty()) {
            // Mostrar mensaje de "No hay productos"
            TextView mensaje = new TextView(this);
            mensaje.setText("No hay productos seleccionados.");
            mensaje.setTextColor(getResources().getColor(android.R.color.white));
            mensaje.setTextSize(18);
            mensaje.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            contenedor.addView(mensaje);

            // Ocultar el precio total
            precioFinalTextView.setText("Precio Total: 0.0€");
        } else {
            double precioTotal = 0;
            for (ListarTags.Producto producto : productosMesa) {
                precioTotal += producto.getPrecio() * producto.getCantidad();
            }

            // Mostrar el precio total
            precioFinalTextView.setText("Precio Total: " + String.format("%.1f", precioTotal) + "€");

            for (ListarTags.Producto producto : productosMesa) {
                LinearLayout productoLayout = new LinearLayout(this);
                productoLayout.setOrientation(LinearLayout.HORIZONTAL);
                productoLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

                // Cantidad del producto
                TextView cantidadProducto = new TextView(this);
                cantidadProducto.setText("x" + producto.getCantidad());
                cantidadProducto.setTextColor(getResources().getColor(android.R.color.white));
                cantidadProducto.setTextSize(16);
                cantidadProducto.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                // Nombre del producto
                TextView nombreProducto = new TextView(this);
                nombreProducto.setText(producto.getNombre());
                nombreProducto.setTextColor(getResources().getColor(android.R.color.white));
                nombreProducto.setTextSize(16);
                nombreProducto.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

                // Precio del producto
                TextView precioProducto = new TextView(this);
                precioProducto.setText(String.format("%.1f€", producto.getPrecio()));
                precioProducto.setTextColor(getResources().getColor(android.R.color.white));
                precioProducto.setTextSize(16);
                precioProducto.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                // Botón "menos"
                Button btnMenos = new Button(this);
                btnMenos.setText("-");
                btnMenos.setTextSize(14);
                btnMenos.setTextColor(getResources().getColor(android.R.color.white)); // Color blanco para el texto
                btnMenos.setBackground(null); // Sin fondo
                btnMenos.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

                // Sombra del botón
                btnMenos.setElevation(dpToPx(4)); // Elevación (asegúrate de que funcione con ViewCompat)

                // Alternativa para dispositivos sin sombra visible (compatibilidad)
                ViewCompat.setElevation(btnMenos, dpToPx(4));

                // Configuración de tamaño
                LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(
                        dpToPx(32), // Ancho
                        dpToPx(32)  // Alto
                );
                btnLayoutParams.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4)); // Margen opcional
                btnMenos.setLayoutParams(btnLayoutParams);

                // Acción del botón
                btnMenos.setOnClickListener(v -> {
                    if (producto.getCantidad() > 1) {
                        producto.setCantidad(producto.getCantidad() - 1); // Restar 1 a la cantidad
                    } else {
                        productosMesa.remove(producto); // Eliminar si la cantidad es 1
                    }
                    actualizarVistaComanda(); // Actualizar la vista
                });

                // Añadir elementos al layout del producto
                productoLayout.addView(cantidadProducto);
                productoLayout.addView(nombreProducto);
                productoLayout.addView(precioProducto);
                productoLayout.addView(btnMenos);

                // Margen inferior entre productos
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) productoLayout.getLayoutParams();
                if (params != null) {
                    params.setMargins(0, 0, 0, dpToPx(8));
                    productoLayout.setLayoutParams(params);
                }

                contenedor.addView(productoLayout);
            }
        }
    }


    private int dpToPx(int sp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (sp * density + 0.5f);
    }
}
