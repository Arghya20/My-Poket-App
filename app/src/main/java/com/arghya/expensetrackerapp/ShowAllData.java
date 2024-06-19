package com.arghya.expensetrackerapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ShowAllData extends AppCompatActivity {

    TextView showAllDataTitle;
    ListView listView;
    DatabaseHelper dbHelper;

    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;

    public static boolean Expense = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_all_data);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        showAllDataTitle = findViewById(R.id.showAllDataTitle);
        listView = findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(this);


        if (Expense) {
            showAllDataTitle.setText("Expense Overview");
        } else {
            showAllDataTitle.setText("Income Overview");

        }

        loadData();
    }

    public void loadData() {
        Cursor cursor = null;

        if (Expense) {
            cursor = dbHelper.getAllExpenseData();
        } else {
            cursor = dbHelper.getAllIncomeData();
        }

        if (cursor != null && cursor.getCount() > 0) {
            arrayList = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String reason = cursor.getString(2);
                double time = cursor.getDouble(3);

                hashMap = new HashMap<>();
                hashMap.put("id", "" + id);
                hashMap.put("amount", "" + Math.round(amount));
                hashMap.put("reason", "" + reason);
                hashMap.put("time", "" + time);
                arrayList.add(hashMap);
            }

            MyAdapter myAdapter = new MyAdapter();
            listView.setAdapter(myAdapter);

        } else {
            showAllDataTitle.setText("No Data Found");
        }


    }


    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            View view;

            if (convertView == null) {
                if (Expense) {
                    view = inflater.inflate(R.layout.expense_card_ui, null);
                } else {
                    view = inflater.inflate(R.layout.income_card_ui, null);
                }
            } else {
                view = convertView;
            }


            TextView spendingTitle, spendingAmount, timeAndDate;
            LinearLayout btnDelete;

            spendingTitle = view.findViewById(R.id.spendingTitle);
            spendingAmount = view.findViewById(R.id.spendingAmount);
            timeAndDate = view.findViewById(R.id.timeAndDate);
            btnDelete = view.findViewById(R.id.btnDelete);

            hashMap = arrayList.get(position);
            String id = hashMap.get("id");
            String amount = hashMap.get("amount");
            String reason = hashMap.get("reason");
            double time = Double.parseDouble(hashMap.get("time"));


            spendingTitle.setText(reason);
            if (Expense) {
                spendingAmount.setText("-" + amount);

            } else {
                spendingAmount.setText("+" + amount);
            }

            // Convert timestamp to human-readable date and time
            Date date = new Date((long) time);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy , hh:mm a", Locale.getDefault());
            String formattedDate = sdf.format(date);

            // Display formatted date and time
            timeAndDate.setText(formattedDate);

            // Delete Item

            // Delete Item with confirmation dialog
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog to confirm deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowAllData.this);
                    builder.setTitle("Delete Confirmation")
                            .setMessage("Are you sure you want to delete this record?")
                            .setNegativeButton("Cancel", null);

                    // Set the positive button with red text
                    builder.setPositiveButton("Delete", null);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            // Get the delete button and set its text color to red
                            Button deleteButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            deleteButton.setTextColor(Color.RED);
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Perform deletion
                                    if (Expense) {
                                        dbHelper.deleteExpense(id);
                                    } else {
                                        dbHelper.deleteIncome(id);
                                    }
                                    // Refresh data
                                    loadData();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });

            return view;

        }
    }


}

//app v 1.1 completed