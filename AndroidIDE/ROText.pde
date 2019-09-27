class ROText extends Drawable {
  float tsz, chw;
  ROText(int x, int y, int w, int h) {
    super(x, y, w, h);
    s = new ArrayList();
    setSize(min(width, height)/20);
  }
  int xoff = 0; // scroll
  int yoff = 0;
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
    strokeWeight(2);
    textSize(tsz);
    line(x, y+h, x+w, y+h);
    int dy = -s.size();
    clip(x+border, y+3, w-border*2, h-6);
    for (String s : s) {
      text(s, x+border + xoff, y + dy*tsz + yoff);
      dy++;
    }
    noClip();
    redraw = false;
  }
  void tick() {
    if (!visible) return;
    if (mousePressed && smouseIn() && (mouseY!=pmouseY || mouseX!=pmouseX)) {
      redraw = true;
      yoff+= mouseY-pmouseY;
      if (yoff < h-border) yoff = h-border;
      
      
      xoff+= mouseX-pmouseX;
      int max = 0;
      for (String s : s) max = max(max, s.length());
      float maxx = (max - 2)*chw;
      if (xoff < -maxx) xoff = (int) -maxx;
      if (w > (max + 5)*chw) xoff = 0;
      if (xoff > 0) xoff = 0;
    }
    
    if (redraw) redraw();
  }
  
  ArrayList<String> s;
  void append(String a) { // append a line
    s.add(a);
    yoff = h-border;
    xoff = 0;
    redraw = true;
  }
  void set(ArrayList<String> a) {
    s = a;
    yoff = h-border;
    xoff = 0;
    redraw = true;
  }
  void setSize(int sz) {
    tsz = sz;
    textSize(tsz);
    chw = g.textWidth('H');
    redraw = true;
  }
  void end() {
    yoff = h-border;
  }
}
