package com.jiyuanime.shape;

import java.awt.*;

/**
 * 粗圆
 *
 * @author vencc
 * @date 2018/3/30
 */
public class RoughRoundShape {
    public static Graphics2D getGraphics(Graphics2D g,int x,int y,int size,Color c){
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(c);
        g.fillOval(x, y, size, size);
        return g;
    }
}
