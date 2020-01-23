abstract static class Plane extends Drawable {
  
  double scale = 1;
  double fullX;
  double fullY;
  double fullS;
  double lastDist; // ==0 => first multitouch frame
  double lastX, lastY;
  Plane(int x, int y, int w, int h) {
    super(x, y, w, h);
    fullS = 10;
    fullX = a.width/2 /-fullS;
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
    
    
    int freq = 10;
      
    double sx = (fullX/scale);
    double sy = (fullY/scale);
    
    double ex = ((fullX + a.width/fullS)/scale);
    double ey = ((fullY + a.height/fullS)/scale);
    
    double rsz = Math.log((ex-sx)/freq)/Math.log(10);
    double sz = Math.pow(10, Math.floor(rsz));
    if (sz == 0) { // infinite loop
      fullS/= 1.1;
      d.popMatrix();
      return;
    }
    double m1 = rsz % 1;
    if (m1 < 0) m1+= 1;
    if (m1 > .6) sz*= 5;
    else if (m1 > .3) sz*= 2;
    
    d.textAlign(LEFT, BOTTOM);
    d.fill(0xffd2d2d2);
    float ts = max(d.width, d.height)/70f;
    d.textSize(ts);
    
    DecimalFormat df = new DecimalFormat("#.0");
    int dgs = (int) Math.ceil(Math.log(1/sz)/Math.log(10));
    df.setMaximumFractionDigits(dgs);
    df.setMinimumFractionDigits(dgs);
    
    
    float off = ts*.4;
    float ty = constrain(realY(0), off+y+ts, y+h-off);
    d.textAlign(LEFT, BOTTOM);
    for (double cx = Math.floor(sx/sz) * sz; cx < ex; cx+= sz) {
      if (cx + sz == cx) { // infinite loop
        fullS/= 1.1;
        d.popMatrix();
        return;
      }
      int rx = realX(cx);
      d.line(rx, y, rx, y+h);
      d.text(df.format(cx).replaceAll("^-?\\.?0+$", "0"), rx, ty);
    }
    
    float tx = constrain(realX(0), x+off,
      x+w-off-max(
        d.textWidth(df.format(Math.floor(sy/sz) * sz)),
        d.textWidth(df.format(ey))
      )
    );
    
    for (double cy = Math.floor(sy/sz) * sz; cy < ey; cy+= sz) {
      if (cy + sz == cy) { // infinite loop
        fullS/= 1.1;
        d.popMatrix();
        return;
      }
      int ry = realY(cy);
      d.line(x, ry, x+w, ry);
      d.text(df.format(-cy).replaceAll("^-?\\.?0+$", "0"), tx, ry);
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
