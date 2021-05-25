package org.example;

import javafx.beans.property.SimpleStringProperty;

public class Messenge {
    public SimpleStringProperty PostText;

    public Messenge(String postText) {
        this.PostText = new SimpleStringProperty(postText);
    }
    public Messenge(){
        this.PostText = new SimpleStringProperty("\u0412\u0432\u0435\u0434\u0438\u0442\u0435\u0020\u0442\u0435\u043a\u0441\u0442\u0020\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f");
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
