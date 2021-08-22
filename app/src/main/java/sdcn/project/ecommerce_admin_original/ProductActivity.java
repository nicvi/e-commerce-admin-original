package sdcn.project.ecommerce_admin_original;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import sdcn.project.ecommerce_admin_original.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

// Import these Performance Monitoring classes at the top of your `.java` file
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

// TODO This activity show the products in the recyclerView

// * just download the data from fireStore take from 1.5 to 6.2 seconds, without showing them on the
//      recyclerView, and without considering the time that take to download the image and then displayed
//      them.
public class ProductActivity extends AppCompatActivity {
    // [____________|| Viewers. ||____________] [<--BEGIN]
    Button btnAnterior, btnSiguiente;
    // i make a view RecyclerView  and bind it with the xml file that contains the recyclerView.
    RecyclerView productoRecycler;
    // [____________|| Viewers. ||____________] [END-->]

    // [____________|| Product Object Variables (captionedImageAdapter) ||____________] [<--BEGIN]
    private Producto[] productArray;
    String[] documentID;
    // [____________|| Product Object Variables (captionedImageAdapter) ||____________] [END-->]

    // [____________|| Firestore ||____________] [<--BEGIN]
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query first;

    // [____________|| Firestore ||____________] [END-->]

    // [____________|| Pagination ||____________] [<--BEGIN]
    Paginator paginator ;
    // TODO private int totalPages = Paginator.TOTAL_NUM_ITEMS / Paginator.ITEMS_PER_PAGE;
    int totalPages;
    int productsPerPages=5;
    int total_Products;

    boolean data_download= false;
    // [____________|| Pagination ||____________] [END-->]

    // [____________|| Variables ||____________] [<--BEGIN]
    // si currentPage lo cambio a 1, entonces no le debo sumar el +1 al currentPage
    // que se encuenta en el case 0 del metodo paging.
    private int currentPage = 0;
    long startTime;
    long startTime2;
    long startTime3;
    final int productsPerPage = 6;
    long totalProducts;
    DocumentSnapshot lastVisibleNext, lastVisiblePrevious;
    // [____________|| Variables ||____________] [END-->]


