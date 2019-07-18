package pablo.myexample.drivewayshare;

public class MyReservation {

    public String reservationhostname;
    public String reservationaddress;
    public String reservationtime;
    public String reservationprice;
    public String reservationImageUrl;
    public String myreservationrequested;
    public String reservationname;
    public String reservationPlate;
    public String reservationModel;
    public String reservationhostid;
    public String reservationhostpostid;

    public MyReservation() {
        //required default constructor
    }

    public MyReservation(String reservationname, String reservationPlate, String reservationModel, String reservationaddress, String reservationtime, String reservationprice, String reservationImageUrl, String reservationhostname, String myreservationrequested, String reservationhostid, String reservationhostpostid) {
        this.reservationhostname = reservationhostname;
        this.reservationaddress = reservationaddress;
        this.reservationtime = reservationtime;
        this.reservationprice = reservationprice;
        this.reservationImageUrl = reservationImageUrl;
        this.reservationname = reservationname;
        this.reservationPlate = reservationPlate;
        this.reservationModel = reservationModel;
        this.myreservationrequested = myreservationrequested;
        this.reservationhostid = reservationhostid;
        this.reservationhostpostid = reservationhostpostid;
    }

    public String getReservationhostname() {
        return reservationhostname;
    }

    public void setReservationhostname(String reservationhostname) {
        this.reservationhostname = reservationhostname;
    }

    public String getReservationaddress() {
        return reservationaddress;
    }

    public void setReservationaddress(String reservationaddress) {
        this.reservationaddress = reservationaddress;
    }

    public String getReservationtime() {
        return reservationtime;
    }

    public void setReservationtime(String reservationtime) {
        this.reservationtime = reservationtime;
    }

    public String getReservationprice() {
        return reservationprice;
    }

    public void setReservationprice(String reservationprice) {
        this.reservationprice = reservationprice;
    }

    public String getReservationImageUrl() {
        return reservationImageUrl;
    }

    public void setReservationImageUrl(String reservationImageUrl) {
        this.reservationImageUrl = reservationImageUrl;
    }

    public String getMyreservationrequested() {
        return myreservationrequested;
    }

    public void setMyreservationrequested(String myreservationrequested) {
        this.myreservationrequested = myreservationrequested;
    }

    public String getReservationname() {
        return reservationname;
    }

    public void setReservationname(String reservationname) {
        this.reservationname = reservationname;
    }

    public String getReservationPlate() {
        return reservationPlate;
    }

    public void setReservationPlate(String reservationPlate) {
        this.reservationPlate = reservationPlate;
    }

    public String getReservationModel() {
        return reservationModel;
    }

    public void setReservationModel(String reservationModel) {
        this.reservationModel = reservationModel;
    }

    public String getReservationhostid() {
        return reservationhostid;
    }

    public void setReservationhostid(String reservationhostid) {
        this.reservationhostid = reservationhostid;
    }

    public String getReservationhostpostid() {
        return reservationhostpostid;
    }

    public void setReservationhostpostid(String reservationhostpostid) {
        this.reservationhostpostid = reservationhostpostid;
    }
}