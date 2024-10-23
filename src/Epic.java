public class Epic extends Task {

    public Epic(String description, String name) {
        super(description, name);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name.length() + '\'' +
                ", description='" + description.length() + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
