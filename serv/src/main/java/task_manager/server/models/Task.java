package task_manager.server.models;

/**
 * Created by Alex on 19.02.2018.
 */
import com.fasterxml.jackson.annotation.JsonProperty;


public class Task {
    @JsonProperty("id")
    private int id;
    @JsonProperty("author_id")
    private int author_id;
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("about")
    private String about;

    public Task(int id, int author_id, String caption, String about) {
        this.id = id;
        this.author_id = author_id;
        this.caption = caption;
        this.about = about;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorId() {
        return author_id;
    }

    public void setAuthorId(int id) {
        this.author_id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
