package com.readutf.mcmatchmaker.utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {

    public static TextComponent color(String message) {
        return LegacyComponentSerializer.legacy('&').deserialize(message);
    }




}
