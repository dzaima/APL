// example class for an APL object
class Example extends SimpleMap {
  void setv(String k, Obj v) {
    String s = k.toLowerCase();
    switch (s) {
      default: throw new DomainError("setting non-existing key "+s+" for ______");
    }
  }
  Obj getv(String k) {
    String s = k.toLowerCase();
    switch (s) {
      default: return NULL;
    }
  }
  String name() { return "______"; }
}
