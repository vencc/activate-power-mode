package com.jiyuanime.shape;

import java.awt.*;

/**
 * 精圆
 *
 * @author vencc
 * @date 2018/3/30
 */
public class RoundShape{
    public static Graphics2D getGraphics(Graphics2D g,int x,int y,int size,Color c){
        g.setColor(c);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillOval(x, y, size, size);
        return g;
    }
}
