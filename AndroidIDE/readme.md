To generate the dzaima/APL interpreter jar file, run `./convert.py` in `app/`.

Start Dyalog JSONServer:

```apl
)LOAD path/to/JSONServer/Distribution/JSONServer.dws
S←⊃JSONServer.Run ⍬
eval←{⎕←'  ',⍵ ⋄ 0::{⎕←↑⎕DM⋄⎕DM}⍬ ⋄ ,↓⎕←⍕⍎⍵}
```

Connect to it:

```apl
:i dyalog
⍝ and, optionally, if JSONServer wasn't started on localhost with port 8080,
)ip:port
```

---

Compile for android:

1. in PS.pde, change start to `/*`
2. Export application in Android mode
3. Use your favorite way to compile the project, changing the min sdk version. For IntelliJ IDEA:
    - open with "use auto-import" checked
    - file → project structure → Project SDK = 1.8
    - sync, build → build project
    - project structure → facets → Android-Gradle (app) → Flavors → Min Sdk version → 24
    - build → generate signed APK → whatever