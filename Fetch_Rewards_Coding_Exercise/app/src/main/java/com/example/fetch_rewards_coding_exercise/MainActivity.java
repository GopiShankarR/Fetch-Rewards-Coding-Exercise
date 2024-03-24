package com.example.fetch_rewards_coding_exercise;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private static class Item implements Comparable<Item>{
        public final int id;
        public final int listId;
        public final String name;

        public Item(int id, int listId, String name) {
            this.id = id;
            this.listId = listId;
            this.name = name;
        }
        public int compareTo(Item i)
        {
            return Integer.compare(this.id, i.id);
        }

        public String toString()
        {
            return "Item{"
                    + "id=" + id + ", listId='" + listId + '\''
                    + ", name=" + name + '}';
        }
    }

    private TableLayout myTable;
    private RequestQueue myQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        myTable = findViewById(R.id.tableLayout);
        myQueue = Volley.newRequestQueue(this);

        getJSON();
    }

    public void getJSON() {
        getRequest();
    }

    private void getRequest() {
        String url = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response    .Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // 1. Convert JSON data to a List of Items, filtering null names
                            ArrayList<Item> items = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                int id = jsonObject.getInt("id");
                                int listId = jsonObject.getInt("listId");
                                String name = jsonObject.optString("name", null);
                                if (name != null) { // Filter null names
                                    items.add(new Item(id, listId, name));
                                }
                            }


                            // 2. Group items by listId and sort them within each group
                            Map<Integer, List<Item>> itemsByListId = new HashMap<>(); // Use TreeMap for sorting by listId
                            for (Item item : items) {
                                itemsByListId.computeIfAbsent(item.listId, key -> new ArrayList<>()).add(item);
                            }
                            System.out.println(itemsByListId);
                            // 3. Add header row and data rows
                            addHeaderRowToTableLayout();
                            for (Map.Entry<Integer, List<Item>> entry : itemsByListId.entrySet()) {
                                int listId = entry.getKey();
                                List<Item> itemsForListId = entry.getValue();
                                Collections.sort(itemsForListId);
                                for (Item item : itemsForListId) {
                                    if(!item.name.isEmpty() && !item.name.equals("null"))
                                        addRowToTableLayout(item.id, item.listId, item.name);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                      }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        myQueue.add(request);
    }

    @SuppressLint("SetTextI18n")
    private void addHeaderRowToTableLayout() {
        TableRow row = new TableRow(this);

        // Create text views for column names
        TextView idHeaderTextView = new TextView(this);
        idHeaderTextView.setText("ID");
        idHeaderTextView.setGravity(Gravity.CENTER);
        idHeaderTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Equal weight for each column
        idHeaderTextView.setPadding(8, 8, 8, 8);
        idHeaderTextView.setTypeface(null, Typeface.BOLD);

        TextView listIdHeaderTextView = new TextView(this);
        listIdHeaderTextView.setText("ListID");
        listIdHeaderTextView.setGravity(Gravity.CENTER);
        listIdHeaderTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Equal weight for each column
        listIdHeaderTextView.setPadding(8, 8, 8, 8);
        listIdHeaderTextView.setTypeface(null, Typeface.BOLD);

        TextView nameHeaderTextView = new TextView(this);
        nameHeaderTextView.setText("Name");
        nameHeaderTextView.setGravity(Gravity.CENTER);
        nameHeaderTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Equal weight for each column
        nameHeaderTextView.setPadding(8, 8, 8, 8);
        nameHeaderTextView.setTypeface(null, Typeface.BOLD);

        // Add text views to the header row
        row.addView(idHeaderTextView);
        row.addView(listIdHeaderTextView);
        row.addView(nameHeaderTextView);

        // Add the header row to the table layout
        myTable.addView(row);
    }

    private void addRowToTableLayout(int id, int listId, String name) {
        // Create a new row
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        // Create text views for each column and set their values
        TextView idTextView = new TextView(this);
        idTextView.setText(id != 0 ? String.valueOf(id) : "-");
        idTextView.setGravity(Gravity.CENTER);
        idTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Equal weight for each column
        idTextView.setPadding(8, 8, 8, 8); // Padding for spacing

        TextView listIdTextView = new TextView(this);
        listIdTextView.setText(listId != 0 ? String.valueOf(listId) : "-");
        listIdTextView.setGravity(Gravity.CENTER);
        listIdTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Equal weight for each column
        listIdTextView.setPadding(8, 8, 8, 8); // Padding for spacing

        TextView nameTextView = new TextView(this);
        nameTextView.setText(!name.isEmpty() ? name : "-");
        nameTextView.setGravity(Gravity.CENTER);
        nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1)); // Equal weight for each column
        nameTextView.setPadding(8, 8, 8, 8); // Padding for spacing

        // Add text views to the row
        row.addView(idTextView);
        row.addView(listIdTextView);
        row.addView(nameTextView);

        // Add the row to the table layout
        myTable.addView(row);
    }
}