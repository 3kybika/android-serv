package task_manager.server.models;

/**
 * Created by Alex on 19.02.2018.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
//import com.sun.org.apache.xpath.internal.operations.Bool;

data class Task(@JsonProperty("id")
                var id: Int?,
                @JsonProperty("author_id")
                var author_id: Int?,
                @JsonProperty("caption")
                var caption: String?,
                @JsonProperty("about")
                var about: String?,
                @JsonProperty("checked")
                var checked: Boolean?) {
    constructor(author_id: Int?, caption: String?, about: String?, checked: Boolean?)
            : this(0, author_id, caption, about, checked)
}
