<img src=../docs/p1.png width="200"></img> <img src=../docs/p2.png width="200"></img> <img src=../docs/l1.png width="356"></img>

[Download APK](https://github.com/dzaima/APL/releases)

Execute `:h` in the app for a list of commands.

By default, this executes code as dzaima/APL. Other options of interpretation:

```apl
⍝ TryAPL:
:i TryAPL ⍝ requires an internet connection
⍝ Dyalog through RIDE:
$ RIDE_INIT=SERVE:desktopIp:4502 dyalog -nokbd ⍝ on a desktop
:i ride desktopIp ⍝ in the app, after starting the server; another port can be chosen if needed
⍝ for completeness sake, another separate dzaima/APL interpreter can be opened with:
:i dzaima
```


---



Compile for android:

1. generate the dzaima/APL interpreter jar file - run `./convert.py` in `app/`.
2. in PS.pde, change start to `/*`
3. either run in Android Mode with a device connected, or File → Export Signed Package to generate an APK.

Alternatively, Processing runs the project in Java mode just fine (keyboard input may be a bit funky though).

---

```
swipe on characters to enter ones around it
= - evaluate/save
⇧ - shift (caps letters, select with «»)
C - clear input field
M - go to matching bracket

K - open virtual keyboard
# - default layout
A - layout with a-z
N - layout with numbers at top-level
F - extra function layout
  X - close tab (saves where applicable)

↶↷ - undo/redo
▲▼ - move up/down or trough REPL history
^C/^V - copy/paste

:h - list commands (do ":h kb" to view the above text)
```
