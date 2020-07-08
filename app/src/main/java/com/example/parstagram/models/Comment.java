package com.example.parstagram.models;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_COMMENTER = "user";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_POST = "post";

    public String getContent() {
        return getString(KEY_CONTENT);
    }

    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    public User getCommenter() {
        return (User) getParseUser(KEY_COMMENTER);
    }

    public void setCommenter(User user) {
        put(KEY_COMMENTER, user);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }


}
