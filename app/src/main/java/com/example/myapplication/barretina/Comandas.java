package com.example.myapplication.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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
                Toast.makeText(Comandas.this, "Has clicado en enviar", Toast.LENGTH_SHORT).show()
        );

        // Configurar el botón ATRÁS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());
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

        if (productosMesa == null || productosMesa.isEmpty()) {
            TextView mensaje = new TextView(this);
            mensaje.setText("No hay productos seleccionados.");
            mensaje.setTextColor(getResources().getColor(android.R.color.white));
            mensaje.setTextSize(18);
            mensaje.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            contenedor.addView(mensaje);
        } else {
            double precioTotal = 0;
            for (ListarTags.Producto producto : productosMesa) {
                precioTotal += producto.getPrecio() * producto.getCantidad();
            }

            TextView precioFinalTextView = findViewById(R.id.precioFinal);
            precioFinalTextView.setText("Precio Total: " + String.format("%.2f", precioTotal) + "€");

            for (ListarTags.Producto producto : productosMesa) {
                LinearLayout productoLayout = new LinearLayout(this);
                productoLayout.setOrientation(LinearLayout.HORIZONTAL);
                productoLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

                TextView cantidadProducto = new TextView(this);
                cantidadProducto.setText("x" + producto.getCantidad());
                cantidadProducto.setTextColor(getResources().getColor(android.R.color.white));
                cantidadProducto.setTextSize(16);
                cantidadProducto.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                TextView nombreProducto = new TextView(this);
                nombreProducto.setText(producto.getNombre());
                nombreProducto.setTextColor(getResources().getColor(android.R.color.white));
                nombreProducto.setTextSize(16);
                nombreProducto.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

                TextView precioProducto = new TextView(this);
                precioProducto.setText(String.format("%.2f€", producto.getPrecio()));
                precioProducto.setTextColor(getResources().getColor(android.R.color.white));
                precioProducto.setTextSize(16);
                precioProducto.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                productoLayout.addView(cantidadProducto);
                productoLayout.addView(nombreProducto);
                productoLayout.addView(precioProducto);

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
