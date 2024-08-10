package com.audition.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class AuditionPost {

    @JsonProperty("userId")
    private int userId;
    private int id;
    private String title;
    private String body;
    private List<Comments> comments;

    @JsonProperty("comments")
    public List<Comments> getComments() {
        return comments != null && !comments.isEmpty() ? comments : null;
    }
}
