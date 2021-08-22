package sdcn.project.ecommerce_admin_original;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import sdcn.project.ecommerce_admin_original.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*

    Log.d() - Send a DEBUG log message.
    Log.e() - Send an ERROR log message.
    Log.i() - Send an INFO log message.
    Log.v() - Send a VERBOSE log message.
    Log.w() - Send a WARN log message.
    Log.wtf() - What a Terrible Failure: Report an exception that should never happen.

 */

public class createCategoryActivity extends AppCompatActivity {

    // <==================|| Firestore ||==================> [END]
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  final String TAG="CATEGORIA: ";// = "YOUR-TAG-NAME";
    // <==================|| Views ||==================> [END]
    private TextInputEditText category;
    // <==================|| Buttons ||==================> [END]
    Button btn_crear_ategorias;
    // <==================|| Variables ||==================> [END]
    private String category_str;
    static int addedContador;
    // <==================|| Variables ||==================> [END]
    private DocumentSnapshot mLastQueriedDocument;
    private ArrayList<String> mNotes = new ArrayList<>();
    private View mParentLayout;
    private ListView lv;
    //private int actualSizeListView=1, newSizeListView=0;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_create_category);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]

        //initializate the view previously declareted
        mParentLayout = findViewById(android.R.id.content);
        btn_crear_ategorias = (Button) findViewById(R.id.button_createCategory);
        // TODO nuevo
        lv = (ListView) findViewById(R.id.ListViewCategorias);

        // call "getFirestoreElements" method to add the cloud firestore database elements to the
        // local array needed to initializate the ListView

        gettingFromFirestore("Categorias", "Categoria");
        // call "getFirestoreElements" method to add the cloud firestore database elements to the
        // local array needed to initializate the ListView

        btn_crear_ategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                category = (TextInputEditText) findViewById(R.id.txt_category);
                category_str= Objects.requireNonNull(category.getText()).toString();

