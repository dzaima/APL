class TopBar extends Drawable {
  Tab ctab;
  ArrayList<Tab> tabs = new ArrayList();
  TopBar(int x, int y, int w, int h) {
    super(x, y, w, h);
  }
  void tick() {
    if (smouseIn() && mousePressed && !pmousePressed) {
      textSize(h*.8);
      int cx = x;
      for (Tab t : tabs) {
        String n = t.name();
        int dx = ceil(textWidth(n)) + h/2;
        if (mouseX > cx && mouseX < cx + dx) to(t);
        cx+= dx;
        redraw();
      }
    }
  }
  void redraw() {
    textSize(h*.8);
    if (!visible) return;
    rectMode(CORNER);
    fill(#222222);
    noStroke();
    rect(x, y, w, h);
    
    int cx = x;
    for (Tab t : tabs) {
      String n = t.name();
      int dx = ceil(textWidth(n)) + h/2;
      if (t == ctab) {
        fill(#333333);
        rect(cx, y, dx, h);
      }
      cx+= dx;
    }
    
    fill(#D2D2D2);
    textAlign(CENTER, CENTER);
    cx = x;
    for (Tab t : tabs) {
      String n = t.name();
      int dx = ceil(textWidth(n)) + h/2;
      text(n, cx + dx/2, y + h/2);
      cx+= dx;
    }
  }
  void to(Tab t) {
    if (ctab != null) ctab.hide();
    ctab = t;
    ctab.show();
  }
  void toNew(Tab t) {
    add(t);
    to(t);
  }
  void add(Tab t) {
    tabs.add(t);
  }
  void resized() {
    if (ctab != null) ctab.show();
  }
}
