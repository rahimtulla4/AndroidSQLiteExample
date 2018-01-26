package com.inducesmile.androidsqliteexample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inducesmile.androidsqliteexample.adapter.ProductAdapter;
import com.inducesmile.androidsqliteexample.database.SqliteDatabase;

import java.util.List;

import static android.view.inputmethod.EditorInfo.*;
import static com.inducesmile.androidsqliteexample.R.id.activity_to_do;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = MainActivity.class.getSimpleName();

    private SqliteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout fLayout = (FrameLayout) findViewById(activity_to_do);

        RecyclerView productView = (RecyclerView)findViewById(R.id.product_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        productView.setLayoutManager(linearLayoutManager);
        productView.setHasFixedSize(true);

        mDatabase = new SqliteDatabase(this);
        List<Product> allProducts = mDatabase.listProducts();

        if(allProducts.size() > 0){
            productView.setVisibility(View.VISIBLE);
            ProductAdapter mAdapter = new ProductAdapter(this, allProducts);
            productView.setAdapter(mAdapter);

        }else {
            productView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no product in the database. Start adding now", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add new quick task
                addTaskDialog();
            }
        });
    }

    private void addTaskDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_product_layout, null);

        final EditText nameField = (EditText)subView.findViewById(R.id.enter_name);
        final EditText quantityField = (EditText)subView.findViewById(R.id.enter_quantity);
        quantityField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_NULL)
                {
                }
                return false;
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new product");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD PRODUCT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final int quantity = Integer.parseInt(quantityField.getText().toString());

                if(TextUtils.isEmpty(name) || quantity <= 0){
                    Toast.makeText(MainActivity.this, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                }
                else{
                    Product newProduct = new Product(name, quantity);
                    mDatabase.addProduct(newProduct);

                    //refresh the activity
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(mDatabase != null)
        {
            mDatabase.close();
        }
    }
}
