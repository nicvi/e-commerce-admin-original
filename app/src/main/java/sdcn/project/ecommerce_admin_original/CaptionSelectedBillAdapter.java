package sdcn.project.ecommerce_admin_original;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import sdcn.project.ecommerce_admin_original.R;

import java.util.List;

public class CaptionSelectedBillAdapter extends
        RecyclerView.Adapter<CaptionSelectedBillAdapter.ViewHolder>
{

    // <==========|| Attributes. ||==========> [Begin]
    private final List<Producto> purchaseProductArray;
    private final List<Integer> purchaseQuantityArray;
    // <==========|| Attributes. ||==========> [Begin]





    // <==========|| Make the constructor. ||==========> [Begin]
    // We’ll pass the data to the adapter using its constructor.
    public CaptionSelectedBillAdapter(
            List<Producto> purchaseProductArray
            , List<Integer> purchaseQuantityArray)
    {
        this.purchaseProductArray = purchaseProductArray;
        this.purchaseQuantityArray = purchaseQuantityArray;
    }
    // <==========|| Make the constructor. ||==========> [END]




    // <==================|| Define the adapter’s view holder ||==================> [BEGIN}
    // Our recycler view needs to display CardViews, so we specify that our ViewHolder contains
    // CardViews. If you want to display another type of data (views) in the recycler view, you
    // should define it here.
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private CardView cardView;
        public ViewHolder(CardView v)
        {
            super(v);
            cardView = v;
        }
    }
    // <==================|| Define the adapter’s view holder ||==================> [END]






    // <==================|| implement the getItemCount() method ||==================> [BEGIN]

    // The length of the captions array equals the number of data items in the recycler view so
    // that number is returned by the getItemCount override method.
    @Override
    public int getItemCount() {return this.purchaseProductArray.size();}
    // <==================|| implement the getItemCount() method ||==================> [END]







    // <==================|| Override the onCreateViewHolder() method ||==================> [BEGIN]
    // "CaptionedImagesAdapter.ViewHolder" was changed by just "ViewHolder"
    // The method "onCreateViewHolder" gets called when the recycler view needs to create a view
    // holder. (one for each card)
    @NonNull
    @Override
    public CaptionSelectedBillAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType)
    {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_captioned_purchased_trolley, parent, false);

        return new ViewHolder(cv);
    }
    // <==================|| Override the onCreateViewHolder() method ||==================> [END]





    /*
    <==================|| Add the data to the card views ||==================> [BEGIN]
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
    public void onBindViewHolder(
            @NonNull CaptionSelectedBillAdapter.ViewHolder holder
            , int position)
    {
        //final Down_Up_loader down_up_loader = new Down_Up_loader ();

        final Producto product = purchaseProductArray.get(position);

        int quantity = purchaseQuantityArray.get(position);

        // The cardView hold the views that shows the information of each product in the collection
        // "Productos"
        CardView cardView = holder.cardView;

        SetViews (cardView, product, quantity);
    }
    // <==================|| Add the data to the card views ||==================> [END]




    public void SetViews (CardView cardView, Producto product, int quantity)
    {
        TextView textView_productNameOnBill =cardView.findViewById(R.id.textView_productNameOnBill);
        TextView textView_productCostOnBill=cardView.findViewById(R.id.textView_productCostOnBill);
        TextView textView_productAmongOnBill=cardView.findViewById(R.id.textView_productAmongOnBill);
        TextView textView_productUnitOnBill=cardView.findViewById(R.id.textView_productUnitOnBill);

        textView_productNameOnBill.setText(product.getName());

        String productCost = "$ " + product.getPrecio();
        textView_productCostOnBill.setText(productCost);

        textView_productAmongOnBill.setText(String.valueOf(quantity));

        textView_productUnitOnBill.setText(product.getUnidad());
    }

}
