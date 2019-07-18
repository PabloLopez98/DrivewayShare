package pablo.myexample.drivewayshare;

import android.widget.ImageView;

public class MyPost {

    public String mypostname;
    public String address;
    public String time;
    public String price;
    public String imageUrl;
    public String requested;
    public String creatorId;
    public String creatorPostId;
    public String clientName;
    public String clientPlate;
    public String clientModel;

    public MyPost() {
        //required default constructor
    }

    public MyPost(String mypostname, String address, String time, String price, String imageUrl, String requested, String creatorId, String creatorPostId, String clientName, String clientPlate, String clientModel) {
        this.mypostname = mypostname;
        this.address = address;
        this.time = time;
        this.price = price;
        this.imageUrl = imageUrl;
        this.requested = requested;
        this.creatorId = creatorId;
        this.creatorPostId = creatorPostId;
        this.clientModel = clientModel;
        this.clientName = clientName;
        this.clientPlate = clientPlate;
    }

    //Getters

    public String getmypostname() {
        return mypostname;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public String getRequested() {
        return requested;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorPostId() {
        return creatorPostId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPlate() {
        return clientPlate;
    }

    public String getClientModel() {
        return clientModel;
    }

    //Setters

    public void setmypostname(String mypostname) {
        this.mypostname = mypostname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setRequested(String requested) {
        this.requested = requested;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public void setCreatorPostId(String creatorPostId) {
        this.creatorPostId = creatorPostId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientPlate(String clientPlate) {
        this.clientPlate = clientPlate;
    }

    public void setClientModel(String clientModel) {
        this.clientModel = clientModel;
    }
}
