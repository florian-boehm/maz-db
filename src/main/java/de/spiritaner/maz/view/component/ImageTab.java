package de.spiritaner.maz.view.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

/**
 * Created by florian on 2/21/17.
 */
public class ImageTab extends Tab {

    private StringProperty img;
    private StringProperty name;
    private StringProperty url;

    public ImageTab() {
        img = new SimpleStringProperty();
        img.addListener((observableValue, oldValue, newValue) -> {
            super.setGraphic(new ImageView(getImg()));
        });

        name = new SimpleStringProperty();
        name.addListener((observableValue, oldValue, newValue) -> {
            super.setTooltip(new Tooltip(name.get()));
        });

        url = new SimpleStringProperty();

        this.getStyleClass().add("img-tab");
    }

    public String getImg() {
        return img.get();
    }

    public StringProperty imgProperty() {
        return img;
    }

    public void setImg(String img) {
        this.img.set(img);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
}
