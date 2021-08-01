package sdcn.project.ecommerce_d;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import sdcn.project.ecommerce_d.R;


import java.text.DecimalFormat;

//The ViewHolder is used to specify which views should be used for each data item.
class CaptionedImagesAdapter extends RecyclerView.Adapter<CaptionedImagesAdapter.ViewHolder>
{





    // <==========|| we are telling the adapter what data it should work with. ||==========> [BEGIN]
    // We’ll use these variables (captions and imageIds) to hold the products data.
    private Producto[] productArray;
    private String[] documentID;
    /*
    private String[] nameArray, categoriaArray, unidadArray, urlArray;
    private int[] cantidadDisponibleArray;
    private float[] preciosArray, preciosArrayNuevo;
    private boolean[] ofertaArray;

     */
    // <==========|| we are telling the adapter what data it should work with. ||==========> [END]


    // <==========|| Add the listeners as a private variable. ||==========> [BEGIN]
    private Listener listener;
    private ListenerDelete listenerDelete;
    // <==========|| Add the listener as a private variable. ||==========> [END]

    // <==========|| Viewers. ||==========> [BEGIN]
    //private ImageButton btnDltProduct;
    // <==========|| Viewers. ||==========> [END]




    // <==========|| Interfaces. ||==========> [BEGIN]
    interface Listener
    {
        void onClick(Producto producto);
    }

    interface ListenerDelete
    {
        void onLongClickDelete(String ProductName,String DocumentID, String URL );
        void onClickDelete();
    }
    // <==========|| Interface. ||==========> [END]





