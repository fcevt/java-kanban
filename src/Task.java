import java.util.Objects;

public class Task {
    String name;
    String description;
    int id;
    TaskStatus status;


    public Task(String description, String name) {
        this.status = TaskStatus.NEW;
        this.id = 0;
        this.description = description;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name.length() + '\'' +
                ", description='" + description.length() + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
