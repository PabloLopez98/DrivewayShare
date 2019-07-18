package pablo.myexample.drivewayshare;

public class MarkerTag {

    public String creatorId;
    public String creatorPostId;
    public String reservedOrNot;

    public MarkerTag() {
        //required default constructor
    }

    public MarkerTag(String creatorId, String creatorPostId, String reservedOrNot) {

        this.creatorId = creatorId;
        this.creatorPostId = creatorPostId;
        this.reservedOrNot = reservedOrNot;

    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorPostId() {
        return creatorPostId;
    }

    public void setCreatorPostId(String creatorPostId) {
        this.creatorPostId = creatorPostId;
    }

    public String getReservedOrNot() {
        return reservedOrNot;
    }

    public void setReservedOrNot(String reservedOrNot) {
        this.reservedOrNot = reservedOrNot;
    }
}
