abstract static class Plane extends Drawable {
  
  double fullX;
  double fullY;
  int freq = 20;
  double fullS;
  double lastDist; // ==0 => first multitouch frame
  double lastX, lastY;
  Plane(int x, int y, int w, int h) {
    super(x, y, w, h);
    fullS = 10;
    fullX = (x+w/2) /-fullS;
    fullY = (y+h/2) /-fullS;
  }
  
  abstract void draw();
  
  final void tick() {
    if (!visible) return;
    if (smouseY > y && smouseY < y+h) {
      if (a.touches.length == 2) {
        float ow = dist(a.touches[0].x, a.touches[0].y, a.touches[1].x, a.touches[1].y);
        double sc = ow/lastDist;
        double avgX = (a.touches[0].x + a.touches[1].x) / 2f;
        double avgY = (a.touches[0].y + a.touches[1].y) / 2f;
        if (lastDist != 0) {
          double pS = fullS;
          fullS*= sc;
          double scalechange = 1/fullS - 1/pS;
          fullX-= (avgX * scalechange);
          fullY-= (avgY * scalechange);
          fullX+= (lastX-avgX)/fullS;
          fullY+= (lastY-avgY)/fullS;
        }
        lastX = avgX;
        lastY = avgY;
        lastDist = ow;
      } else {
        lastDist = 0;
        if (a.touches.length == 1) {
          fullX += (a.pmouseX - a.mouseX)/fullS;
          fullY += (a.pmouseY - a.mouseY)/fullS;
        }
      }
    } else lastDist = 0;
    d.pushMatrix();
    beginClip(d, x, y, w, h);
    d.imageMode(CORNER);
    d.background(12);
    
    d.stroke(0xff666666);
    d.strokeWeight(1);
    
    
      
    double sx = fullX;
    double sy = fullY;
    
    double ex = fullX + a.width/fullS;
    double ey = fullY + a.height/fullS;
    double rsz = Math.log((ex-sx)/freq)/Math.log(10);
    double s1 = Math.pow(10, Math.floor(rsz));
    if (s1 == 0) { // prevent an infinite loop due to too much lost precision
      fullS/= 1.1;
      d.popMatrix();
      return;
    }
    double s2 = s1;
    double m1 = rsz % 1;
    if (m1 < 0) m1+= 1;
    if (m1 > .6)      { s1*= 5; s2*= 20; }
    else if (m1 > .3) { s1*= 2; s2*= 10; }
    else              { s1*= 1; s2*=  5; }
    
    d.textAlign(LEFT, BOTTOM);
    d.fill(0xffd2d2d2);
    float ts = max(d.width, d.height)/70f;
    d.textSize(ts);
    
    DecimalFormat df = new DecimalFormat("#.0");
    int dgs = (int) Math.ceil(Math.log(1/s2)/Math.log(10));
    df.setMaximumFractionDigits(dgs);
    df.setMinimumFractionDigits(dgs);
    
    float off = ts*.4;
    float ty = constrain(realY(0), off+y+ts, y+h-off);
    d.textAlign(LEFT, BOTTOM);
    for (double cx = Math.floor(sx/s1) * s1; cx < ex; cx+= s1) {
      if (cx + s1 == cx) { // infinite loop ↑
        fullS/= 1.1;
        d.popMatrix();
        return;
      }
      
      boolean big = Math.abs(mod(cx, s2)/s2-.5)>0.49;
      d.stroke(big? 0xff666666 : 0x40666666);
      int rx = realX(cx);
      d.line(rx, y, rx, y+h);
      if (big) d.text(df.format(cx).replaceAll("^-?\\.?0+$", "0"), rx, ty);
    }
    
    float tx = constrain(realX(0), x+off,
      x+w-off-max(
        d.textWidth(df.format(Math.floor(sy/s1) * s1)),
        d.textWidth(df.format(ey))
      )
    );
    
    for (double cy = Math.floor(sy/s1) * s1; cy < ey; cy+= s1) {
      if (cy + s1 == cy) { // infinite loop ↑
        fullS/= 1.1;
        d.popMatrix();
        return;
      }
      boolean big = Math.abs(mod(cy, s2)/s2-.5)>0.49;
      d.stroke(big? 0xff666666 : 0x40666666);
      int ry = realY(cy);
      d.line(x, ry, x+w, ry);
      if (big) d.text(df.format(-cy).replaceAll("^-?\\.?0+$", "0"), tx, ry);
    }
    
    draw();
    
    endClip(d);
    d.popMatrix();
  }
  
  //translate((int)(-fullX * fullS), (int)(-fullY * fullS));
  //scale((float)fullS);
  int realX(double x) {
    return (int) ((x - fullX)*fullS);
  }
  int realY(double y) {
    return (int) ((y - fullY)*fullS);
  }
}

static double mod(double a, double b) {
  a%= b;
  if (a < 0) a+= b;
  return a;
}
