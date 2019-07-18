package pablo.myexample.drivewayshare;

public class MyConversation {

    public String chat;
    public String myName;

    public MyConversation() {
    }

    public MyConversation(String chat, String myName) {
        this.chat = chat;
        this.myName = myName;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}
