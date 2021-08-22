package sdcn.project.ecommerce_admin_original;

import androidx.paging.PagingDataAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import sdcn.project.ecommerce_admin_original.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class FacturaActivity extends AppCompatActivity {

    // [____________|| Variables ||____________] [<--BEGIN]
    long totalProducts;
    private boolean isPickedUp;
    private String strPickUpCollection;
    // [____________|| Variables ||____________] [<--END]



    // [____________|| VIEWERS ||____________] [<--BEGIN]
    private PagingDataAdapter BillPagingDataAdapter;
    private RecyclerView recyclerView;
    private TextView textView_noBillsMessage;
    private LinearLayout linearLayout_onFacturaActivity;
    private TextView textView_BillLogList;
    // [____________|| VIEWERS ||____________] [<--END]



    // [____________|| FireStore ||____________] [<--BEGIN]
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [____________|| FireStore ||____________] [<--END]



    // [____________|| Progress Bar ||____________] [<--BEGIN]
    private ProgressDialog progressBar;
    private int progressBillBarStatus = 0;
    private boolean shouldAllowBack= true;
    // [____________|| Progress Bar ||____________] [<--BEGIN]

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_factura);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]


        Bundle b = getIntent().getBundleExtra("intentBundle");
        if (b!=null)
        {
            isPickedUp =b.getBoolean("pickedUpBills");

            // initialize the recyclerView
            linearLayout_onFacturaActivity = findViewById(R.id.linearLayout_onFacturaActivity);
            recyclerView = findViewById(R.id.RecyclerView_facturas);
            recyclerView.setVisibility(View.GONE);
            textView_noBillsMessage=findViewById(R.id.textView_noBillsMessage);
            textView_BillLogList = findViewById(R.id.textView_BillLogList);

            LaunchProgressBarAndBuy(textView_noBillsMessage);
            String strBillLogTitle = "Historico de compras";
            if (isPickedUp)
            {
                System.out.println("isPickedUp");
                strBillLogTitle +=" (Recogidos/ Entregados)";
                textView_BillLogList.setText(strBillLogTitle);
                strPickUpCollection  ="Recogido";

            }
            else
            {
                System.out.println("isNotPickedUp");
                strBillLogTitle +=" (NO Recogidos/ No Entregados)";
                textView_BillLogList.setText(strBillLogTitle);
                strPickUpCollection  ="NoRecogido";
            }
            getProductsNumber();

        }
        else
        {
            System.out.println("b==null");
        }
        receivedDataFromPreviousActivity();

    }

    public void getProductsNumber()
    {
        // i get the number of products
        db
            .collection("Facturas")
            .document("9JLKeuCuSqH9ZUzDh61X")
            .collection(strPickUpCollection)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            System.out.println("task isSuccessful");
                            if (!task.getResult().isEmpty())
                            {
                                System.out.println("Bill collection is NOT empty");
                                textView_noBillsMessage.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                // Do something
                                //LaunchProgressBarAndBuy(textView_noBillsMessage);
                                launchRecyclerView ();
                            }
                            else
                            {
                                System.out.println("Bill collection IS empty");

                                recyclerView.setVisibility(View.GONE);
                                textView_noBillsMessage.setVisibility(View.VISIBLE);

                                Log.d("TAG", "Error getting documents: ",
                                        task.getException());

                                progressBillBarStatus = 100;
                            }
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Bill collection on fail");
                        Log.w("onFailure", "Error deleting document", e);
                    }
                });
    }



    public void launchRecyclerView ()
    {
        // Create new MoviesAdapter object and provide
        CaptionedBillAdapter billsAdapter = new CaptionedBillAdapter(new BillComparator());


        // Create ViewModel
        BillViewModel billViewModel=new ViewModelProvider(
                this,
                new MyViewModelFactory(
                        this.getApplication()
                        , strPickUpCollection
                )
        ).get(BillViewModel.class);


        // set the adapter to the recyclerView
        recyclerView.setAdapter(billsAdapter);


        // Subscribe to to paging data
        billViewModel.pagingDataFlow.subscribe(billPagingData -> {
            // submit new data to recyclerview adapter
            billsAdapter.submitData(getLifecycle(), billPagingData);
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        progressBillBarStatus = 100;

        // set adapter listeners
        billsAdapter.setListenerAccessBill(new CaptionedBillAdapter.ListenerAccessBill()
        {
            @Override
            public void onClickAccessBill(Bill currentBill, User user) {
                // TODO pass the bill object the next activity
                // Cause i'm deleting a product, i'll send a Bill object wrapped in
                // a bundle object over an intent, to the BuyNowActivity.
                Bundle bundleBill = new Bundle();
                bundleBill.putParcelable("optionsBill", currentBill);

                // add a User object as an extra bundle
                bundleBill.putParcelable("bundleUser", user);

                // i initialize the intent, to be ready to send the Compra object

                // put as a bundle the doc ID
                Bundle bundle_FacturaBillID = new Bundle();

                Intent intent;
                intent = new Intent(FacturaActivity.this, BillSelectedActivity.class);
                intent.putExtra("bundleBill", bundleBill);
                startActivityForResult(intent, 1);
            }
        });

        billsAdapter.setListenerDeleteBill(new CaptionedBillAdapter.ListenerDeleteBill() {
            @Override
            public void onLongClickRemove(String billId) {
                // call delete function
                deleteBill(billId);

                //
                progressBillBarStatus = 0;
                LaunchProgressBarAndBuy(textView_noBillsMessage);
                getProductsNumber();

            }

            @Override
            public void onClickDelete() {
                // show toast message
                Toast.makeText(
                        getApplicationContext(),
                        "Mantenga pulsado el botón para eliminar factura." ,
                        Toast.LENGTH_LONG)
                        .show();
            }
        });


    }






    public void LaunchProgressBarAndBuy(View v)
    {
        progressBar = new ProgressDialog(v.getContext());
        progressBar.setCancelable(false);
        progressBar.setMessage("Obteniendo facturas...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBillBarStatus = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBillBarStatus < 100) {
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
                            progressBar.setProgress(progressBillBarStatus);
                        }
                    });

                     */
                }

                if (progressBillBarStatus >= 100) {

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBar.dismiss();
                }
            }
        }).start();
    }



    public void deleteBill(String billId)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();;

        db.collection("Facturas")
                .document("9JLKeuCuSqH9ZUzDh61X")
                .collection(strPickUpCollection)
                .document(billId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Se elimino factura",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("onFailure", "Error deleting document", e);
                    }
                });
    }


    public void receivedDataFromPreviousActivity()
    {
        String caller = getIntent().getStringExtra("billDeleted");
        if (  caller!=null  && caller.equals("BillSelected"))
        {
            shouldAllowBack= false;
        }
    }


    @Override
    public void onBackPressed()
    {
        if (shouldAllowBack)
        {
            super.onBackPressed();
        }
        else
        {
            shouldAllowBack=true;
            Intent intent = new Intent(this,BillSelectorActivity.class);
            startActivity(intent);
        }
    }


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