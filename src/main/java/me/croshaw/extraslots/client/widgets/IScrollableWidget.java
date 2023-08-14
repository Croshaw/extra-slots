package me.croshaw.extraslots.client.widgets;

public interface IScrollableWidget {
    void extraSlots$refreshWidget();
    void extraSlots$updateWidget(int x, int y);
    boolean extraSlots$scroll(double mouseX, double mouseY, double amount);
}
