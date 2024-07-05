package com.example.assignment2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    // we need to stored the to do list items in the array list
    // because we dont know how many task list item the user can add
    // its better we used arraylist rather than array
    // we used an array adapter here as a middlemen to connect the arraylist
    // with the listview
    // arraylist --> array adapter --> listview
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter arrayAdapter;


    // create a menu bar for the apps
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();  // pop up menu
        menuInflater.inflate(R.menu.main_menu, menu); // get menu from xml
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        return super.onCreateOptionsMenu(menu);  // return menu object
    }


    // what happen if the user select the menu item - add button
    // when the user select the menu item add in the menu bar
    // create an intent object, we used an explicit intent here
    // bring the user to another page or activity
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_note) {
            Intent intent = new Intent(getApplicationContext(), note_editor.class);  // open the link and page
            startActivity(intent);

            return true;  // the user select add menu item
        }
        // If user select About Us Page
        if (item.getItemId() == R.id.about_us) {
            Intent intent = new Intent(getApplicationContext(), AboutUs.class);  // open the link and page
            startActivity(intent);

            return true;  // the user select add menu item
        }
        return false;  // the user did not select the menu item
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get the list view object from xml files
        ListView listView = (ListView) findViewById(R.id.listView);

        // setup the shared preferences object in this app
        // for private use only, the SP aka file can be accessed by this app only
        SharedPreferences sharedPreferences =
                getApplicationContext().getSharedPreferences
                        ("com.example.assignment2", Context.MODE_PRIVATE);

        // the shared preferences object cannot work directly with the arraylist
        // this app will stored all the user input into array list first
        // then convert the arraylist into hash set before we can save
        // the user input into SP
        // user input --> array list --> hash set --> shared preferences
        // array list cannot --> shared preferences, need hash set as middlemen
        HashSet<String> set = (HashSet<String>)
                sharedPreferences.getStringSet("notes", null);
        //Log.i("test", set.toString());

        if (set == null) {  // if the set is empty no user input yet
            notes.add("Example note");
        } else {
            notes = new ArrayList(set);  // display the user input
        }  // end if

        // use an array adapter object to attach the notes arraylist to the
        // list view and display the notes array list details in the list view
        // notes array list --> array adapter --> list view
        // list view cannot read data directly from arraylist , need array adapter
        arrayAdapter = new ArrayAdapter
                (this, R.layout.list_item, R.id.textView, notes);

        listView.setAdapter(arrayAdapter);

        // what happen if the user click the items in the listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // override the abstract method
            // use an intent object to bring the user to another page
            // send the intent object to another page
            // the intent object contains noteID to let the app know
            // which task the user wish to edit
            // which record in the file the user wish to edit here
            public void onItemClick
            (AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),
                        note_editor.class);
                intent.putExtra("noteId", i);
                startActivity(intent);


            }

        });

        // when the user long press click the item on the list view
        // delete the task
        listView.setOnItemLongClickListener
                (new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick
                            (AdapterView<?> adapterView, View view, int i, long l) {

                        final int itemToDelete = i;  // which item the user wish to delete

                        // create a new alert dialog box
                        // if the user click yes button
                        // if the the user click no button
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Are you sure?")
                                .setMessage("Do you want to delete this note?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                notes.remove(itemToDelete);
                                                // remove items on the notes array list
                                                arrayAdapter.notifyDataSetChanged();
                                                // update the listview via array adapter

                                                SharedPreferences sharedPreferences =
                                                        getApplicationContext().getSharedPreferences
                                                                ("com.example.assignment2",
                                                                        Context.MODE_PRIVATE);  // open SP or file

                                                HashSet<String> set = new HashSet(MainActivity.notes);
                                                // convert array list to hash set

                                                sharedPreferences.edit().putStringSet("notes", set).apply();
                                                // open the SP, put new data into SP, save all changes
                                                // you can edit or delete one item at one time only

                                            }
                                        }
                                )

                                .setNegativeButton("No", null)
                                .show();


                        return true;
                    }


                });
    }
}