class ROText extends Drawable {
  float tsz;
  ROText(int x, int y, int w, int h) {
    super(x, y, w, h);
    s = new ArrayList();
    show();
  }
  int yoff = 0; // scroll
  int border = 10;
  void redraw() {
    tsz = min(width, height)/20;
    yoff = h-border;
  }
  void tick() {
    clip(x+border, y, w-border*2, h);
    if (mousePressed && dragged()) {
      yoff+= mouseY-pmouseY;
      if (yoff < h-border) yoff = h-border;
    }
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
    for (String s : s) {
      text(s, x+border, y + dy*tsz + yoff);
      dy++;
    }
    noClip();
  }
  
  ArrayList<String> s;
  void append(String a) { // append a line
    s.add(a);
    yoff = h-border;
  }
  void set(ArrayList<String> a) {
    s = a;
    yoff = h-border;
  }
  void setSize(int sz) {
    tsz = sz;
  }
}
