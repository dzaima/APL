<img src=../docs/p1.png width="200"></img> <img src=../docs/p2.png width="200"></img> <img src=../docs/l1.png width="356"></img>

[Download APK](https://github.com/dzaima/APL/releases)

By default this interprets dzaima/APL, but to connect to Dyalog APL (through JSONServer), in Dyalog do:

```apl
)LOAD path/to/JSONServer/Distribution/JSONServer.dws
S←⊃JSONServer.Run ⍬
eval←{⎕←'  ',⍵ ⋄ 0::{⎕←↑⎕DM⋄⎕DM}⍬ ⋄ ,↓⎕←⍕⍎⍵}
```

And connect to it:

```apl
:i dyalog
⍝ and, optionally, if JSONServer wasn't started on localhost with port 8080,
)ip:port
```


---



Compile for android:

1. generate the dzaima/APL interpreter jar file - run `./convert.py` in `app/`.
2. in PS.pde, change start to `/*`
3. Export application in Android mode
4. Use your favorite way to compile the project, changing the min sdk version. For IntelliJ IDEA:
    - open with "use auto-import" checked
    - file → project structure → Project SDK = 1.8
    - sync, build → build project
    - project structure → facets → Android-Gradle (app) → Flavors → Min Sdk version → 24
    - build → generate signed APK → whatever

Alternatively, Processing runs the project in Java mode just fine (keyboard input may be a bit funky though).