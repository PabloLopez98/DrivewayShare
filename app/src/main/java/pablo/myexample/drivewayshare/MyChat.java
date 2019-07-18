package pablo.myexample.drivewayshare;

public class MyChat {//class for uploading data object to recycler cardview for messages fragment

    String chatName, clientOrNotText, imageUrl;

    public MyChat() {
    }

    public MyChat(String chatName, String imageUrl, String clientOrNotText) {
        this.chatName = chatName;
        this.imageUrl = imageUrl;
        this.clientOrNotText = clientOrNotText;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatImageUrl() {
        return imageUrl;
    }

    public void setChatImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getClientOrNotText() {
        return clientOrNotText;
    }

    public void setClientOrNotText(String clientOrNotText) {
        this.clientOrNotText = clientOrNotText;
    }
}
