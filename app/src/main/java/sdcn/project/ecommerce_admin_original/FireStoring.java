package sdcn.project.ecommerce_admin_original;

import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FireStoring {
    private FirebaseFirestore db;
    private Context getApplicationContext;

    public Boolean creadoUploadPic;

    private ListView lv;
    ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> mNotes;

    public FireStoring(
            FirebaseFirestore db,
            Context getApplicationContext
            ){
        this.db= db;
        this.getApplicationContext= getApplicationContext;
    }
    /*
    public void reloadAdapter(){

        itemsAdapter.clear();
        itemsAdapter.addAll(mNotes);
        itemsAdapter.notifyDataSetChanged();
    }

     */


    // el metodo get sirve para buscar y obtener coincidencias en la base de datos segun los
    // argumentos str_value y "state"
    //  que se le ingresa como argumento. Y dependiendo de los resultados, se crea la categoria o se
    //  se la elimina.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkCategoryCreateDeleteIt(
                                            final Map<String, Object> object,
                                            final String str_collection,
                                            final String action_create_remove,
                                            final String field
    )
    {
        // create variables need it to get the firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesCollectionRef = db.collection(str_collection);
        Query notesQuery = null;

        // i get the key from the hasmap "object", ill use it for identify the type of element
        // stored in the hashMap.
        String strObject = null, value=null;
        Producto producto = null;
        Set<String> keys = object.keySet();
        for (String k : keys) {
            strObject= k;
        }
        // if the element stored in the hashmap is a Product object then ill do:
        if (Objects.equals(strObject, "Producto")){

            producto= (Producto) object.get("Producto");
            value= Objects.requireNonNull(producto).getName();
        }
        // if the element stored in the hashmap is an String object then ill do:
        else{
            value= (String) object.get("String");
        }
        // now i manipulate the document that i get "notesQuery" from the collection search
        notesQuery = notesCollectionRef
                .whereEqualTo(field, value);
        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    System.out.println("-1creadoUploadPic checkCategoryCreateDeleteIt: "+creadoUploadPic);
                    setCreadoUploadPic(true);
                    // this if identify if the value already exist on the Firestore database
                    if (Objects.requireNonNull(task.getResult()).size()>0){
                        // if the value already exist and we want to create it then a warning
                        // message will be display in a Toast way
                        if (action_create_remove.equals("create")) {
                            Toast.makeText(
                                    getApplicationContext,
                                    "Ya creado",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                        // if the "action" argument is remove  then i have to remove the document
                        //  that contain the value of the itme i want to remove
                        if (action_create_remove.equals("remove")){
                            // i dont kno why i use the variable newSizeListView
                            // here create a method to remove the item from firestore
                            db.collection(str_collection).document(
                                    task.getResult().getDocuments().get(0).getId()
                            ).delete();
                            // TODO removeFromFirestore(str_collection,object,task.getResult().getDocuments().get(0).getId(),getApplicationContext);
                        }
                    }
                    // if the category inserted do not exist then create it
                    else {
                        if (action_create_remove.equals("create")){
                            // i dont kno why i use the variable newSizeListView
                            System.out.println("0creadoUploadPic checkCategoryCreateDeleteIt: "+creadoUploadPic);
                            writeToFirestore(object,str_collection, db);
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




    public void removeFromFirestore(
            final String str_collection,
            final String item_removed,
            final String DocumentID
    ){
        if (!item_removed.equals("Sin categoría")) {

            db = FirebaseFirestore.getInstance();
            db.collection(str_collection).document(DocumentID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(
                                    getApplicationContext,
                                    "Se elimino de coleccion"+ str_collection + ": "+item_removed,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("removeFromFirestore", "Error deleting document", e);
                        }
                    });
        }
        else{
            Toast.makeText(
                    getApplicationContext,
                    "La colleccion: '" + item_removed +"' no se puede eliminar",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void writeToFirestore(
            final Map<String, Object> object,
            final String str_collection,
            FirebaseFirestore db
    ){

        //==========================================
        CollectionReference notesCollectionRef = db.collection(str_collection);

        final String str_value;
        String strObject = null;
        Producto producto = null;
        Set<String> keys = object.keySet();
        for (String k : keys) {
            strObject= k;
        }
        // if the element stored in the hashmap is a Product object then ill do:
        if (Objects.equals(strObject, "Producto")){

            producto= (Producto) object.get("Producto");
            str_value= Objects.requireNonNull(producto).getName();
            notesCollectionRef.add(Objects.requireNonNull(producto))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // if creado is true then ill get this value from other class and use it as a
                    // green flag to upload the picture

                    System.out.println("1creadoUploadPic writeToFirestore: "+creadoUploadPic);
                    Toast.makeText(
                        getApplicationContext,
                        "Se creo en "+ str_collection+ ": " +str_value,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("writeToFirestore", "Error adding document", e);
                    }
                });
            System.out.println("2creadoUploadPic writeToFirestore: "+creadoUploadPic);
        }
        // if the element stored in the hashmap is an String object then ill do:
        else{
            str_value= (String) object.get("String");
            final Map<String, Object> hash_value = new HashMap<>();
            hash_value.put(str_collection, str_value);

            notesCollectionRef.add(hash_value)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(
                            getApplicationContext,
                            "Se creo en categoria: "+str_value,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("writeToFirestore", "Error adding document", e);
                        }
                    });
            ;
        }

    }

    public Boolean getCreadoUploadPic() {
        if (creadoUploadPic==null)creadoUploadPic=false;
        System.out.println("4creadoUploadPic getCreadoUploadPic: "+ creadoUploadPic);
        return creadoUploadPic;
    }

    public void setCreadoUploadPic(Boolean creado) {
        this.creadoUploadPic = creado;
    }
}