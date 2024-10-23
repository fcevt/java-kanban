public class Subtask extends Task{
    int epicId;


    public Subtask(String description, String name) {
        super(description, name);
        this.epicId = 0;

    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name.length() + '\'' +
                ", description='" + description.length() + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }



    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


}
