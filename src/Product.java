import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int NAME_LENGTH = 35;
    private static final int DESCRIPTION_LENGTH = 75;
    private static final int ID_LENGTH = 6;

    private String ID;          // ID should never change
    private String name;
    private String description;
    private double cost;

    /**
     * Full constructor for creating a Product
     * @param ID The product's ID
     * @param name The product's name
     * @param description The product's description
     * @param cost The product's cost
     */
    public Product(String ID, String name, String description, double cost) {
        // Validate input lengths
        if (ID.length() != ID_LENGTH) {
            throw new IllegalArgumentException("ID must be exactly " + ID_LENGTH + " characters");
        }
        this.ID = ID;
        this.name = formatString(name, NAME_LENGTH);
        this.description = formatString(description, DESCRIPTION_LENGTH);
        setCost(cost);
    }

    /**
     * Default constructor
     */
    public Product() {
        this.ID = "";
        this.name = "";
        this.description = "";
        this.cost = 0.0;
    }

    // Getters
    public String getID() { return ID; }
    public String getName() { return name.trim(); }
    public String getDescription() { return description.trim(); }
    public double getCost() { return cost; }

    // Setters (except for ID which should be immutable)
    public void setName(String name) { this.name = formatString(name, NAME_LENGTH); }
    public void setDescription(String description) { this.description = formatString(description, DESCRIPTION_LENGTH); }
    public void setCost(double cost) {
        if (cost >= 0) {
            this.cost = cost;
        } else {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
    }

    // Formats string to fixed length by padding with spaces
    private String formatString(String str, int length) {
        if (str == null) str = "";
        return String.format("%-" + length + "s", str.substring(0, Math.min(str.length(), length)));
    }

    /**
     * Converts product data to CSV format
     * @return CSV formatted string of product data
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%.2f",
                ID, name.trim(), description.trim(), cost);
    }

    /**
     * Converts product data to JSON format
     * @return JSON formatted string of product data
     */
    public String toJSON() {
        return String.format(
                "{\"ID\":\"%s\",\"name\":\"%s\",\"description\":\"%s\",\"cost\":%.2f}",
                ID, name.trim(), description.trim(), cost);
    }

    /**
     * Converts product data to XML format
     * @return XML formatted string of product data
     */
    public String toXML() {
        return String.format(
                "  <product>\n" +
                        "    <ID>%s</ID>\n" +
                        "    <name>%s</name>\n" +
                        "    <description>%s</description>\n" +
                        "    <cost>%.2f</cost>\n" +
                        "  </product>",
                ID, name.trim(), description.trim(), cost);
    }

    /**
     * Formats the product information for display
     * @return Formatted string representation of the product
     */
    @Override
    public String toString() {
        return String.format("Product{ID='%s', name='%s', description='%s', cost=%.2f}",
                ID, name.trim(), description.trim(), cost);
    }

    /**
     * Checks if two products are equal
     * @param o The object to compare with
     * @return true if the products are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.cost, cost) == 0 &&
                Objects.equals(ID, product.ID) &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description);
    }

    /**
     * Generates a hash code for the product
     * @return hash code value for the product
     */
    @Override
    public int hashCode() {
        return Objects.hash(ID, name, description, cost);
    }
}  