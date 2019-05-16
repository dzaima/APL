abstract class Plane extends Drawable {
  
  double scale = 1;
  double fullX;
  double fullY;
  double fullS;
  double lastDist; // old ow?
  Plane(int x, int y, int w, int h) {
    super(x, y, w, h);
    fullS = 10;
    fullX = width/2 /-fullS;
    fullY = (y+h/2) /-fullS;
    println(fullX, fullY, fullS);
  }
  
  abstract void draw();
  
  final void tick() {
    if (!visible) return;
    if (smouseY > y && smouseY < y+h) {
      if (touches.length == 2) {
        float ow = dist(touches[0].x, touches[0].y, touches[1].x, touches[1].y);
        double sc = ow/lastDist;
        if (lastDist != 0) {
          double avgX = (touches[0].x + touches[1].x) / 2f;
          double avgY = (touches[0].y + touches[1].y) / 2f;
          double pS = fullS;
          fullS*= sc;
          double scalechange = 1/fullS - 1/pS;
          fullX-= (avgX * scalechange);
          fullY-= (avgY * scalechange);
        }
        lastDist = ow;
      } else {
        lastDist = 0;
        if (touches.length == 1) {
          fullX += (pmouseX - mouseX)/fullS;
          fullY += (pmouseY - mouseY)/fullS;
        }
      }
    } else lastDist = 0;
    pushMatrix();
    imageMode(CORNER);
    clip(x, y, w, h);
    background(12);
    
    stroke(0xff666666);
    strokeWeight(1);
    
    
    int freq = 10;
      
    double sx = (fullX/scale);
    double sy = (fullY/scale);
    
    double ex = ((fullX + width/fullS)/scale);
    double ey = ((fullY + height/fullS)/scale);
    
    double rsz = Math.log((ex-sx)/freq)/Math.log(10);
    double sz = Math.pow(10, Math.floor(rsz));
    if (sz == 0) { // infinite loop
      fullS/= 1.1;
      popMatrix();
      return;
    }
    double m1 = rsz % 1;
    if (m1 < 0) m1+= 1;
    if (m1 > .6) sz*= 5;
    else if (m1 > .3) sz*= 2;
    
    textAlign(LEFT, BOTTOM);
    fill(0xffd2d2d2);
    float ts = max(width, height)/70f;
    textSize(ts);
    
    DecimalFormat df = new DecimalFormat("#.0");
    int dgs = (int) Math.ceil(Math.log(1/sz)/Math.log(10));
    df.setMaximumFractionDigits(dgs);
    df.setMinimumFractionDigits(dgs);
    
    
    float off = ts*.4;
    float ty = constrain(realY(0), off+y+ts, y+h-off);
    textAlign(LEFT, BOTTOM);
    for (double cx = Math.floor(sx/sz) * sz; cx < ex; cx+= sz) {
      if (cx + sz == cx) { // infinite loop
        fullS/= 1.1;
        popMatrix();
        return;
      }
      int rx = realX(cx);
      line(rx, y, rx, y+h);
      text(df.format(cx).replaceAll("^-?\\.?0+$", "0"), rx, ty);
    }
    
    float tx = constrain(realX(0), x+off,
      x+w-off-max(
        textWidth(df.format(Math.floor(sy/sz) * sz)),
        textWidth(df.format(ey))
      )
    );
    
    for (double cy = Math.floor(sy/sz) * sz; cy < ey; cy+= sz) {
      if (cy + sz == cy) { // infinite loop
        fullS/= 1.1;
        popMatrix();
        return;
      }
      int ry = realY(cy);
      line(x, ry, x+w, ry);
      text(df.format(-cy).replaceAll("^-?\\.?0+$", "0"), tx, ry);
    }
    
    draw();
    
    noClip();
    popMatrix();
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
