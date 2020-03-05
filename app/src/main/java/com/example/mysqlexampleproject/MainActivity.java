package com.example.mysqlexampleproject;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ItemAdapter itemAdapter;
    Context thisContext;
    ListView myListView;
    TextView progressTextView;
    Map<String, Double> fruitsMap = new LinkedHashMap<String, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        myListView = findViewById(R.id.myListView);
        progressTextView = findViewById(R.id.progressTextView);
        thisContext = this;

        // Clear out the progressTextView
        progressTextView.setText("");

        // getDataButton setup
        Button btn = findViewById(R.id.getDataButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });

    }

    private class GetData extends AsyncTask<String, String, String> {

        String msg = "";

        // JDBC driver name and database url
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String URL = "jdbc:mysql://10.0.0.238:3306/example";
        static final String USER = "bret";
        static final String PASSWORD = "sept0211";

        @Override
        protected void onPreExecute() {
            progressTextView.setText("Connecting to Database...");
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;

            try {

                Class.forName(JDBC_DRIVER);

                // Connect to Database
                conn = DriverManager.getConnection(URL, USER, PASSWORD);

                // Statement setup
                stmt = conn.createStatement();
                String sql = "SELECT * FROM fruits";

                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    System.out.println(rs.getString("name") + " , " + rs.getString("price"));
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    fruitsMap.put(name, price);
                }

                msg = "Process complete.";

                // Close open resources
                rs.close();
                stmt.close();
                conn.close();

            } catch (SQLException | ClassNotFoundException connError) {

                msg = "An exception was thrown for JDBC.";
                connError.printStackTrace();

            } finally {

                // Close stmt if left open
                try {

                    if (stmt != null)
                        stmt.close();

                } catch (SQLException e) {

                    e.printStackTrace();

                }

                // Close conn if left open
                try {

                    if (conn != null)
                        conn.close();

                } catch (SQLException e) {

                    e.printStackTrace();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String msg) {

            progressTextView.setText(this.msg);

            if (fruitsMap.size() > 0) {
                itemAdapter = new ItemAdapter(thisContext, fruitsMap);
                myListView.setAdapter(itemAdapter);
            }
        }
    }

} // End of MainActivity
