package sdcn.project.ecommerce_admin_original;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import sdcn.project.ecommerce_admin_original.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO  To be Completed:

    /*
    * upload to fireStore field cantidad_disponible the sum between actual stock and stock added
    * make the views not uploaded unmodifiable, like the name, etc
    * set the product unit in the spinner unit at start of the activity
    * */

public class EditProductActivity extends AppCompatActivity {
    // <==================|| Firestore ||==================> [BEGIN]
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    // <==================|| VARIABLES ||==================> [END]
    private  static final String TAG = "crear_Producto_Activity";
    // We’ll use this constant to pass the ID of the pizza as extra information in the intent.
    public static final String EXTRA_PRODUCT_ATTRIBUTES = "productID";



    // <==================|| OBJECTS ||==================> [END]
    private Producto product;



    // <==================|| Views ||==================> [BEGIN]
    private TextView txt_editedProductCategory;//, productStockActual;
    private EditText txt_editedProductPrice, txt_editedProductStock, txt_editedProductNewPrice,
            txt_editedProductName;
    private CheckBox checkBox_editedOffer;
    private Spinner spinner_editedUnit;
    private ImageView imageView_editedProductImage;
    private Button btnPpdateProduct;

    // <==================|| Structuras ||==================> [BEGIN]
    ArrayAdapter<CharSequence> itemsAdapterUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_edit_product);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]


        // I get the product from the previous activity.
        product = (Producto) Objects.requireNonNull(
                getIntent().
                getExtras()).
                get(EXTRA_PRODUCT_ATTRIBUTES);
        // initialize the views
        setProductValues();

        btnPpdateProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // check if the product to be uploaded exists, if true then update the product
                checkProduct("Productos", "name", product);
            }
        });



    }

    public void setProductValues()
    {
        // bind the views variables with the views on the xml layout file
        txt_editedProductName= (EditText) findViewById(R.id.txt_editedProductName);
        txt_editedProductPrice = (EditText) findViewById(R.id.txt_editedProductPrice);
        txt_editedProductNewPrice = (EditText) findViewById (R.id.txt_editedProductNewPrice);
        txt_editedProductStock = (EditText) findViewById (R.id.txt_editedProductStock);
        checkBox_editedOffer = (CheckBox) findViewById (R.id.checkBox_editedOffer);
        spinner_editedUnit = (Spinner) findViewById(R.id.spinner_editedUnit);
        txt_editedProductCategory = (TextView) findViewById (R.id.txt_editedProductCategory);
        imageView_editedProductImage = (ImageView) findViewById(R.id.imageView_editedProductImage);
        btnPpdateProduct= (Button) findViewById (R.id.btn_updateProduct);

        newPriceChecker();

        // [____________|| set actual values  to the views ||____________] [<--BEGIN]
        txt_editedProductName.setText(product.getName());
        txt_editedProductPrice.setText(String.valueOf( product.getPrecio()));
        txt_editedProductNewPrice.setText(String.valueOf( product.getPrecioNuevo()));
        txt_editedProductStock.setText(String.valueOf(product.getCantidad_disponible()));
        // populate and set the actual item in the spinner
        populateUnitsSpinner(spinner_editedUnit);
        txt_editedProductCategory.setText(product.getCategoria());
        // Initialize the imageView, binding the firebase storage image with that of this activity
        getImageGlide(imageView_editedProductImage, product.getUrl().get(0));
        // [____________|| set actual values  to the views ||____________] [END-->]

    }




    //================================ New Price (offer) checker ==================================// [BEGIN]

    public void newPriceChecker()
    {
        if (product.isOferta())
        {
            checkBox_editedOffer.setChecked(true);
            txt_editedProductNewPrice.setEnabled(true);
            txt_editedProductNewPrice.setText(String.valueOf(product.getPrecioNuevo()));
        }
        else
        {
            checkBox_editedOffer.setChecked(false);
            txt_editedProductNewPrice.setEnabled(false);
        }


        this.checkBox_editedOffer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if( ((CheckBox) v).isChecked() )
                {
                    txt_editedProductNewPrice.setEnabled(true);
                }
                else
                {
                    txt_editedProductNewPrice.setText(String.valueOf(product.getPrecioNuevo()));
                    txt_editedProductNewPrice.setEnabled(false);
                }
            }
        });

    }

    //================================ CheckBox checker  =================================// [END]





    public void populateUnitsSpinner(Spinner spinner)
    {
        int actualUnit = 0;
        // Create an ArrayAdapter using the string array and a default spinner layout
        itemsAdapterUnits = ArrayAdapter.createFromResource(this,
                R.array.unidades_array, android.R.layout.simple_spinner_item);
        for ( int i = 0; i < itemsAdapterUnits.getCount() ; i++)
        {
            if (
                product.getUnidad().equals(
                        Objects.requireNonNull(itemsAdapterUnits.getItem(i)).toString()
                )
            )
            {
                System.out.println("itemsAdapterUnits: " + itemsAdapterUnits.getItem(i));
                actualUnit= i;
            }
        }
        // Specify the layout to use when the list of choices appears
        itemsAdapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(itemsAdapterUnits);
        spinner.setSelection(actualUnit);

    }






    public void getImageGlide( ImageView imageView, String url_image)
    {
        Glide.with(this)
                .load(url_image)
                .into(imageView);
    }






    //============================ Check product existence ==============================// [BEGIN]
    // I use checkProduct method after update the product info views values
    public void checkProduct(
            final String collectionChose,
            final String fieldChose,
            final Producto producto
    )
    {
        // create variables need it to get the fireStore collection
        CollectionReference notesCollectionRef = db.collection(collectionChose);
        Query notesQuery = null;

        // now i manipulate the collection to find the require document, the product need it.
        notesQuery = notesCollectionRef
                .whereEqualTo(fieldChose, producto.getName());
        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()) {
                   // If the document wanted exist then i'll update it

                   // TODO Here use a mothod for:
                   //update the values of Producto object producto, matching the view's values with
                   // the product's values
                   updateProductObject();

                   // update the value to firebase
                   updateValue(
                           collectionChose,
                           Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId(),
                           producto
                   );
               }
               else{
                   Log.w(
                           "checkCategoryCreate",
                           "Error getting documents.",
                           task.getException()
                   );
               }
           }
        });
    }
    //============================ Check product existence ==============================// [END]






    //============================ Update product value ==============================// [END]
    public void updateProductObject()
    {

        product.setName(txt_editedProductName.getText().toString());
        product.setOferta(checkBox_editedOffer.isChecked());
        product.setPrecio(Float.parseFloat( txt_editedProductPrice.getText().toString() ) );
        product.setPrecioNuevo(Float.parseFloat( txt_editedProductNewPrice.getText().toString()));
        product.setCantidad_disponible(
                Integer.parseInt(txt_editedProductStock.getText().toString() ) );
        product.setUnidad(spinner_editedUnit.getSelectedItem().toString());

        product.setCaseSearch(makeCaseSearchArray( product.getName()) );
    }

    //============================ Update product value ==============================// [END]







    // [BEGIN-->] <==========|| make a caseSearch array ||==========>
    private List<String> makeCaseSearchArray(String caseSearch) {

        // put in lower case
        caseSearch = caseSearch.toLowerCase();
        // get rit of the accents, if there are one
        caseSearch = Normalizer.normalize(caseSearch, Normalizer.Form.NFD);
        caseSearch = caseSearch.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // create a array list of strings and prepare it
        List<String> caseSearchList = new ArrayList<>();
        String temp = "";

        // fill the string array
        for (char letter : caseSearch.toCharArray())
        {
            temp = temp + letter;
            caseSearchList.add(temp);
        }

        //split the string and add each word on the String array
        for (int i = 0; i<caseSearch.split(" ").length; i++)
        {
            if (i>0)
            {
                caseSearchList.add(caseSearch.split(" ")[i]);
            }
        }

        return caseSearchList;
    }
    // [BEGIN-->] <==========|| make a caseSearch array ||==========>








    //============================ Update product value ==============================// [BEGIN]
    public void updateValue(
        String strCollection,
        String strDocument,
        Producto product
    )
    {
        DocumentReference docRef = db.collection(strCollection).document(strDocument);

        docRef
            .update(

                    "name", product.getName(),
                    "oferta", product.isOferta(),
                    "precio", product.getPrecio(),
                    "precioNuevo", product.getPrecioNuevo(),
                    "cantidad_disponible",product.getCantidad_disponible(),
                    "unidad", product.getUnidad(),
                    "caseSearch", product.getCaseSearch()

            )
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
    }
    //============================ Update product value ==============================// [END]









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