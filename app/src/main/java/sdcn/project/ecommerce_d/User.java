package sdcn.project.ecommerce_d;

import android.os.Parcel;
import android.os.Parcelable;

/*
/**  "@Data" creates all basic methods and a non argument constructor with help of LOMBOK plugin.
 *   "@AllArgsConstructor(access = AccessLevel.PROTECTED)" creates a full arguments constructor
 */

public class User implements Parcelable
{

    // <==================|| Arguments ||==================> [BEGIN]
    private String firstName, lastName, email, phone, documentID;
    private double latitude, longitude;
    // <==================|| Arguments ||==================> [END]





    // <==================|| Constructor full arguments ||==================> [BEGIN]
    public User(
            String firstName,
            String lastName,
            String email,
            String phone,
            String documentID,
            double latitude,
            double longitude
    )
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.documentID = documentID;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // <==================|| Constructor full arguments ||==================> [END]





    // <==================|| Constructor without arguments ||==================> [BEGIN]
    public User() {    }
    // <==================|| Constructor without arguments ||==================> [END]







    // <==================|| Getters and Setters ||==================> [BEGIN]
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(documentID);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public User(Parcel in)
    {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.documentID = in.readString();
        this.latitude =  in.readDouble();
        this.longitude = in.readDouble();

    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
    {
        @Override
        public User createFromParcel (Parcel in)
        {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    // <==================|| Parcelling ||==================> [END]





    // <==================|| ToString ||==================> [BEGIN]
    @Override
    public String toString()
    {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", documentID='" + documentID + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    // <==================|| ToString ||==================> [BEGIN]

}