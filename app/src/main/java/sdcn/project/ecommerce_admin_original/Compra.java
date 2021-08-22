package sdcn.project.ecommerce_admin_original;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

/**  "@Data" creates all basic methods and a non argument constructor with help of LOMBOK plugin.
 *   "@AllArgsConstructor(access = AccessLevel.PROTECTED)" creates a full arguments constructor
 */


public class Compra implements Parcelable
{

    // <==================|| Arguments ||==================> [BEGIN]
    private List<String> purchaseDocumentIDArray;
    private List<Producto> purchaseProductArray;
    private List<Integer> purchaseQuantityArray;
    // <==================|| Arguments ||==================> [END]







    // <==================|| Constructor ||==================> [BEGIN]

    public Compra
            (
                    List<String> purchaseDocumentIDArray,
                    List<Producto> purchaseProductArray,
                    List<Integer> purchaseQuantityArray
            )
    {
        this.purchaseDocumentIDArray = purchaseDocumentIDArray;
        this.purchaseProductArray = purchaseProductArray;
        this.purchaseQuantityArray = purchaseQuantityArray;
    }

    // <==================|| Constructor ||==================> [END]








    // <==================|| Getters and Setters ||==================> [BEGIN]

    public List<String> getPurchaseDocumentIDArray() {
        return purchaseDocumentIDArray;
    }

    public void setPurchaseDocumentIDArray(List<String> purchaseDocumentIDArray) {
        this.purchaseDocumentIDArray = purchaseDocumentIDArray;
    }

    public List<Producto> getPurchaseProductArray() {
        return purchaseProductArray;
    }

    public void setPurchaseProductArray(List<Producto> purchaseProductArray) {
        this.purchaseProductArray = purchaseProductArray;
    }

    public List<Integer> getPurchaseQuantityArray() {
        return purchaseQuantityArray;
    }

    public void setPurchaseQuantityArray(List<Integer> purchaseQuantityArray) {
        this.purchaseQuantityArray = purchaseQuantityArray;
    }

    // <==================|| Getters and Setters ||==================> [END]









    // <==================|| Constructor without arguments ||==================> [BEGIN]
    public Compra(){
        Log.d("Producto(): ","calling a constructor without parameters");
    }
    // <==================|| Constructor without arguments ||==================> [END]













    // <==================|| Parcelling ||==================> [BEGIN]

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeList(purchaseDocumentIDArray);
        dest.writeList(purchaseProductArray);
        dest.writeList(purchaseQuantityArray);
        /*
        dest.writeInt(this.products.size());
        for (Map.Entry<Producto, Integer> entry : this.products.entrySet())
        {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeInt(entry.getValue());
        }
         */
        //dest.writeMap(this.products);
    }

    public Compra(Parcel in)
    {
        this.purchaseDocumentIDArray= in.readArrayList(List.class.getClassLoader());
        this.purchaseProductArray= in.readArrayList(Producto.class.getClassLoader());
        this.purchaseQuantityArray= in.readArrayList(List.class.getClassLoader());

        /*
        int groupsSize = in.readInt();
        this.products = new HashMap<Producto,Integer> (groupsSize);

        for (int i = 0; i< groupsSize; i++)
        {
            Producto key = in.readParcelable(Producto.class.getClassLoader());
            Integer value = in.readInt();

            this.products.put(key, value);
        }
         */
        //in.readMap(products, Producto.class.getClassLoader());
    }

    public static final Creator<Compra> CREATOR = new Creator<Compra>()
    {
        @Override
        public Compra createFromParcel (Parcel in)
        {
            return new Compra(in);
        }

        @Override
        public Compra[] newArray(int size) {
            return new Compra[size];
        }
    };

    // <==================|| Parcelling ||==================> [END]






    // <==================|| ToString ||==================> [BEGIN]

    @Override
    public String toString() {
        return "Compra{" +
                "purchaseDocumentIDArray=" + purchaseDocumentIDArray +
                ", purchaseProductArray=" + purchaseProductArray +
                ", purchaseQuantityArray=" + purchaseQuantityArray +
                '}';
    }

    // <==================|| ToString ||==================> [END]
}


/*

    // <=============|| Universal code for parceling for hashMaps of objects ||============> [BEGIN]


    // For writing to a Parcel
    public <K extends Parcelable,V extends Parcelable> void writeParcelableMap(
            Parcel parcel, int flags, Map<K, V > map)
    {
        parcel.writeInt(map.size());
        for(Map.Entry<K, V> e : map.entrySet()){
            parcel.writeParcelable(e.getKey(), flags);
            parcel.writeParcelable(e.getValue(), flags);
        }
    }

    // For reading from a Parcel
    public <K extends Parcelable,V extends Parcelable> Map<K,V> readParcelableMap(
            Parcel parcel, Class<K> kClass, Class<V> vClass)
    {
        int size = parcel.readInt();
        Map<K, V> map = new HashMap<K, V>(size);
        for(int i = 0; i < size; i++){
            map.put(kClass.cast(parcel.readParcelable(kClass.getClassLoader())),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }

    /////////////////
    // Usage ////////
    /////////////////


    // MyClass1 and MyClass2 must extend Parcelable
    Map<MyClass1, MyClass2> map;

    // Writing to a parcel
    writeParcelableMap(parcel, flags, map);

    // Reading from a parcel
    map = readParcelableMap(parcel, MyClass1.class, MyClass2.class);

    // <=============|| Universal code for parceling for hashMaps of objects ||============> [END]
*/


/*
 // <==================|| Parcelling functions ||==================> [BEGIN]
    // For writing to a Parcel
    public <K extends Parcelable,V extends Integer> void writeParcelableMap(
            Parcel parcel, int flags, Map<K, V > map)
    {
        parcel.writeInt(map.size());
        for(Map.Entry<K, V> e : map.entrySet()){
            parcel.writeParcelable(e.getKey(), flags);
            parcel.writeInt(e.getValue());
        }
    }

    // For reading from a Parcel
    public <K extends Parcelable,V extends Integer> Map<K,V> readParcelableMap
    (
            Parcel parcel,
            Class<K> kClass,
            Class<V> vClass
    )
    {
        int size = parcel.readInt();
        Map<K, V> map = new HashMap<K, V>(size);
        for(int i = 0; i < size; i++){
            map.put(
                    kClass.cast(parcel.readParcelable(kClass.getClassLoader())),
                    vClass.cast(parcel.readInt())
            );
        }
        return map;
    }
    // <==================|| Parcelling functions ||==================> [END]
* */