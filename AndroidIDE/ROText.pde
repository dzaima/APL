class ROText extends Drawable {
  float tsz;
  ROText(int x, int y, int w, int h) {
    super(x, y, w, h);
    s = new ArrayList();
    tsz = min(width, height)/20;
  }
  int yoff = 0; // scroll
  int border = 10;
  boolean redraw;
  void redraw() {
    clip(x+border, y, w-border*2, h);
    fill(#101010);
    noStroke();
    rectMode(CORNER);
    rect(x, y, w, h);
    textAlign(LEFT, TOP);
    fill(#D2D2D2);
    stroke(#D2D2D2);
    textSize(tsz);
    line(x, y+h, x+w, y+h);
    int dy = -s.size();
    clip(x+border, y+3, w-border*2, h-6);
    for (String s : s) {
      text(s, x+border, y + dy*tsz + yoff);
      dy++;
    }
    noClip();
    redraw = false;
  }
  void tick() {
    if (!visible) return;
    if (mousePressed && smouseIn()) {
      yoff+= mouseY-pmouseY;
      redraw = true;
      if (yoff < h-border) yoff = h-border;
    }
    
    if (redraw) redraw();
  }
  
  ArrayList<String> s;
  void append(String a) { // append a line
    s.add(a);
    yoff = h-border;
    redraw = true;
  }
  void set(ArrayList<String> a) {
    s = a;
    yoff = h-border;
    redraw = true;
  }
  void setSize(int sz) {
    tsz = sz;
    redraw = true;
  }
  void end() {
    yoff = h-border;
  }
}
