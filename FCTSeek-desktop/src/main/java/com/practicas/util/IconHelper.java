/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.practicas.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.HashMap;
import java.util.Map;

public class IconHelper {

    private static final Map<String, Image> cache = new HashMap<>();

    static {
        String[] imagenes = {
            "ic_home.png", "ic_search.png", "ic_list.png", "ic_profile.png",
            "ic_email.png", "ic_user.png", "ic_phone.png", "ic_location.png",
            "ic_heart_full.png", "ic_heart_empty.png",
            "ic_star_empty.png", "ic_star_full.png", "ic_building.png"
        };
        for (String nombre : imagenes) {
            try {
                var stream = IconHelper.class.getResourceAsStream("/images/" + nombre);
                if (stream != null) {
                    cache.put(nombre, new Image(stream));
                }
            } catch (Exception ignored) {}
        }
    }

    public static ImageView get(String nombre, int size) {
        ImageView iv = new ImageView();
        Image img = cache.get(nombre);
        if (img != null) iv.setImage(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        return iv;
    }

    public static Image getImage(String nombre) {
        return cache.get(nombre);
    }
}