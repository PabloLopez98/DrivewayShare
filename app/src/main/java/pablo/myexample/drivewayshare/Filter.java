package pablo.myexample.drivewayshare;

public class Filter {

    public String startingHour;
    public String maxPrice;

    //required empty constructor
    public Filter() {
    }

    public Filter(String startingHour, String maxPrice) {

        this.startingHour = startingHour;
        this.maxPrice = maxPrice;

    }

    public String getStartingHour() {
        return startingHour;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setStartingHour(String startingHour) {
        this.startingHour = startingHour;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }
}
