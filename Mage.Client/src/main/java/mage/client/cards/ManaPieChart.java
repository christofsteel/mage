package mage.client.cards;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JComponent;

class Slice {

    double value;
    Color color;

    public Slice(double value, Color color) {
        this.value = value;
        this.color = color;
    }
}

public class ManaPieChart extends JComponent {

    ArrayList<Slice> slices = new ArrayList<Slice>();

    ManaPieChart() {
    }

    ManaPieChart(Integer w, Integer u, Integer b, Integer r, Integer g, Integer c) {
        if (w != null && w > 0) {
            slices.add(new Slice(w, Color.WHITE));
        }
        if (u != null && u > 0) {
            slices.add(new Slice(u, Color.BLUE));
        }
        if (b != null && b > 0) {
            slices.add(new Slice(b, Color.BLACK));
        }
        if (r != null && r > 0) {
            slices.add(new Slice(r, Color.RED));
        }
        if (g != null && g > 0) {
            slices.add(new Slice(g, Color.GREEN));
        }
        if (c != null && c > 0) {
            slices.add(new Slice(c, Color.LIGHT_GRAY));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferred = super.getPreferredSize();
        Dimension minimum = getMinimumSize();
        Dimension maximum = getMaximumSize();
        preferred.width = Math.min(Math.max(preferred.width, minimum.width), maximum.width);
        preferred.height = Math.min(Math.max(preferred.height, minimum.height), maximum.height);
        return preferred;
    }

    public void paint(Graphics g) {
        drawPie((Graphics2D) g, getBounds(), slices.toArray(new Slice[slices.size()]));
    }

    void drawPie(Graphics2D g, Rectangle area, Slice[] slices) {
        double total = 0.0D;
        for (int i = 0; i < slices.length; i++) {
            total += slices[i].value;
        }

        double curValue = 0.0D;
        int startAngle = 0;
        int lastAngle = 0;
        for (int i = 0; i < slices.length; i++) {
            startAngle = lastAngle;
            int arcAngle = (int) (slices[i].value * 360 / total);

            g.setColor(slices[i].color);
            g.fillArc(area.x, area.y, area.width - 20, area.height - 20, startAngle, arcAngle);
            curValue += slices[i].value;
            lastAngle += arcAngle;
        }
    }
}
