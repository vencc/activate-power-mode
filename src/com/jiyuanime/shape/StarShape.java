package com.jiyuanime.shape;

import java.awt.*;

/**
 * 五角星形状
 *
 * @author vencc
 * @date 2018/3/30
 */
public class StarShape {
    public static Graphics2D getGraphics(Graphics2D g, int x0, int y0, int r, Color c) {
        double ch = 72 * Math.PI / 180;//
        double de = Math.abs(0) * Math.PI / 180;
        //五角星是中心对称图形，角度的实际取值范围在“0——72”之间；
        int x[] = {(int) (x0 + Math.sin(de) * r),
                (int) (x0 - r * Math.sin(ch - de)),
                (int) (x0 + r * Math.cos(de - ch / 4)),
                (int) (x0 - r * Math.sin(ch / 2 + de)),
                (int) (x0 + r * Math.sin(ch / 2 - de)),
        };
        int y[] = {(int) (y0 - r * Math.cos(de)),
                (int) (y0 - r * Math.cos(ch - de)),
                (int) (y0 + r * Math.sin(de - ch / 4)),
                (int) (y0 + r * Math.cos(ch / 2 + de)),
                (int) (y0 + r * Math.cos(ch / 2 - de)),
        };
        int bx = (int) (x0 + r * Math.sin(ch / 2 + de) * Math.cos(ch) / Math.cos(ch / 2));
        int by = (int) (y0 - r * Math.cos(ch / 2 + de) * Math.cos(ch) / Math.cos(ch / 2));

        Polygon a = new Polygon();//凹四边形
        Polygon b = new Polygon();//三角形

        a.addPoint(x[0], y[0]);
        a.addPoint(bx, by);
        a.addPoint(x[2], y[2]);
        a.addPoint(x[3], y[3]);

        b.addPoint(x[1], y[1]);
        b.addPoint(x[4], y[4]);
        b.addPoint(bx, by);

        g.setColor(c);
        g.fillPolygon(a);
        g.fillPolygon(b);
        return g;
    }
}
