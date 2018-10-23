// example class for an APL object
class Example extends APLMap {
  Arr toArr() { throw new SyntaxError("Converting a ______ object to array"); }
  void set(Value k, Obj v) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      default: throw new DomainError("setting non-existing key "+s+" for ______");
    }
  }
  Obj getRaw(Value k) {
    String s = k.fromAPL().toLowerCase();
    switch (s) {
      default: return NULL;
    }
  }
  int size() { throw new SyntaxError("Getting size of the ______ object"); }
  String toString() { return "______"; }
}
