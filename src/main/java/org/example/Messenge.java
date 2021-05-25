package org.example;

import javafx.beans.property.SimpleStringProperty;

public class Messenge {
    public SimpleStringProperty PostText;

    public Messenge(String postText) {
        this.PostText = new SimpleStringProperty(postText);
    }
    public Messenge(){
        this.PostText = new SimpleStringProperty("Введите текст сообщения");
    }

    public String getPostText() {
        return PostText.get();
    }

    public SimpleStringProperty postTextProperty() {
        return PostText;
    }

    public void setPostText(String postText) {
        this.PostText.set(postText);
    }
}
