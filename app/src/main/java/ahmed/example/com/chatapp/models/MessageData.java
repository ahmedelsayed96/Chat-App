package ahmed.example.com.chatapp.models;

/**
 * Created by root on 05/10/17.
 */

public class MessageData {

    String id;
    private String type;
    private long date;
    private String message;
    private String image;
    private String author;

    public MessageData() {
    }

    public MessageData(String type, long date, String message, String image, String author) {
        this.type = type;
        this.date = date;
        this.message = message;
        this.image = image;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
