static class TopBar extends Drawable {
  Tab ctab;
  ArrayList<Tab> tabs = new ArrayList();
  TopBar(int x, int y, int w, int h) {
    super(x, y, w, h);
  }
  void tick() {
    if (smouseIn() && a.mousePressed && !pmousePressed) {
      d.textSize(h*.8);
      int cx = x;
      for (Tab t : tabs) {
        String n = t.name();
        int dx = max(2*h, ceil(d.textWidth(n)) + h/2);
        if (a.mouseX > cx && a.mouseX < cx + dx) to(t);
        cx+= dx;
        redraw();
      }
    }
  }
  void redraw() {
    d.textSize(h*.8);
    if (!visible) return;
    d.rectMode(CORNER);
    d.fill(#222222);
    d.noStroke();
    d.rect(x, y, w, h);
    
    int cx = x;
    for (Tab t : tabs) {
      String n = t.name();
      int dx = max(2*h, ceil(d.textWidth(n)) + h/2);
      if (t == ctab) {
        d.fill(#333333);
        d.rect(cx, y, dx, h);
      }
      cx+= dx;
    }
    
    d.fill(#D2D2D2);
    d.textAlign(CENTER, CENTER);
    cx = x;
    for (Tab t : tabs) {
      String n = t.name();
      int dx = max(2*h, ceil(d.textWidth(n)) + h/2);
      d.text(n, cx + dx/2, y + h*.4);
      cx+= dx;
    }
  }
  void to(Tab t) {
    if (ctab != null) ctab.hide();
    ctab = t;
    ctab.show();
    redraw();
  }
  void move(int d) {
    int i = tabs.indexOf(ctab) + d;
    i%= tabs.size();
    if (i < 0) i+= tabs.size();
    to(tabs.get(i));
  }
  void toNew(Tab t) {
    add(t);
    to(t);
  }
  void add(Tab t) {
    tabs.add(t);
    redraw();
  }
  void resized() {
    if (ctab != null) ctab.show();
  }
  void close() {
    if (tabs.size() == 1) return;
    int i = tabs.indexOf(ctab);
    tabs.remove(i);
    i--;
    if (i < 0) i = 0;
    to(tabs.get(i));
  }
}
