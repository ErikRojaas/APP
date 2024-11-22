package com.example.myapplication.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Mesas extends AppCompatActivity {

    public static ArrayList<List<ListarTags.Producto>> comandas = new ArrayList<>(20);

    public static int getNumMesa() {
        return numMesa + 1;
    }

    public static void setNumMesa(int numMesa) {
        Mesas.numMesa = numMesa;
    }

    public static int numMesa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taules);  // Asegúrate de tener este layout creado

        LinearLayout container = findViewById(R.id.circlesContainer);  // Contenedor para las filas

        ImageButton settingsButton = findViewById(R.id.imageButton4);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Mesas.this, ConfigActivity.class);
            startActivity(intent);
        });
        insertBase();

        for (int i = 0; i < 20; i++) {
            comandas.add(new ArrayList<>()); // Crear una lista vacía para cada mesa
        }

        // Crear los 20 círculos (mesas)
        for (int i = 0; i < 20; i++) {
            // Crear un contenedor horizontal para los círculos (mesas)
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setGravity(LinearLayout.HORIZONTAL); // Alineación central
            rowLayout.setWeightSum(2); // Dos botones por fila

            // Crear el primer cuadrado invisible
            FrameLayout squareLayout = new FrameLayout(this);
            LinearLayout.LayoutParams squareParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);  // Usamos el 50% del espacio para cada cuadrado
            squareParams.setMargins(110, 20, 20, 100);  // Márgenes entre los círculos
            squareLayout.setLayoutParams(squareParams);
            squareLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Fondo transparente (invisible)

            // Crear el círculo dentro del cuadrado
            Button circleButton = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    300,  // Ancho del círculo
                    300); // Alto del círculo, para que sea un círculo perfecto
            buttonParams.gravity = LinearLayout.HORIZONTAL;

            // Establecer las propiedades del círculo
            circleButton.setLayoutParams(buttonParams);
            circleButton.setText("Mesa " + (i + 1));
            circleButton.setTextColor(getResources().getColor(android.R.color.white));
            circleButton.setBackground(getResources().getDrawable(R.drawable.circle_shape));  // Aplicar el fondo circular
            circleButton.setTextSize(16f);  // Tamaño del texto dentro del círculo

            // Establecer el evento de clic para abrir la actividad de detalle de mesa
            int finalI = i;
            circleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crear un Intent para abrir MesaDetalleActivity
                    Intent intent = new Intent(Mesas.this, Comandas.class);
                    //Toast.makeText(Mesas.this, "Has entrado a " + circleButton.getText().toString(), Toast.LENGTH_SHORT).show();

                    // Pasar el nombre de la mesa al Intent
                    intent.putExtra("nombreMesa", circleButton.getText().toString());

                    setNumMesa(finalI);

                    // Iniciar la nueva actividad
                    startActivity(intent);
                }
            });

            // Agregar el círculo al cuadrado invisible
            squareLayout.addView(circleButton);

            // Agregar el cuadrado con el círculo al contenedor de la fila
            rowLayout.addView(squareLayout);

            // Crear el segundo cuadrado invisible si no es el último
            if (i + 1 < 20) {
                FrameLayout secondSquareLayout = new FrameLayout(this);
                LinearLayout.LayoutParams secondSquareParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);  // Usar el 50% del espacio
                secondSquareParams.setMargins(55, 20, 20, 40);  // Márgenes entre los círculos
                secondSquareLayout.setLayoutParams(secondSquareParams);
                secondSquareLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // Fondo transparente (invisible)

                // Crear el círculo dentro del cuadrado
                Button secondCircleButton = new Button(this);
                LinearLayout.LayoutParams secondButtonParams = new LinearLayout.LayoutParams(
                        300,  // Ancho del círculo
                        300); // Alto del círculo, para que sea un círculo perfecto
                secondButtonParams.gravity = LinearLayout.HORIZONTAL;

                // Establecer las propiedades del segundo círculo
                secondCircleButton.setLayoutParams(secondButtonParams);
                secondCircleButton.setText("Mesa " + (i + 2));
                secondCircleButton.setTextColor(getResources().getColor(android.R.color.white));
                secondCircleButton.setBackground(getResources().getDrawable(R.drawable.circle_shape));  // Aplicar el fondo circular
                secondCircleButton.setTextSize(16f);  // Tamaño del texto dentro del círculo

                // Establecer el evento de clic
                int finalI1 = i;
                secondCircleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Crear un Intent para abrir MesaDetalleActivity
                        Intent intent = new Intent(Mesas.this, Comandas.class);

                        //Toast.makeText(Mesas.this, "Has entrado a " + secondCircleButton.getText().toString(), Toast.LENGTH_SHORT).show();
                        // Pasar el nombre de la mesa al Intent
                        intent.putExtra("nombreMesa", secondCircleButton.getText().toString());

                        setNumMesa(finalI1 + 1);

                        // Iniciar la nueva actividad
                        startActivity(intent);
                    }
                });

                // Agregar el segundo círculo al segundo cuadrado
                secondSquareLayout.addView(secondCircleButton);

                // Agregar el segundo cuadrado con círculo al contenedor de la fila
                rowLayout.addView(secondSquareLayout);

                // Incrementar i para saltar al siguiente par
                i++;
            }

            // Agregar la fila de círculos (en cuadrados invisibles) al contenedor principal
            container.addView(rowLayout);
        }
    }

    private void insertBase(){
        new Thread(() -> {
            try {
                deleteBase();

                //?useSSL=false&logger=com.mysql.cj.log.StandardLogger&logLevel=DEBUG
                Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:33007/BarRetina", "xavierik", "X@v13r1k");

                for (int i = 0; i < 20; i++) {
                    String query = "insert into mesa (id_mesa,estado) value (?,?)";
                    int idMesa = i + 1;
                    try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        pstmt.setInt(1, idMesa);
                        pstmt.setString(2, "Libre");

                        pstmt.executeUpdate();

                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(Comandas.this, "Conectado peta el insert", Toast.LENGTH_SHORT).show();
                    }
                }
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(Comandas.this, "falla en el connect", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void deleteBase(){
        new Thread(() -> {
            try {
                //?useSSL=false&logger=com.mysql.cj.log.StandardLogger&logLevel=DEBUG
                Connection connection = DriverManager.getConnection("jdbc:mysql://10.0.2.2:33007/BarRetina", "xavierik", "X@v13r1k");
                String query = "delete from mesa";
                try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.executeUpdate();
                    connection.close();
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
}