    Trace myTrace = FirebasePerformance.getInstance().newTrace("test_trace");
    Trace myTrace2 = FirebasePerformance.getInstance().newTrace("test_trace_pics");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);


        // [____________|| Viewers. ||____________] [BEGIN]
        btnAnterior= (Button) findViewById(R.id.btnAnterior);
        btnSiguiente= (Button) findViewById(R.id.btnSiguiente);
        btnAnterior.setEnabled(false);
        btnSiguiente.setEnabled(false);
        // i make a view RecyclerView  and bind it with the xml file that contains the recyclerView
        productoRecycler = (RecyclerView) findViewById(R.id.product_recycler);
        // [____________|| Viewers. ||____________] [ END] ------>>>>>>


        // [BEGIN -->] _______________Toolbar_____________________________
        // set the toolbar on the layout as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_product);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        // ___________________________Toolbar___________________ [<-- END]

        // <----------------------------------------------------------------------------------------


        startTime = System.currentTimeMillis();

        // first i call the "getProductsNumber" method which get me the total number of products,
        //      this method also call the method "paginateFirstQuery" which retrieve the information of
        //      the first "productsPerPage" number of products.
        getProductsNumber();

        btnSiguiente.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                btnSiguiente.setEnabled(false);
                System.out.println("button next Clicked");
                paginateNextQuery();
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btnAnterior.setEnabled(false);
                System.out.println("button next Clicked");
                paginatePreviousQuery();
            }
        });
    }

    public void getProductsNumber()
    {
        myTrace.start();
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
                        totalProducts = document.getLong("totalProductos");
                        System.out.println("totalProducts: "+totalProducts);
                        // Do something
                        myTrace.stop();
                        paginateFirstQuery();
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

    public void updateProductsNumber(String action)
    {

        DocumentReference docRef = db
                .collection("CantidadProductos")
                .document("metaData");

        // i update the number of products, i decrease or increase it.
        if (action.equals("updateAdd")){
            totalProducts+=1;
           docRef
           .update("totalProductos", totalProducts)
           .addOnSuccessListener(new OnSuccessListener<Void>(){
               @Override
               public void onSuccess(Void avoid){
                   // products number  was successfully updated --> i'll reload the recyclerView
                   Log.d("UpdatedAdd: ", "Total products increased successfully!");
               }
           })
           .addOnFailureListener(new OnFailureListener(){
               @Override
               public void onFailure(@NonNull Exception e){
                   System.out.println("Not Updated");
                   Log.w("Updated", "Error updated document", e);
               }
           });

        }
        else if(action.equals("updateMinus")){
            totalProducts-=1;
            docRef
            .update("totalProductos", totalProducts)
            .addOnSuccessListener(new OnSuccessListener<Void>(){
                @Override
                public void onSuccess (Void avoid){
                    //paginate first Query again;
                    paginateFirstQuery();
                    Log.d("UpdatedMinus", "Total products decreased successfully!");
                }
            })
            .addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e){
                    Log.w("updatedMinus: ", "Error Updated document", e);
                }
            });
        }

    }

    public void paginateActualQuery()
    {
        startTime2 = System.currentTimeMillis();

        Query next = db.collection("Productos")
                .orderBy("name")
                .startAt(lastVisiblePrevious)
                .limit(productsPerPage);

        System.out.println("before next query");
        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots)
                    {
                        // enable btn next, paging next pages
                        paging(documentSnapshots);
                        // update the lastVisible document
                        lastVisiblePrevious = documentSnapshots.getDocuments()
                                .get(0);

                        // call a method for get the Products from fireStore
                        fillArrayProducts(documentSnapshots, documentSnapshots.size());
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Total execution time: " + (endTime - startTime2));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("onFailure", "accessing next document", e);
                    }
                });
    }


    public void paginateNextQuery()
    {
        startTime2 = System.currentTimeMillis();

            Query next = db.collection("Productos")
                    .orderBy("name")
                    .startAfter(lastVisibleNext)
                    .limit(productsPerPage);

            System.out.println("before next query");
            next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots)
                    {
                        // enable btn next, paging next pages
                        currentPage+=1;
                        paging(documentSnapshots);
                        // update the lastVisible document
                        lastVisibleNext = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);
                        lastVisiblePrevious = documentSnapshots.getDocuments()
                                .get(0);

                        // call a method for get the Products from fireStore
                        fillArrayProducts(documentSnapshots, documentSnapshots.size());
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Total execution time: " + (endTime - startTime2));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("onFailure", "accessing next document", e);
                }
            });
    }

    public void paginatePreviousQuery(){
        startTime3 = System.currentTimeMillis();

        Query next = db.collection("Productos")
                .orderBy("name")
                .endBefore(lastVisiblePrevious)
                .limitToLast(productsPerPage);

        System.out.println("before next query");
        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots)
                    {
                        // enable btn next, paging next pages
                        currentPage-=1;
                        paging(documentSnapshots);
                        // update the lastVisible document
                        lastVisibleNext = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);
                        lastVisiblePrevious = documentSnapshots.getDocuments()
                                .get(0);

                        // call a method for get the Products from fireStore
                        fillArrayProducts(documentSnapshots, documentSnapshots.size());
                        final long endTime = System.currentTimeMillis();
                        System.out.println("Total execution time: " + (endTime - startTime3));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("onFailure", "accessing next document", e);
                    }
                });

    }
    public void paginateFirstQuery()
    {
        currentPage = 0;
        // Construct query for first 25 cities, ordered by population
        first = db.collection("Productos")
                .orderBy("name")
                .limit(productsPerPage);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        // Construct a new query starting at this document,
                        // get the next 25 cities.

                        // paging first page, setting the corresponding views
                        paging(documentSnapshots);


                        // enable to prepare to get the next query if the condition is true
                        if (documentSnapshots.getDocuments().size()>0)
                        {
                            // set values for the last document read it and the first one
                            lastVisibleNext = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() -1);
                            lastVisiblePrevious = documentSnapshots.getDocuments()
                                    .get(0);

                            // call a method for get the Products from fireStore
                            fillArrayProducts(documentSnapshots, documentSnapshots.size());
                        }
                    }
                });
    }

    public void paging (QuerySnapshot documentSnapshots)
    {
        // enable btn next
        boolean fullPage = documentSnapshots.size()<productsPerPage;
        boolean firstPage = currentPage == 0;
        int value = 0;
        if (fullPage) value|= 0x01;
        if (firstPage) value|= 0x02;

        switch (value)
        {
            case 0:
                // fullPage false and firstPage false
                System.out.println("case 0 (a)");
                if (totalProducts>documentSnapshots.size()*(currentPage+1))
                {
                    System.out.println("case 0 (a), n page");
                    btnSiguiente.setEnabled(true);
                    btnAnterior.setEnabled(true);
                }
                else{
                    System.out.println("case 0 (a), n page y last page");
                    btnSiguiente.setEnabled(false);
                    btnAnterior.setEnabled(true);
                }
                break;
            case 1:
                // fullPage true ^ firstPage false
                System.out.println("case 1 (c), last page");
                btnSiguiente.setEnabled(false);
                btnAnterior.setEnabled(true);
                break;
            case 2:
                // fullPage false ^ firstPage true
                System.out.println("case 2 (b)");
                if (totalProducts>documentSnapshots.size())
                {
                    System.out.println("case 2 (b), (full) 1rst of n pages");
                    btnSiguiente.setEnabled(true);
                    btnAnterior.setEnabled(false);
                }
                else{
                    System.out.println("case 2 (b), (full) 1rst and last page");
                    btnSiguiente.setEnabled(false);
                    btnAnterior.setEnabled(false);
                }
                break;
            case 3:
                // fullPage true and firstPage true
                System.out.println("case 3 (d), first an last page");
                btnSiguiente.setEnabled(false);
                btnAnterior.setEnabled(false);
                break;
            default:
                throw new RuntimeException("Something strange happens, is fullPage?: "+ fullPage+
                        "is firstPage?: " + firstPage);
        }
    }



    public void fillArrayProducts(
            QuerySnapshot firestoneProductsArray,
            int productsPerPage)
    {
        myTrace2.start();
        if (totalProducts>0)
        {
            productArray= new Producto[productsPerPage];
            documentID = new String[productsPerPage];
            for (int i = 0; i < productsPerPage; i++)
            {
                // here fill the arrays
                // firestoneProductsArray.getDocuments().get(i).get()
                Producto product =
                        firestoneProductsArray
                                .getDocuments()
                                .get( i)
                                .toObject(Producto.class);

                documentID[i] = firestoneProductsArray.getDocuments().get(i).getId();
                assert product != null;
                productArray[i]= product;
            }
            toImageAdapter();
        }
        else
        {
            //productoRecycler.setAdapter(null);
            productoRecycler.setVisibility(View.GONE);
            Toast.makeText(
                    getApplicationContext(),
                    "No hay productos para mostrar. Cree productos.",
                    Toast.LENGTH_SHORT
            ).show();
        }
        myTrace2.stop();
    }


    // -------------------------------------------------------------------------------------------->

    // <==========|| constructProduct method ||==========> [BEGIN]
    // constructProduct method gets the information of each document on the "Producto" collection
    public void constructProduct(String collection){
        Task<QuerySnapshot> docRef;
        docRef = db.collection(collection)
                .get();
        docRef.addOnCompleteListener(new OnCompleteListener< QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){

                if(task.isSuccessful()){
                    total_Products= Objects.requireNonNull(task.getResult()).size();
                    totalPages=total_Products/productsPerPages;
                    pagingold(task.getResult(), total_Products, productsPerPages);
                }
                else{
                    Log.w(
                            "constructProduct(): ",
                            "Error getting documents.",
                            task.getException()
                    );
                }
            }
        });
    }
    // <==========|| constructProduct method ||==========> [END]





    // <==========|| paging method ||==========> [BEGIN]
    public void pagingold(
            QuerySnapshot FirestoneProductList,
            int totalProducts,
            int productPerPage)
    {
        // if "totalProducts=0" then there are no products in the firestone collection "Productos"
        // then there are products
        if(totalProducts>0) {
            // First i assume that are as many products in fireStore as productPerPage
            // each array must be reset and re-initialize each time "paging" method is called.
            // each array size must be the "productPerPage" int value.
            productArray= new Producto[productPerPage];
            documentID = new String[productPerPage];

            // Basics paging variables
            int ITEMS_REMAINING=totalProducts % productPerPage;
            int LAST_PAGE=totalProducts/productPerPage;

            // "i" is the item index from all the lists used for add on it all the products field
            // obtained from the "Products" collection query snapshot list.

            //int i =0 ;

            // entrego una lista con una coleccion personalizada, osea no con todos los productos,
            // sino con los productos correspendientes de la pagina.
            int startItem=currentPage*productPerPage;

            // if there are items remaining then those will be show in a new page ("extra page").
            if (currentPage == LAST_PAGE && ITEMS_REMAINING > 0)
            {
                // initialize again the documentID array and the productArray array
                productArray= new Producto[ITEMS_REMAINING];
                documentID = new String[ITEMS_REMAINING];

                for (int i = 0; i < ITEMS_REMAINING; i++)
                {
                    // here fill the arrays
                    Producto product =
                            FirestoneProductList
                            .getDocuments()
                            .get(startItem + i)
                            .toObject(Producto.class);

                    documentID[i] = FirestoneProductList.getDocuments().get(i).getId();
                    assert product != null;
                    productArray[i]= product;
                }
            }
            // else: totalItems <= productPerPage
            else
                {
                for (int i = 0; i < productPerPage; i++)
                {
                    // here fill the arrays
                    Producto product =
                            FirestoneProductList.
                                    getDocuments().
                                    get(startItem + i).
                                    toObject(Producto.class);
                    documentID[i] = FirestoneProductList.getDocuments().get(i).getId();
                    assert product != null;
                    productArray[i]= product;
                }
            }
            toImageAdapter();
        }
        // If there are no elements to download from firebase, because totalProducts = 0, then the
        // recyclerView will show nothing.
        else
        {
            productoRecycler.setAdapter(null);
            Toast.makeText(
                    getApplicationContext(),
                    "No hay productos para mostrar. Cree productos.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
    // <==========|| paging method ||==========> [END]




    // <==========|| toImageAdapter method creates a CaptionImagesAdapter object that will initialize all the cardViews in the RecyclerView ||==========> [BEGIN]
    public void toImageAdapter(){
        // create an CaptionImagesAdapter object and then i'll pass the arrays with the products
        // information that i want yo show in  the RecyclerView
        // The CaptionImagesAdapter only receive as arguments arrays with the elements to be
        // displayed.
        CaptionedImagesAdapter Adapter = new CaptionedImagesAdapter(
                documentID,
                productArray
        );

        productoRecycler.setAdapter(Adapter);
        // LinearLayoutManager is used to show the cardview in a list way
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productoRecycler.setLayoutManager(layoutManager);

        // data_download set as true will allow to do paging
        //data_download = true;

        // * the listener will launch the EditActivity activity when the cardView within the
        //   recyclerView is touched
        // * "startActivity" implements the Listener onClick() method.
        //   It starts PizzaDetailActivity, passing it the product the user chose.

        Adapter.setListener(new CaptionedImagesAdapter.Listener() {
            public void onClick(Producto producto) {
                Intent intent = new Intent(
                        ProductActivity.this,
                        EditProductActivity.class
                );
                intent.putExtra(EditProductActivity.EXTRA_PRODUCT_ATTRIBUTES, producto);
                ProductActivity.this.startActivity(intent);
            }
        });

        Adapter.setListenerDelete(new CaptionedImagesAdapter.ListenerDelete() {
            @Override
            public void onLongClickDelete(
                    String ProductName,
                    String documentID,
                    String urlImagePic
            )
            {
                Toast.makeText(
                        getApplicationContext(),
                        "Producto eliminado.",
                        Toast.LENGTH_SHORT
                ).show();
                // * here i delete the product image in firebase storage through a delete method.
                removeImageStored("products", urlImagePic);
                // * here i delete the product document in firestone through a delete method.
                removeProduct(ProductName, documentID);
                // Reduce de number of total products
                updateProductsNumber("updateMinus");
                // * and call again the method that fill the recyclerView, but with updated products.
                //constructProduct("Productos");
                //paginateActualQuery();


            }

            public void onClickDelete() {
                Toast.makeText(
                        getApplicationContext(),
                        "Mantenga pulsado para eliminar producto.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    // <==========|| toImageAdapter method creates a CaptionImagesAdapter object that will initialize all the cardViews in the RecyclerView ||==========> [END]




    // <==========|| toggleButtons enable or disable the next or previous button depending if it is on the first or last page ||==========> [BEGIN]
    private void toggleButtons() {
        if (currentPage == totalPages) {
            btnSiguiente.setEnabled(false);
            btnAnterior.setEnabled(true);
        } else if (currentPage == 0) {
            btnAnterior.setEnabled(false);
            btnSiguiente.setEnabled(true);
        } else if (currentPage >= 1 && currentPage <= totalPages) {
            btnSiguiente.setEnabled(true);
            btnAnterior.setEnabled(true);
        }
    }
    // <==========|| toggleButtons enable or disable the next or previous button depending if it is on the first or last page ||==========> [END]




    // <==========|| Delete from firestone method ||==========> [Begin]
    // this method delete the document of the selected product.

    public void removeProduct(final String item_removed, final String DocumentID ){

        FirebaseFirestore db = FirebaseFirestore.getInstance();;

        db.collection("Productos").document(DocumentID)
            .delete()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Se elimino Producto: "+item_removed,
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
    // <==========|| Delete from fireStore method ||==========> [END]




    // <==========|| Delete from Firebase Storage method ||==========> [Begin]
    public void removeImageStored(String folderName,String urlImagePic){
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a reference to the file to delete
        StorageReference httpsReference =
                storage.
                getReferenceFromUrl(
                        urlImagePic
                );
        //StorageReference desertRef = storageRef.child( folderName + "/" + picName );

        // Delete the file
        httpsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
    // <==========|| Delete from Firebase Storage method ||==========> [Begin]



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