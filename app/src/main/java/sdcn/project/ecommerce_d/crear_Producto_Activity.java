package sdcn.project.ecommerce_d;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import sdcn.project.ecommerce_d.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class crear_Producto_Activity extends AppCompatActivity {

    // ==================================================
    // ==================== FALTA =======================
    // ==================================================

        // * cargar la imagen desde el celular (obtener el ID de la imagen)
            // * almacenar la imagen en el objeto Producto del metodo crearProducto
                // * subir la imagen del objeto producto a la base de datos

        // * que el producto que se quiera registrar en la base de datos no este creado anteriormente

        // * definir el acuerdo comercial del producto
        // * unir la cantidad en stock del producto con la cantidad de producto

        // * initialize the spinner


    // <==================|| Firestore ||==================> [END]
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference storageReference;



    // <==================|| Views ||==================> [END]
    private TextInputEditText txtInput_productName;
    private EditText editText_productPrice, editText_productPriceNuevo, editText_productAmountStock;
    private CheckBox  checkbox_Offer;
    private Spinner spinner_Category, spinner_Unidad;
    private ImageView imageView_product;
    private Button btn_selectImage, btn_createProduct;



    // <==================|| Varibales producto ||==================> [END]
    private int cantidad_disponible_Product;
    private String nameProduct, categoriaProduct, unidadProducto;
    private float precioProduct, precioProductNuevo;
    private boolean ofertaProduct;
    private long totalProducts;
    private  static final String TAG = "crear_Producto_Activity";
    public Uri imageUri;



    // <==================|| Objects ||==================> [END]
    private Producto producto;



    // <==================|| Structuras ||==================> [END]
    private ArrayList<String> mNotes = new ArrayList<>(), units =  new ArrayList<>();
    private ArrayAdapter<String> itemsAdapter;
    private ArrayAdapter<CharSequence> itemsAdapterUnits;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear__producto_);

        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_crear_producto);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]

        txtInput_productName= (TextInputEditText) findViewById(R.id.txt_productoName);
        editText_productPrice = (EditText) findViewById(R.id.txt_productoPrice);
        editText_productPriceNuevo = (EditText) findViewById(R.id.txt_productPriceNuevo);
        editText_productAmountStock = (EditText) findViewById (R.id.txt_productoCantidad);
        checkbox_Offer = (CheckBox) findViewById (R.id.checkBox_oferta);
        spinner_Category = (Spinner) findViewById (R.id.spinner_category);
        spinner_Unidad = (Spinner) findViewById(R.id.spinner_Unidad);
        imageView_product = (ImageView) findViewById(R.id.imageView_uploaded);
        btn_selectImage = (Button) findViewById(R.id.btn_uploadImage);
        this.btn_createProduct= (Button) findViewById (R.id.btn_crearProducto);

        txtInput_productName.setText("");
        editText_productPrice.setText("");
        editText_productPriceNuevo.setText("");

        editText_productPrice.setText("");


        // check if i can put an offer price
        newPriceChecker();
        // aqui se instanciara el spinner
        populateUnitsSpinner(spinner_Unidad);
        // take the values from docuemnts in the collection "category" from the firestore databse
        // and then populate it to the spinner.
        getFirestoreElementsToSpinner();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        this.btn_selectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                choosePicture();
            // or here create code for upload the image from the phone, acceding the button
            // btn_selectImage
            }
        });

        this.btn_createProduct.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                producto = CrearProducto(
                        txtInput_productName,
                        editText_productPrice,
                        editText_productPriceNuevo,
                        editText_productAmountStock,
                        checkbox_Offer,
                        spinner_Category,
                        spinner_Unidad,
                        imageView_product,
                        imageUri);

                if (producto!=null)
                {
                    // add the string caseSearch array to the product
                    producto.setCaseSearch(makeCaseSearchArray(
                            txtInput_productName.getText().toString())
                    );

                    checkCategoryCreateDeleteIt(
                            "Productos",
                            "create",
                            "name",
                            producto);

                    System.out.println("[BUTTONCREATEPRODUCT] NewPriceP: "+
                            editText_productPriceNuevo.getText()
                    );
                    System.out.println("[BUTTONCREATEPRODUCT] producto: "+ producto.toString() );

                }
                else{
                    System.out.println("Producto es null");
                }
            }
        });

    }


    //================================ New Price (offer) checker ==================================// [BEGIN]

        public void newPriceChecker()
        {
            checkbox_Offer.setChecked(false);
            editText_productPriceNuevo.setEnabled(false);
            editText_productPriceNuevo.setText(R.string.noOfferPrice);

            this.checkbox_Offer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if( ((CheckBox) v).isChecked() )
                    {
                        editText_productPriceNuevo.setEnabled(true);
                        //productPriceNuevo.setText("0");
                    }
                    else
                    {
                        editText_productPriceNuevo.setText(R.string.noOfferPrice);
                        editText_productPriceNuevo.setEnabled(false);
                    }
                }
            });

        }

    //================================ CheckBox checker  =================================// [END]





    //===================== CREAR PRODUCTO ===========================================//[BEGIN]

    // The method  CrearProducto() create a Product object from the arguments received
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Producto CrearProducto (
            TextInputEditText productName, EditText productPrice, EditText productPriceNuevo,
            EditText productAmountStock, CheckBox checkboxOffer,
            Spinner spinnerCategory, Spinner spinnerUnidad,
            ImageView productImage, Uri ImageUri
    ){
        // validate if the arguments received in the method are not null and if those are not then
        // create the object.
        if (
                !Objects.requireNonNull(productName.getText()).toString().equals("") &
                !productPrice.getText().toString().equals("") &
                !productPriceNuevo.getText().toString().equals("") &
                !spinnerUnidad.getSelectedItem().toString().equals("")&
                !spinnerCategory.getSelectedItem().toString().equals("")&
                !productAmountStock.getText().toString().equals("") &
                ImageUri!=null &
                !Objects.equals(ImageUri, Uri.EMPTY))
        {

            //System.out.println("ImageUri"+ImageUri);

            Log.d("ImageUri(uploadP): ", ImageUri.toString());

            // initialization of the product variables and then create the Object Product
            this.nameProduct= productName.getText().toString();
            this.precioProduct= Float.parseFloat(productPrice.getText().toString());
            this.precioProductNuevo= Float.parseFloat(productPriceNuevo.getText().toString());
            this.cantidad_disponible_Product= Integer.parseInt(productAmountStock.getText().toString());
            this.ofertaProduct = checkboxOffer.isChecked();
            this.categoriaProduct=spinnerCategory.getSelectedItem().toString();
            this.unidadProducto= spinnerUnidad.getSelectedItem().toString();

            System.out.println("[CREARPRODUCTO] precioProductNuevo: "+ precioProductNuevo);
            System.out.println("[CREARPRODUCTO] precioProduct: "+ precioProduct);
            productImage.getTransitionName();
            // create the Product with the variables previously initialized
            return new Producto(
                    this.nameProduct, this.precioProduct, this.precioProductNuevo,
                    this.cantidad_disponible_Product, unidadProducto,
                    this.categoriaProduct, this.ofertaProduct
            );
        }
        else{
            Log.d("Al crear producto","Los parametros de productos son null");
            Toast.makeText(this,
                    "Complete los parametros para crear producto",
                    Toast.LENGTH_SHORT
            ).show();
        }
        return null;
    }

    //===================== CREAR PRODUCTO ===========================================//[END]





    //============================================================================================//
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkCategoryCreateDeleteIt(
            final String strCollectionChose,
            final String action_create_remove,
            final String strFieldChose,
            final Producto producto
    )
    {
        // create variables need it to get the firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesCollectionRef = db.collection(strCollectionChose);
        Query notesQuery = null;

        String value=producto.getName();
        // now i manipulate the document that i get from the collection search
        notesQuery = notesCollectionRef
                .whereEqualTo(strFieldChose, value);
        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // this if identify if the value already exist on the Firestore database
                    if (Objects.requireNonNull(task.getResult()).size()>0){
                        // if the value already exist and we want to create it then a warning
                        // message will be display in a Toast way
                        if (action_create_remove.equals("create")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Ya creado",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        // if the "action" argument is remove  then i have to remove the document
                        //  that contain the value of the itme i want to remove
                        if (action_create_remove.equals("remove")){
                            // i dont kno why i use the variable newSizeListView
                            // here create a method to remove the item from firestore
                            db.collection(strFieldChose).document(
                                    task.getResult().getDocuments().get(0).getId()
                            ).delete();
                            // TODO removeFromFirestore(str_collection,object,task.getResult().getDocuments().get(0).getId(),getApplicationContext);
                        }
                    }
                    // if the category inserted do not exist then create it
                    else {
                        if (action_create_remove.equals("create"))
                        {
                            // i don't know why i use the variable newSizeListView

                            registrarProductoUploadPicture(
                                    producto,
                                    strCollectionChose
                            );
                        }
                    }
                    // actualizo el array con las categorias creadas.
                    //reloadAdapter();
                }
                else{
                    Log.w("checkCategoryCreate", "Error getting documents.", task.getException());
                }
            }
        });
    }

    //============================================================================================//
    // Update firestore value
    //============================================================================================//

    public void updateValue(
        String strCollection,
        String strDocument,
        String strField,
        String strValue
    ){
        if (strCollection.equals("CantidadProductos"))
        {

            db.collection(strCollection)
                    .document(strDocument)
                    .update(strField, Long.valueOf(strValue))
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "document from: "+ strCollection +" updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document from: "+strCollection, e);
                        }
                    });
        }
        else
        {
            db.collection(strCollection)
                    .document(strDocument)
                    .update(strField, FieldValue.arrayUnion(strValue))
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "document from: "+ strCollection +" updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document from: "+strCollection, e);
                        }
                    });
        }
    }

    //============================================================================================//
    // upload Producto objecto to firestore
    //============================================================================================//


    public void registrarProductoUploadPicture(
            Producto producto,
            String strCollectionChose
    )
    {
        // Create a new user with a first and last name

        // Add a new document with a random generated ID in the Productos collection
        db.collection(strCollectionChose)
            .add(producto)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // if the Producto is uploaded in the firestore database then i'll upload the
                    // image
                    uploadPictureUpdateValue(strCollectionChose,documentReference.getId());
                    Log.d(
                            TAG,
                            "DocumentSnapshot added with ID: "
                                    + documentReference.getId()
                    );

                    // and update the CantidadProductos collection
                    getCantidadProductos(documentReference.getId());
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
    }

    //============================================================================================//

    private void getCantidadProductos(String documentID)
    {
        DocumentReference docRef = db
                .collection("CantidadProductos")
                .document("metaData");
        // i get the number of products
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()){
                        if (document.getLong("totalProductos")!=null)
                        {
                            totalProducts = document.getLong("totalProductos");
                            totalProducts+=1;
                            System.out.println("totalProducts: "+totalProducts);
                            // update the values of totalProducts
                            updateValue("CantidadProductos",
                                    "metaData",
                                    "totalProductos",
                                    String.valueOf(totalProducts)
                            );
                        }
                    }
                    else{
                        Log.d("Error", "No such document");
                    }
                }else{
                    Log.d("Error", "get failed with ");
                }
            }
        });
    }

    //============================================================================================//

    // The addSnapshotListener, onEvent method will not keep refreshed the values retrieved from
    //  firestore database
    public void getFirestoreElementsToSpinner()
    {
        db.collection("Categorias")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // El lazo for accede al valor "Categoria" de cada documento de la coleccion
                        // "categorias" y lo almacena en la variable docuemnto que luego se almacena
                        // en un Array que se lo envia al ListView para ser mostrado en pantalla.
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String note = Objects.requireNonNull(document.getData().get("Categoria")).toString();
                            int i=0;
                            if (mNotes.size()==0)mNotes.add(note);
                            // recorro areglo local
                            for (String str :mNotes){
                                //System.out.println(" note: "  + note);
                                if (str.equals(note)){
                                    i+=1;
                                }
                            }
                            // al finalizar el loop para recorrer el array local, yo ya se
                            // segun el valor de i si se debe actualizar o no el array local
                            if (i==0){
                                mNotes.add(note);
                            }
                        }
                        mostrarSpinner();
                    }
                    else
                    {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }

    public void populateUnitsSpinner(Spinner spiner){
        // Create an ArrayAdapter using the string array and a default spinner layout
        itemsAdapterUnits = ArrayAdapter.createFromResource(this,
                R.array.unidades_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        itemsAdapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spiner.setAdapter(itemsAdapterUnits);
    }

    public void mostrarSpinner(){
        itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mNotes);
        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Category.setAdapter(itemsAdapter);
    }

    //============================================================================================//
    // Picture
    //============================================================================================//

    // choosePicture() method is used for upload a picture of the product to firebase storage
    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    // Overrid method onActivityResult() is used for upload a picture of the product in firebase storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            imageView_product.setImageURI(imageUri);
            //uploadPicture();
        }
    }

    private void uploadPictureUpdateValue
    (
            String strCollectionChose,
            String documentChose
    )
    {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("uploadPicture...");
        pd.show();
        final String randomKey = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("products/"+ nameProduct);
        final UploadTask uploadTask = ImagesRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){

                Task<Uri> urlTask = uploadTask.continueWithTask(
                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", Objects.requireNonNull(task.getException()).toString());
                        }
                        return ImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            // updateValue() method will set the url of the image uploaded in
                            // firebaseStorage in the field "url" of the corresponding document
                            updateValue(
                                    strCollectionChose,
                                    documentChose,
                                    "url",
                                    Objects.requireNonNull(downloadUri).toString()
                            );
                            Log.i("setThisUri", downloadUri.toString());// This is the one you should store
                        } else {
                            Log.i("wentWrong","downloadUri failure");
                        }
                    }
                });
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content),
                        "Producto creado",
                        Snackbar.LENGTH_LONG
                ).show();
            }
            })
            .addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception exception){
                    pd.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "failed to Upload",
                            Toast.LENGTH_LONG
                    ).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask
                        .TaskSnapshot taskSnapshot)
                {
                    double progressPercent = (
                            100.00*taskSnapshot
                                    .getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                    pd.setMessage("Progress" + (int) progressPercent + "%");
                }
            });
    }


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