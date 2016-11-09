/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdf.drop.application;

import javafx.scene.image.Image;

public class AppImageLoader {

    public Image getImage(String format) {
        Image image = new Image(getClass().getResourceAsStream(
                "/images/" + format + ".png"));
        return image;
    }
    
}
