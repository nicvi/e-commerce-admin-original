package sdcn.project.ecommerce_admin_original;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import sdcn.project.ecommerce_admin_original.R;

import java.util.Objects;

public class BillSelectorActivity extends AppCompatActivity {

    // [____________|| Viewers. ||____________] [<--BEGIN]
    Button button_pickedUpBills;
    Button button_noPickedUpBills;
    // [____________|| Viewers. ||____________] [END---->]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_selector);

        /// views
        button_pickedUpBills = findViewById(R.id.button_pickedUpBills);
        button_noPickedUpBills = findViewById(R.id.button_noPickedUpBills);

        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_bill_selector);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]

        // views actions
        button_pickedUpBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle_pickedUpBills = new Bundle();
                bundle_pickedUpBills.putBoolean("pickedUpBills", true);

                Intent intent;
                intent = new Intent(BillSelectorActivity.this, FacturaActivity.class);
                intent.putExtra("intentBundle", bundle_pickedUpBills);
                startActivity(intent);
            }
        });

        button_noPickedUpBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle_noPickedUpBills = new Bundle();
                bundle_noPickedUpBills.putBoolean("pickedUpBills", false);

                Intent intent;
                intent = new Intent(BillSelectorActivity.this, FacturaActivity.class);
                intent.putExtra("intentBundle", bundle_noPickedUpBills);
                startActivity(intent);
            }
        });
    }

    // [BEGIN-->] <==========|| Toolbar / Action Bar override methods ||==========>
    // inflate the app bar with the toolbar wanted model
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // R.menu.menu is a reference to an xml file named menu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // set actions to the app bar buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
    // <==========|| Toolbar / Action Bar override methods ||==========> [<-- END]
}