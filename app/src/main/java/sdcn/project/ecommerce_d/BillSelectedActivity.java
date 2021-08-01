package sdcn.project.ecommerce_d;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sdcn.project.ecommerce_d.R;
import com.google.android.gms.maps.model.LatLng;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class BillSelectedActivity extends AppCompatActivity
{
    // progress bar
    private ProgressDialog progressBar;
    private int progressBarStatus=0;

    // variables
    private String billId;
    private boolean fromPickedUpMenu;

    // [____________|| Firestore ||____________] [<--BEGIN]
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Bill object
    private Bill bill;
    private User user;

    // views
    private TextView textView_billPickedUp,
            textView_billTotalProducts,
            textView_billPurchaseMethod,
            textView_billHour,
            textView_billDate,
            textView_billTotalPrice,
            textView_billName,
            textView_billUserName,
            textView_billUserEmail,
            textView_billUserPhone,
            textView_billUserLocation;

    private RecyclerView recyclerView_selectedBill;

    private Button button_deleteSelectedBill,
            button_updateSelectedBill,
            button_loadUserLocation;

    private RadioGroup radioGroup_markAsPickedUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_selected);

        //If i get the product from the "selectedProduct" activity then ill store it in a Bundle var
        Bundle b = getIntent().getBundleExtra("bundleBill");

        bill =b.getParcelable("optionsBill");
        user = b.getParcelable("bundleUser");

        // init views
        textView_billName = findViewById(R.id.textView_billName);
        textView_billTotalPrice = findViewById(R.id.textView_billTotalPrice);
        textView_billDate = findViewById(R.id.textView_billDate);
        textView_billHour = findViewById(R.id.textView_billHour);
        textView_billPurchaseMethod = findViewById(R.id.textView_billPurchaseMethod);
        textView_billTotalProducts = findViewById(R.id.textView_billTotalProducts);
        textView_billPickedUp = findViewById(R.id.textView_billPickedUp);
        radioGroup_markAsPickedUp= findViewById(R.id.radioGroup_markAsPickedUp);

        textView_billUserName = findViewById(R.id.textView_billUserName);
        textView_billUserEmail = findViewById(R.id.textView_billUserEmail);
        textView_billUserPhone = findViewById(R.id.textView_billUserPhone);
        textView_billUserLocation = findViewById(R.id.textView_billUserLocation);

        recyclerView_selectedBill =(RecyclerView) findViewById(R.id.recyclerView_selectedBill);
        button_deleteSelectedBill = findViewById(R.id.button_deleteSelectedBill);
        button_updateSelectedBill = findViewById(R.id.button_updateSelectedBill);
        button_loadUserLocation = findViewById(R.id.button_loadUserLocation);

        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_bill_selected);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]

        //setViews
        if (bill!= null && user!=null)
        {
            fromPickedUpMenu = bill.isPickedUp();

            try
            {
                setViews();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            setSelectedBillAdapter();

            button_deleteSelectedBill.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    deleteSelectedBill(true);
                }
            });

            button_updateSelectedBill.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    upgradeBill();
                }
            });


        }
        else
        {
            Toast.makeText(this, "Error al cargar producto", Toast.LENGTH_LONG).show();
        }
    }

    public void setViews() throws IOException
    {
        textView_billName.setText(bill.getBillName());
        // bill price
        String billPrice = "$ " + bill.getBillPrice();
        textView_billTotalPrice.setText(billPrice);

        // is picked Up
        String pickedUp="";
        if (bill.isPickedUp())
        {
            pickedUp = "Si";
        }
        else
        {
            pickedUp = "No";
            textView_billPickedUp.setTextColor(Color.parseColor("#981C1C"));
        }
        textView_billPickedUp.setText(pickedUp);

        // date
        String[] dateTimeArray = bill.getBillDate().toString().split(" ");

        String month = dateTimeArray[1];
        String day = dateTimeArray[2];
        String time = dateTimeArray[3];
        String year = dateTimeArray[dateTimeArray.length-1];

        switch(month.toLowerCase())
        {
            case "jan":
                month= "Enero";
                break;
            case "apr":
                month = "Abr";
                break;
            case "feb":
                month = "Febrero";
                break;
            default:
                break;
        }

        String date = month + " " +day + ", " +year  ;

        textView_billDate.setText(date);
        textView_billHour.setText(time);

        // other views
        textView_billPurchaseMethod.setText(bill.getPurchasedMethod());

        // total products value
        textView_billTotalProducts.setText(String.valueOf(getTotalProducts()));

        // mark pickedUp value
        if (bill.isPickedUp())
        {
            radioGroup_markAsPickedUp.check(R.id.radioButton_pickedUP);
        }

        radioGroup_markAsPickedUp.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton =  group.findViewById(checkedId);
                        bill.setPickedUp(radioButton.getText().toString().equals("Si"));
                    }
                }
        );

        // user info views
        String strUserName = user.getFirstName() + " " + user.getLastName();
        textView_billUserName.setText(strUserName);
        textView_billUserEmail.setText(user.getEmail());
        textView_billUserPhone.setText(user.getPhone());

        // put latitude
        String userLocation = "No hay ubicación disponible.";

        // check if there are a location
        if (user.getLatitude()!=0 && user.getLongitude()!=0)
        {
            // if there a user location then get the address
            userLocation = addressGiver( new LatLng( user.getLatitude(), user.getLongitude()) );
            textView_billUserLocation.setText(userLocation);

            // open location in google maps
            button_loadUserLocation.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // open the location
                    System.out.println("on button_loadUserLocation");
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", user.getLatitude(), user.getLongitude());
                    //https://www.google.com/maps/search/?api=1
                    uri = "https://www.google.com/maps/search/?api=1" + "&query=" + user.getLatitude() +"," + user.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            });

        }
        else
        {
            textView_billUserLocation.setText(userLocation);
            button_loadUserLocation.setEnabled(false);
        }
    }

    public int getTotalProducts()
    {
        int totalQuantityProducts = 0;


        for (int quantity : bill.getCompra().getPurchaseQuantityArray())
        {
            totalQuantityProducts += quantity;
        }

        return totalQuantityProducts;
    }

    public void setSelectedBillAdapter()
    {
        // create an CaptionSelectedBillAdapter object and then i'll pass the arrays with the
        // products information that i want yo show in  the RecyclerView.
        // The CaptionImagesAdapter only receive as arguments arrays with the elements to be
        // displayed.
        CaptionSelectedBillAdapter billSelectedAdapter = new CaptionSelectedBillAdapter(
                bill.getCompra().getPurchaseProductArray(),
                bill.getCompra().getPurchaseQuantityArray()
        );

        recyclerView_selectedBill.setAdapter(billSelectedAdapter);
        // LinearLayoutManager is used to show the cardView in a list way
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_selectedBill.setLayoutManager(layoutManager);
    }

    public void deleteSelectedBill(boolean delete)
    {

                deleteBillFromFiresStore(delete);
                // go back to the activity_factura

                Bundle bundle_pickedUpBills = new Bundle();
                bundle_pickedUpBills.putBoolean("pickedUpBills", fromPickedUpMenu);

                Intent billIntent;
                billIntent = new Intent(BillSelectedActivity.this, FacturaActivity.class);
                billIntent.putExtra("billDeleted", "BillSelected");
                billIntent.putExtra("intentBundle", bundle_pickedUpBills);
                startActivity(billIntent);

    }


    public void deleteBillFromFiresStore(boolean delete)
    {
        String strDoc = "";
        if (fromPickedUpMenu)
        {
            strDoc = "Recogido";
        }
        else
        {
            strDoc = "NoRecogido";
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Facturas")
                .document("9JLKeuCuSqH9ZUzDh61X")
                .collection(strDoc)
                .document(bill.getBillID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // finidh the progress bar
                        progressBarStatus+=1;
                        if (delete)
                        {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Se elimino factura: " + bill.getBillName(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                        else
                        {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Se actualizo valor",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBarStatus+=1;
                        Log.w("onFailure", "Error deleting document", e);
                    }
                });
    }

    private void upgradeBill()
    {

        // upgrade picked up Bill field in fireStore
        System.out.println("upgradeBill");

        upgradeBillInFireStore();
    }

    private void upgradeBillInFireStore()
    {

        // if i choose to change the value of pickedUp to TRUE
        // and it wasn't True, then i can change the value
        if (bill.isPickedUp() && !fromPickedUpMenu)
        {
            launchUpgradeBillProgressBar(button_updateSelectedBill);

            System.out.println("upgradeBillInFireStore");


            progressBarStatus = 0;

            // Update the pickedUp field it in the Usuarios Collection
            db.collection("Usuarios")
                    .document(bill.getUserID())
                    .collection("Compras")
                    .document(bill.getBillID())
                    .update("pickedUp", bill.isPickedUp() )
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            System.out.println("bill updated to Compra");

                            // call a method to move the bill to Recogido collection
                            moveToRecogidoCollection();

                            // almost finish the progress bar
                            progressBarStatus=98;

                            Log.d(TAG,"bill updated in Usuarios/Compras collection" );
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("bill NOT updated to Compra");
                            progressBarStatus=100;
                            Log.w(TAG, "Error adding document", e);

                            Toast.makeText(BillSelectedActivity.this,
                                    "Error al actualizar en el usuario",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        }
        else
        {
            Toast.makeText(this,
                    "No hay cambio que realizar",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void moveToRecogidoCollection()
    {
        // set the bill in the Recogido collection
        db.collection("Facturas")
                .document("9JLKeuCuSqH9ZUzDh61X")
                .collection("Recogido")
                .document(bill.getBillID())
                .set(bill)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // keep updating the progress bar
                        progressBarStatus+=1;

                        // call a method to delete the bill from NoRecogido collection
                        deleteSelectedBill(false);

                        System.out.println("bill added to Recogido");
                        Log.d(TAG,"bill written on Bills with ID: " + bill.getBillID());
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        System.out.println("bill not added to Recogido");
                        progressBarStatus=100;
                        Log.w(TAG, "Error adding document", e);

                        Toast.makeText(BillSelectedActivity.this,
                                "Error al actualizar valores en admin app",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });


        // delete the bill from the current NoRecogido Collection
    }


    public void launchUpgradeBillProgressBar(View v)
    {
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setMessage("Realizando cambios...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // call the method that will be waited to finish it


                    // TODO why do i call Thread.sleep(1000), it is because this was the original
                    //  format of the code that i findon internet?
                    /*
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                     */

                    /*
                    progressBarbHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });

                     */
                }

                if (progressBarStatus >= 100) {
                    /*
                    try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                     */
                    progressBar.dismiss();
                }
            }
        }).start();
    }


    // <==========|| AddressGiver method ||==========> [BEGIN]
    // method to get the address from latitude, longitude

    public String addressGiver(LatLng mMarker) throws IOException
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(mMarker.latitude, mMarker.longitude, 3); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        return addresses.get(0).getAddressLine(0);
    }
    // <==========|| AddressGiver method ||==========> [END]






    // [BEGIN-->] <==========|| Action Bar override methods ||==========>
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
    // <==========|| Action Bar override methods ||==========> [<-- END]
}