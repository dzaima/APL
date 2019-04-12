class Drawable {
  int x, y;
  int w, h;
  boolean visible;
  Drawable(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    screen.add(this);
  }
  void tick() { // only update things that changed. For global things called every frame, even if hidden
    
  }
  void redraw() { // redraw everything. Should be pure and callable at any point any time (where P5 drawing is allowed); called at least on window/screen resizing/rotating
    
  }
  void move(int x, int y) {
    this.x = x;
    this.y = y;
    if (visible) redraw();
  }
  void show() {
    if (!visible) redraw();
    visible = true;
  }
  void hide() {
    visible = false;
  }
  void align(int dir) {
    switch(dir) {
      case BOTTOM: move(x, height-h);
    }
  }
  final void delete() {
    assert screen.remove(this);
    visible = false;
    x = y = w = h = -1111111;
  }
  boolean mouseInMe() {
    return mouseX > x && mouseY > y && mouseX < x+w && mouseY < y+h;
  }
  boolean dragged() {
    return smouseX > x && smouseY > y && smouseX < x+w && smouseY < y+h;
  }
}
