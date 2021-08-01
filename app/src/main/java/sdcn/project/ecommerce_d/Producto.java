package sdcn.project.ecommerce_d;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.common.collect.Lists;

import java.util.List;

public class Producto implements Parcelable {
    private int cantidad_disponible;
    private float precio, precioNuevo;
    private String name, categoria, unidad;
    private boolean oferta;
    private List<String> url = Lists.newArrayList();

    private List<String> caseSearch;

    // creo una lista de objetos tipo "Producto".


    // <==================|| Constructor without arguments ||==================> [BEGIN]
    Producto(){
        Log.d("Producto(): ","calling a constructor without parameters");
    }
    // <==================|| Constructor without arguments ||==================> [END]





    // <==================|| Constructor with arguments ||==================> [BEGIN]

    // "Poducto" constructor
    Producto(
            String name,
            float precio,
            float precioNuevo,
            int cantidad_disponible,
            String unidad,
            String categoria,
            boolean oferta
    ){
        this.name = name;
        this.precio = precio;
        this.precioNuevo = precioNuevo;
        this.cantidad_disponible=cantidad_disponible;
        this.unidad= unidad;
        this.categoria=categoria;
        this.oferta=oferta;
    }
    // <==================|| Constructor with arguments ||==================> [END]





    // <==================|| creator for parcelable implementation ||==================> [BEGIN]
    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
    // <==================|| creator for parcelable implementation ||==================> [END]





    // <==================|| Getters and Setters ||==================> [BEGIN]
    // the <variable> name in the get<variable>() method is the one used to set the field name
    // and value in the document on firestore database

    public int getCantidad_disponible() {
        return cantidad_disponible;
    }

    public void setCantidad_disponible(int cantidad_disponible)
    {
        this.cantidad_disponible = cantidad_disponible;
    }

    public float getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(float precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public boolean isOferta() {
        return oferta;
    }

    public void setOferta(boolean oferta) {
        this.oferta = oferta;
    }

    // caseSearch

    public List<String> getCaseSearch() {
        return caseSearch;
    }

    public void setCaseSearch(List<String> caseSearch) {
        this.caseSearch = caseSearch;
    }

    // <==================|| Getters and Setters ||==================> [END]





    // <==================|| Parcelling ||==================> [BEGIN]
    public Producto(Parcel in)
    {
        this.name= in.readString();
        this.precio = in.readFloat();
        this.precioNuevo = in.readFloat();
        this.cantidad_disponible = in.readInt();
        this.unidad = in.readString();
        this.categoria = in.readString();
        this.oferta = in.readInt() != 0;
        in.readStringList(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeFloat(this.precio);
        dest.writeFloat(this.precioNuevo);
        dest.writeInt(this.cantidad_disponible);
        dest.writeString(this.unidad);
        dest.writeString(this.categoria);
        dest.writeInt((this.oferta) ? 1 : 0);
        dest.writeStringList(this.url);
    }
    // <==================|| Parcelling ||==================> [END]




    // <==================|| ToString method ||==================> [BEGIN]

    @Override
    public String toString() {
        return "Producto{" +
                "cantidad_disponible=" + cantidad_disponible +
                ", precio=" + precio +
                ", precioNuevo=" + precioNuevo +
                ", name='" + name + '\'' +
                ", categoria='" + categoria + '\'' +
                ", unidad='" + unidad + '\'' +
                ", oferta=" + oferta +
                ", url=" + url +
                '}';
    }
    // <==================|| ToString method ||==================> [END]
}