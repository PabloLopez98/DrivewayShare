package pablo.myexample.drivewayshare;

public class User {

    public String userName;
    public String licensePlate;
    public String carModel;

    //required default constructor
    public User() {
    }

    public User(String userName, String licensePlate, String carModel) {

        this.userName = userName;
        this.licensePlate = licensePlate;
        this.carModel = carModel;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

}