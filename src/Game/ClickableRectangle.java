package Game;

import processing.core.PApplet;

public class ClickableRectangle {
    int x, y, width, height;
    String text = "";

    ClickableRectangle() {}

    ClickableRectangle(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    boolean isClicked(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void draw(PApplet app) {
        app.fill(200);
        app.rect(x, y, width, height);
        app.fill(0);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(text, x + width /2, y + height/2);
    }
}