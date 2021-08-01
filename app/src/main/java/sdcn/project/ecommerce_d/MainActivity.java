package sdcn.project.ecommerce_d;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import sdcn.project.ecommerce_d.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // <==================|| Firestore ||==================> [BEGIN]
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // <==================|| Firestore ||==================> [END]



    // <==================|| Views ||==================> [BEGIN]
    private Button btn_producto, btn_crearProducto, btn_categorias, btn_nuevo;
    // <==================|| Views ||==================> [END]



    // <==================|| Variables ||==================> [BEGIN]
    private String CHANNEL_ID = "Canal de notificación de compras";
    // <==================|| Variables ||==================> [END]


    // <==================|| Objects ||==================> [BEGIN]
    private Bill bill;
    private User user;
    // <==================|| Objects ||==================> [BEGIN]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // [BEGIN -->] _______________ Toolbar _____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________ Toolbar ___________________ [<-- END]


        btn_producto = (Button) findViewById(R.id.btn_producto);

        btn_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hola signin");
                startActivity(new Intent(MainActivity.this, ProductActivity.class));
            }
        });

        ////////////////////////
        btn_crearProducto = (Button) findViewById(R.id.btn_crear_producto_menu);

        btn_crearProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hola signin");
                startActivity(
                        new Intent(MainActivity.this, crear_Producto_Activity.class)
                );
            }
        });

        ////////////////////////
        btn_categorias = (Button) findViewById(R.id.btn_categorias);

        btn_categorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hola signin");
                startActivity(new Intent(MainActivity.this, createCategoryActivity.class));
            }
        });

        /////////////////////////

        btn_nuevo = (Button) findViewById(R.id.btn_pruebas);

        btn_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BillSelectorActivity.class));
            }
        });



        // <==================|| Notification ||==================> [BEGIN}

        notificationLauncher();
        // <==================|| Notification ||==================> [END]
    }


    public void notificationLauncher()
    {
        db.collection("Facturas")
            .document("9JLKeuCuSqH9ZUzDh61X")
            .collection("NoRecogido")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED)
                        {
                            // if there a are a new Bill added then a msn will be showed
                            Log.d("TAG", "New city: " + dc.getDocument().getData());
                            bill = dc.getDocument().toObject(Bill.class);
                            if (bill!= null) getUser();

                        }
                    }

                }
            });
    }


    private void getUser()
    {
        db
                .collection("Usuarios")
                .document(bill.getUserID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            System.out.println("task isSuccessful");
                            DocumentSnapshot document= task.getResult();
                            if (document.exists())
                            {
                                System.out.println("User exists");
                                // launch the notification
                                createNotificationChannel();
                                user = document.toObject(User.class) ;
                                notificationSettings();

                            }
                            else
                            {
                                System.out.println("User doesn't exists");
                                Log.d("TAG", "Error getting User: ",
                                        task.getException());
                            }
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("User document on fail");
                Log.w("onFailure", "Error getting user document", e);
            }
        });
    }



    public void notificationSettings()
    {
        Bundle bundle_noPickedUpBills = new Bundle();
        bundle_noPickedUpBills.putBoolean("pickedUpBills", false);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, FacturaActivity.class);

        intent.putExtra("intentBundle", bundle_noPickedUpBills);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.admin_notificatio_icon)
                .setContentTitle("Hay una nueva compra")
                .setContentText("Nueva compra de "+user.getFirstName())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Nueva compra de "+user.getFirstName()+
                                ". "+ getBillDate(String.valueOf(bill.getBillDate()))))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private String getBillDate(String date)
    {
        String[] dateTimeArray = date.split(" ");

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

        return  month + " " +day + ", " +year + " - " +time;
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
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
    // <==========|| Action Bar override methods ||==========> [<-- END]
}