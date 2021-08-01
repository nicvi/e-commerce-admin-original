package sdcn.project.ecommerce_d;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;

public class Bill implements Parcelable
{

    // <==================|| Arguments ||==================> [BEGIN]
    private String billName, purchasedMethod, billID, userID;
    private Date billDate;
    private boolean pickedUp;
    private float billPrice;
    private Compra compra;// = new HashMap<Producto , Integer>();
    // <==================|| Arguments ||==================> [END]






    // <==================|| Constructor without arguments ||==================> [BEGIN]
    public Bill(){
        Log.d("Bill(): ","calling a constructor without parameters");
    }
    // <==================|| Constructor without arguments ||==================> [END]








    // <==================|| Constructor full arguments ||==================> [BEGIN]

    public Bill(
            String billName,
            String purchasedMethod,
            String billID,
            String userID,
            Date billDate,
            boolean pickedUp,
            float billPrice,
            Compra compra)
    {
        this.billName = billName;
        this.purchasedMethod = purchasedMethod;
        this.billID = billID;
        this.userID = userID;
        this.billDate = billDate;
        this.pickedUp = pickedUp;
        this.billPrice = billPrice;
        this.compra = compra;
    }


    // <==================|| Constructor full arguments ||==================> [END]








    // <==================|| Getters and Setters ||==================> [BEGIN]

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getPurchasedMethod() {
        return purchasedMethod;
    }

    public void setPurchasedMethod(String purchasedMethod) {
        this.purchasedMethod = purchasedMethod;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public float getBillPrice() {
        return billPrice;
    }

    public void setBillPrice(float billPrice) {
        this.billPrice = billPrice;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    // <==================|| Getters and Setters ||==================> [END]









    // <==================|| Parcelling ||==================> [BEGIN]

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(billName);
        dest.writeString(purchasedMethod);
        dest.writeString(billID);
        dest.writeString(userID);
        dest.writeSerializable(billDate);
        dest.writeInt((pickedUp) ? 1 : 0);
        dest.writeFloat(billPrice);
        dest.writeParcelable(compra, flags);
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

    public Bill(Parcel in)
    {
        this.billName = in.readString();
        this.purchasedMethod = in.readString();
        this.billID = in.readString();
        this.userID = in.readString();
        this.billDate = (java.util.Date)  in.readSerializable();
        this.pickedUp = in.readInt() != 0;;
        this.billPrice = in.readFloat();
        this.compra = (Compra) in.readParcelable(Compra.class.getClassLoader());
    }

    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>()
    {
        @Override
        public Bill createFromParcel (Parcel in)
        {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

    // <==================|| Parcelling ||==================> [END]










    // <==================|| ToString ||==================> [BEGIN]
    @Override
    public String toString() {
        return "Bill{" +
                "billName='" + billName + '\'' +
                ", purchasedMethod='" + purchasedMethod + '\'' +
                ", billID='" + billID + '\'' +
                ", userID='" + userID + '\'' +
                ", billDate=" + billDate +
                ", pickedUp=" + pickedUp +
                ", billPrice=" + billPrice +
                ", compra=" + compra +
                '}';
    }
    // <==================|| ToString ||==================> [END]
}