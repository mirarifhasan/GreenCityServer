package hsquad.greencityserver.Model;

public class PlantService {

    private String name, description, image, price, discount, menuID;

    public PlantService() {
    }

    public PlantService(String description, String discount, String image, String menuID, String name, String price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.discount = discount;
        this.menuID = menuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }
}
