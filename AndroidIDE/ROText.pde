static class ROText extends Drawable {
  float tsz, chw;
  ROText(int x, int y, int w, int h) {
    super(x, y, w, h);
    s = new ArrayList();
    setSize(min(d.width, d.height)/20);
  }
  int xoff = 0; // scroll
  int yoff = 0;
  int border = 10;
  boolean redraw;
  void redraw() {
    beginClip(d, x+border, y, w-border*2, h);
    d.fill(#101010);
    d.noStroke();
    d.rectMode(CORNER);
    d.rect(x, y, w, h);
    d.textAlign(LEFT, TOP);
    d.fill(#D2D2D2);
    d.stroke(#D2D2D2);
    d.strokeWeight(2);
    d.textSize(tsz);
    d.line(x, y+h, x+w, y+h);
    int dy = -s.size();
    d.clip(x+border, y+3, w-border*2, h-6);
    for (String s : s) {
      d.text(s, x+border + xoff, y + dy*tsz + yoff);
      dy++;
    }
    endClip(d);
    redraw = false;
  }
  void tick() {
    if (!visible) return;
    if (a.mousePressed && smouseIn() && (a.mouseY!=a.pmouseY || a.mouseX!=a.pmouseX)) {
      redraw = true;
      yoff+= a.mouseY-a.pmouseY;
      if (yoff < h-border) yoff = h-border;
      
      
      xoff+= a.mouseX-a.pmouseX;
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
  boolean newline;
  void appendLns(String a) {
    String[] lns = split(a, "\n");
    if (newline) {
      for (String s : lns) append(s);
    } else {
      if (s.size()==0) s.add("");
      s.set(s.size()-1, s.get(s.size()-1)+lns[0]);
      for (int i = 1; i < lns.length; i++) append(lns[i]);
    }
    newline = a.endsWith("\n");
    if (newline)s.remove(s.size()-1);
  }
  void append(String a) { // append a line
    s.add(a);
    yoff = h-border;
    xoff = 0;
    newline = true;
    redraw = true;
  }
  void set(ArrayList<String> a) {
    s = a;
    yoff = h-border;
    xoff = 0;
    newline = true;
    redraw = true;
  }
  void setSize(int sz) {
    tsz = sz;
    a.textSize(tsz);
    chw = d.textWidth('H');
    redraw = true;
  }
  void end() {
    yoff = h-border;
  }
}