    // <==================|| Define the adapter’s view holder ||==================> [BEGIN}
    // Our recycler view needs to display CardViews, so we specify that our ViewHolder contains
    // CardViews. If you want to display another type of data (views) in the recycler view, you
    // should define it here.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageButton btnDltProduct;
        public ViewHolder(CardView v) {
            super(v);
            cardView=v;
            btnDltProduct = (ImageButton) v.findViewById(R.id.Button_Delete_Product);
        }
    }
    // <==================|| Define the adapter’s view holder ||==================> [END]





    // <==========|| Make the constructor. ||==========> [Begin]

    // We’ll pass the data to the adapter using its constructor.
    public CaptionedImagesAdapter(
            String[] documentID,
            Producto[] productArray
    )
    {
        this.documentID = documentID;
        this.productArray = productArray;
    }
    // <==========|| Make the constructor. ||==========> [END]




    //
    // FILOSOFAR SOBRE TU MAL RENDIMIENTO EN LOS ESTUDIOS
    //





    // <==================|| implement the getItemCount() method ||==================> [BEGIN]

    // The length of the captions array equals the number of data items in the recycler view so
    // that number is returned by the getItemCount override method.
    @Override
    public int getItemCount() {
        return productArray.length;
    }
    // <==================|| implement the getItemCount() method ||==================> [END]




    // <==================|| Setting Listeners setters ||==================> [BEGIN]
    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    public void setListenerDelete(ListenerDelete listenerDelete){
        this.listenerDelete = listenerDelete;
    }
    // <==================|| Setting Listeners ||==================> [END]




    // <==================|| Override the onCreateViewHolder() method ||==================> [BEGIN]
    // "CaptionedImagesAdapter.ViewHolder" was changed by just "ViewHolder"
    // The method "onCreateViewHolder" gets called when the recycler view needs to create a view
    // holder. (one for each card)
    @Override
    public CaptionedImagesAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        // "card_captioned_image" is the LayoutInflator to turn the layout into a CardView.
        // This is nearly identical to code you've already seen in the onCreateView() of fragments.
        CardView cv =
                (CardView) LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.card_captioned_image, parent, false
                );
        return new ViewHolder(cv);
    }
    // <==================|| Override the onCreateViewHolder() method ||==================> [END]





    /*
    <==================|| Add the data to the card views ||==================> [END]
    * The recycler view calls this method (onBindViewHolder) when it wants to use (or reuse) a
      view holder for a new piece of data.
    * This method populate the CardView’s ImageView and TextView with data.
    * The parameter "holder" it's an object of type "ViewHolder", an inner class previously created,
      the inner class ViewHolder method must hold the views that the cardView contain, those are:
        - the cardView per se.
        - a delete product button.
      So the holder is used to refer to those tow previously mentioned views.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        // I create a object "Producto" that binds with the attributes showed in the cardView of the
        // recyclerView"
        Producto product = productArray[position];

        // The cardView hold the views that shows the information of each product in the collection
        // "Productos"
        CardView cardView = holder.cardView;

        // instance an ImageView and bind it with the one in the xml file
        ImageView imageView = (ImageView)cardView.findViewById(R.id.info_image);
        // Create a drawable with each images element (Image ID) in the imageIds array.
        System.out.println("product.getUrl().get(0): " + product.getUrl().get(0));
        getImageGlide(  imageView,  product.getUrl().get(0),  cardView);
        /*
        GlideApp.with(  cardView.getContext())
            .load(  product.getUrl().get(0))
            .into(  imageView);
        * */
        // A ContentDescription allows you to associate a textual description to the view.
        // For example it associate an Image of an Apple with the text "apple" that is store
        // and coincide it with array's element from "captions"
        imageView.setContentDescription(product.getName());

        // CardView UI, text views
        cardViewTextViewUI(cardView, position);

        // Recycler view that respond to clicks, it was added a listener.
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (listener!= null){
                    listener.onClick(product);
                }
            }
        });

        // ad a listener to the delete product button that was initialized in the ViewHolder method.

        holder.btnDltProduct.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listenerDelete!= null)
                {
                    listenerDelete.onLongClickDelete(
                            product.getName(),
                            documentID[position],
                            product.getUrl().get(0)
                    );
                }
                return true;
            }
        });

        holder.btnDltProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listenerDelete!= null){
                    listenerDelete.onClickDelete();
                }
            }
        });

    }
    // <==================|| Add the data to the card views ||==================> [END]






    // <==================|| CardView UI, text views  ||==================> [BEGIN]
    public void cardViewTextViewUI(CardView cardView, int position)
    {
        String productName;
        float productPrice, productPriceNew;
        boolean productOffer;
        int cantidadDisponible, maxLength;

        Producto product = productArray[position];

        maxLength = 47;
        cantidadDisponible= product.getCantidad_disponible();
        productOffer = product.isOferta();
        productName = product.getName();
        productPrice  = product.getPrecio();
        productPriceNew = product.getPrecioNuevo();
        // ill show the product name according with the number of characters allowed, if the product
        //  name uses more characters than the allowed then ill add some ellipsis to the product
        //  name.
        // create the TextView para el nombre del producto
        TextView textView = (TextView)cardView.findViewById(R.id.nombre_producto);
        //
        if (productName.length() > maxLength)
        {
            productName = productName.substring(0,maxLength-4) + "....";
        }
        // Display the caption in the TextView.
        textView.setText(productName);

        // if there are an offer price, i'll show the offer price and the no offer price on CardView
        // TODO [______________________ Product offered [TRUE] ______________________________]
        if (productOffer)
        {
            // TODO [______________________ previous price [ENABLE] ______________________________]
            TextView textviewNewPrice =
                    (TextView) cardView.findViewById(R.id.textView_precioAnterior);
            textviewNewPrice.
                    setPaintFlags(textviewNewPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            String txtPrecioAnterior = "$ "+ String.valueOf(productPrice);
            textviewNewPrice.
                    setText( txtPrecioAnterior );


            // TODO [_______________________ Actual price [ENABLE] ______________________________]
            // create the TextView para el precion del producto
            TextView textView_precio = (TextView)cardView.findViewById(R.id.textView_precioActual);
            String txtPrecio = "$ "+ String.valueOf(productPriceNew);
            // Display the caption in the TextView.
            textView_precio.setText(txtPrecio);

            // TODO [_______________________ off price [ENABLE] _______________________________]
            TextView textView_off_price = (TextView)cardView.findViewById(R.id.textView_off_price);
            float productOffPriceFloat;
            productOffPriceFloat =
                    ((productPrice - productPriceNew) / productPrice )*100;

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String productOffPriceStr = decimalFormat.format(productOffPriceFloat) + "% descuento";
            textView_off_price.setText(productOffPriceStr);
            // TODO [_______________________ Availability [ENABLE] ____________________________]
            TextView textView_availability =
                    (TextView)cardView.findViewById(R.id.textView_availability);
            if (cantidadDisponible == 0)
            {
                textView_availability.setText(R.string.unavailableTag);
                textView_availability.setTextColor(Color.parseColor("#981C1C"));
            }
        }
        // TODO [______________________ Product offered [FALSE] ______________________________]
        else{
            // TODO [______________________ previous price [DISABLE] ____________________________]
            TextView textviewNewPrice =
                    (TextView) cardView.findViewById(R.id.textView_precioAnterior);
            textviewNewPrice.setVisibility(View.INVISIBLE);

            // TODO [_______________________ off price [DISABLE] _____________________________]
            TextView textView_off_price = (TextView)cardView.findViewById(R.id.textView_off_price);
            textView_off_price.setVisibility(View.INVISIBLE);

            // TODO [_______________________ Actual price [ENABLE] ____________________________]
            // create the TextView para el precion del producto
            TextView textView_precio = (TextView)cardView.findViewById(R.id.textView_precioActual);
            String txtPrecio = "$ "+ String.valueOf(productPrice);
            // Display the caption in the TextView.
            textView_precio.setText(String.valueOf(txtPrecio));
            // TODO [_______________________ Availability [ENABLE] ____________________________]
            TextView textView_availability =
                    (TextView)cardView.findViewById(R.id.textView_availability);
            if (cantidadDisponible==0)
            {
                textView_availability.setText(R.string.unavailableTag);
                textView_availability.setTextColor(Color.parseColor("#981C1C"));
            }
        }


    }
    // <==================|| CardView UI format  ||==================> [END]





    // <====|| Use glide to bind the image from firebase database with the cardView ||=======> [END]
    // the context could be, the activity or a explicit view as a cardView in this scenario.
    public void getImageGlide( ImageView imageView, String url_image, CardView cardView){
        Glide.with(cardView.getContext())
                .load(url_image)
                .into(imageView);
    }
    // <======|| Use glide to bind the image from firebase database with the cardView ||=====> [END]
}