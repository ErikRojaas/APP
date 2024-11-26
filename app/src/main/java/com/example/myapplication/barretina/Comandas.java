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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class Comandas extends AppCompatActivity {

    boolean subida = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comandes);

        // Obtener el nombre de la mesa desde el Intent
        String nombreMesa = getIntent().getStringExtra("nombreMesa");

        // Establecer el nombre de la mesa en el TextView
        TextView nombreMesaTextView = findViewById(R.id.nombreMesa);
        if (nombreMesa != null) {
            nombreMesaTextView.setText(nombreMesa.toUpperCase());
        }

        actualizarVistaComanda();

        ImageButton novaComanda = findViewById(R.id.novaComanda);
        novaComanda.setOnClickListener(view -> {
            List<ListarTags.Producto> productosMesa = Mesas.comandas.get(Mesas.getNumMesa());
            if (productosMesa == null || productosMesa.isEmpty()) {
                Toast.makeText(Comandas.this, "No hay ninguna comanda registrada.", Toast.LENGTH_SHORT).show();
            } else {
                Mesas.comandas.get(Mesas.getNumMesa()).clear();
                actualizarVistaComanda();
            }
            subida = true;
        });

        // Configurar el botón AÑADIR para abrir la actividad ListarTags
        Button btnAnadir = findViewById(R.id.btnAnadir);
        btnAnadir.setOnClickListener(v -> {
            Intent intent = new Intent(Comandas.this, ListarTags.class);
            intent.putExtra("numeroMesa", Mesas.getNumMesa());
            startActivityForResult(intent, 1);
        });

        // Configurar el botón ENVIAR
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(v -> {
            if (subida) {
                StringBuilder cadena = new StringBuilder();
                for (ListarTags.Producto p : Mesas.comandas.get(Mesas.getNumMesa())) {
                    if (p.getCantidad() > 1) {
                        // Calcula el precio unitario
                        double precioUnitario = p.getPrecio() / p.getCantidad();
                        for (int i = 0; i < p.getCantidad(); i++) {
                            cadena.append(p.getNombre()).append(":").append(1).append(":pedido:").append(precioUnitario).append(",");
                        }
                    } else {
                        cadena.append(p.getNombre()).append(":").append(p.getCantidad()).append(":pedido:").append(p.getPrecio()).append(",");
                    }
                }
                if (cadena.length() > 0) {
                    cadena.setLength(cadena.length() - 1); // Eliminar la última coma
                }
                insertBase(Mesas.getNumMesa(), cadena.toString());
                subida = false;
            } else {
                StringBuilder cadena = new StringBuilder();
                for (ListarTags.Producto p : Mesas.comandas.get(Mesas.getNumMesa())) {
                    cadena.append(p.toString()).append(",");
                }
                if (cadena.length() > 0) {
                    cadena.setLength(cadena.length() - 1); // Eliminar la última coma
                }
                updateBase(Mesas.getNumMesa(), cadena.toString());
            }
        });

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

        List<ListarTags.Producto> productosMesa = Mesas.comandas.get(Mesas.getNumMesa());

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

    private void updateBase(int numMesa, String comanda){
        new Thread(() -> {
            try {
                //?useSSL=false&logger=com.mysql.cj.log.StandardLogger&logLevel=DEBUG
                Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:33007/BarRetina", "xavierik", "X@v13r1k");
                String query = "update comanda set comanda = ? where id_mesa = ? order by id_comanda desc limit 1";

                try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, comanda);
                    pstmt.setInt(2, numMesa);

                    pstmt.executeUpdate();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void insertBase(int numMesa, String comanda){
        new Thread(() -> {
            try {
                //?useSSL=false&logger=com.mysql.cj.log.StandardLogger&logLevel=DEBUG
                Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:33007/BarRetina", "xavierik", "X@v13r1k");



                String query = "insert into comanda (id_comanda,id_user,id_mesa,comanda,estado,fecha_comanda) values (?,?,?,?,?,current_time())";

                String queryId = "select count(*) from comanda";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(queryId);
                int id = 0;
                while (rs.next()) {
                    id = rs.getInt("count(*)") + 1;
                }

                int userId = MainActivity.getUserId();

                try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, id);
                    pstmt.setInt(2, userId);
                    pstmt.setInt(3, numMesa);
                    pstmt.setString(4, comanda);
                    pstmt.setString(5, "pedido");

                    pstmt.executeUpdate();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
