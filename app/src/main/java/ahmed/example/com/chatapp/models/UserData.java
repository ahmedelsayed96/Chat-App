package ahmed.example.com.chatapp.models;

/**
 * Created by root on 04/10/17.
 */

public class UserData {

    private String name;
    private String email;
    private String phone;
    private String image;
    private String age;
    private String uid;

    public UserData() {
    }

    public UserData(String name, String EMail, String phone, String image, String age) {
        this.name = name;
        this.email = EMail;
        this.phone = phone;
        this.image = image;
        this.age = age;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEMail() {
        return email;
    }

    public void setEMail(String EMail) {
        this.email = EMail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