                if (!category_str.equals("")){
                    checkCategoryCreateDeleteIt(category_str, "create");
                }
                else{
                    Toast.makeText(
                            getApplicationContext(),
                            "Escriba una categoria",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });


    }

    public void reloadAdapter(){
        itemsAdapter.clear();
        itemsAdapter.addAll(mNotes);
        itemsAdapter.notifyDataSetChanged();
    }

    public void mostrarListView(){

        lv = (ListView) findViewById(R.id.ListViewCategorias);
        itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mNotes);
        lv.setAdapter(itemsAdapter);

        // I add a long click listener to the ListView so this way i can delete the long press
        // element from the Firestore database

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                // TODO Auto-generated method stub

                String selectedCategory=mNotes.get(pos);

                // aqui elimino elemento seleccionado, osea el "selectedCategory".

                checkCategoryCreateDeleteIt(selectedCategory, "remove");
                return true;
            }
        });

        // I add a  click listener to the ListView so this way i can advertise the user how to
        // delete an element from the Firestore database
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
            {
                String selectedCategory=mNotes.get(position);
                Toast.makeText(
                        getApplicationContext(),
                        "Manten presionado para eliminar categoria: "+selectedCategory,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // el metodo get sirve para buscar y obtener coincidencias en la base de datos segun los
    // argumentos str_value y "state"
    //  que se le ingresa como argumento. Y dependiendo de los resultados, se crea la categoria o se
    //  se la elimina.
    public void checkCategoryCreateDeleteIt(final String str_value, final String action){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("Categorias");
        Query notesQuery = null;

        // utilizo el Query "notesQuery" para saber si la categoria que quiero crear ya existe.
        notesQuery = notesCollectionRef
                .whereEqualTo("Categoria", str_value);
        final String notesQuery_eliminalo = notesQuery.getFirestore().toString();

        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    //System.out.println("tamaño: " + task.getResult().size());

                    // este if identify if the category already exist on the Firestore database
                    if (Objects.requireNonNull(task.getResult()).size()>0){
                        if (action.equals("create")){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "La categoria ya esta creada.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        // if the "action" argument is remove  then i have to remove the document
                        //  that contain the value of the itme i want to remove
                        if (action.equals("remove")){
                            // i dont kno why i use the variable newSizeListView
                            //newSizeListView = actualSizeListView-1;
                            // here create a method to remove the item from firestore
                            //System.out.println("REMOVE documentID: " + task.getResult().getDocuments().get(0).getId());
                            removeFromFirestore(
                                    str_value,
                                    task.getResult().getDocuments().get(0).getId()
                            );
                        }
                        gettingFromFirestore("Categorias", "Categoria");
                    }
                    // if the category inserted do not exist then create it
                    else {
                        if (action.equals("create")){
                            //System.out.println("Se creo elemento.");
                            // i dont kno why i use the variable newSizeListView
                            //newSizeListView = actualSizeListView+1;
                            writeToFirestore(str_value);
                        }
                    }
                    // actualizo el array con las categorias creadas.
                    //getFirestoreElements();
                    reloadAdapter();
                }
                else{
                    makeSnackBarMessage ("Query Failed. Check Logs");
                }
            }
        });
    }

    public void removeFromFirestore(final String item_removed, final String DocumentID ){
        if (!item_removed.equals("Sin categoría")) {

            //System.out.println("Se puede eliminar la categoria");
            FirebaseFirestore db = FirebaseFirestore.getInstance();;

            db.collection("Categorias").document(DocumentID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(
                                getApplicationContext(),
                                "Se elimino categoria: "+item_removed,
                                Toast.LENGTH_LONG
                            ).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
        else{
            Toast.makeText(
                    getApplicationContext(),
                    "La categoria: '" + item_removed +"' no se puede eliminar",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // The addSnapshotListener, onEvent method will keep refreshed the values retrieved from the firestore
    // database

    public void gettingFromFirestore(String collection, String field){

        db.collection(collection)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    ArrayList<String> elementsArray = new ArrayList<>();
                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.getData().get(field) != null) {
                            //here i get the "value" that match with the "field".
                            //Because i create a new List in the lines above, the elements of the
                            //arrays will not be repeated.
                            Log.d("valor",
                                    Objects.requireNonNull(doc.getData().get(field)).toString());
                            elementsArray.add(Objects.requireNonNull(doc.getData().get(field)).toString());
                        }
                    }
                    mNotes=elementsArray;
                    mostrarListView();
                    Log.d(TAG, "Currente elements in CA: " + elementsArray);
                }
            });
    }


    public void getFirestoreElements(){
        db.collection("Categorias")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // El lazo for accede al valor "Categoria" de cada documento de la coleccion
                        // "categorias" y lo almacena en la variable docuemnto que luego se almacena
                        // en un Array que se lo envia al ListView para ser mostrado en pantalla.
                        //actualSizeListView=task.getResult().size();
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
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
                            // al finalizar el loop para recorrer el array local, yo ya se segun el
                            // valor de i si se debe actualizar o no el array local
                            if (i==0){
                                mNotes.add(note);
                            }

                        }
                        mostrarListView();
                    }
                    else
                    {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }

    public void putCategorias2Array(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference notesCollectionRef = db.collection("Categorias");
        //System.out.println("En metodo 'get'");
        Query notesQuery = null;
        // si "mLastQueriedDocument" no es null es porque hay datos en el snapshot del documento
        if (mLastQueriedDocument != null){
            notesQuery= notesCollectionRef
                    .startAfter(mLastQueriedDocument);
            //System.out.println("if mLastQueriedDocument");
        }
        else{
            notesQuery = notesCollectionRef;
            //System.out.println("else mLastQueriedDocument");
        }
        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //System.out.println("notesQuerry");
                if (task.isSuccessful()) {

                    //System.out.println("tamaño: " + task.getResult().size());
                    //actualSizeListView=task.getResult().size();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String note = Objects.requireNonNull(document.getData().get("Categoria")).toString();
                        //System.out.println("document.toString(): "+ document.toString());
                        //mNotes.add(note);
                        //mNotes.add(note);
                        int i=0;
                        //System.out.println("mNotes.size(): "+ mNotes.size());
                        if (mNotes.size()==0)mNotes.add(note);

                        // recorro areglo local
                        for (String str :mNotes){
                            //System.out.println(" note: "  + note);
                            if (str.equals(note)){
                                i+=1;
                            }
                        }
                        // al finalizar el loop para recorrer el array local, yo ya se segun el valor de i si se debe actualizar o no el array local
                        if (i==0){
                            mNotes.add(note);
                        }

                    }
                    if (task.getResult().size() != 0) {
                        mLastQueriedDocument = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }
                }
                else{
                    makeSnackBarMessage ("Query Failed. Check Logs");
                }
            }
        });
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public void writeToFirestore(final String str_value){

        if (!str_value.equals("")) {

            // Create a new user with a first and last name
            final Map<String, Object> categoria = new HashMap<>();
            categoria.put("Categoria", str_value);

            db.collection("Categorias")
                    .add(categoria)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Se creo categoria: "+str_value,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            //System.out.println("NO se creo base de datos");
                        }
                    });
        }
    }

    public void readFromFirestore(){
        db.collection("Categorias")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            //System.out.println("DATA GOT: "+document.getData());
                            // condiciones para crear categorias
                            //if(document.getData())
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
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