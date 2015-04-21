# Introduction #

easyb supports the notion of _narratives_, which attempt to set the stage of a story. Narratives use a `narrative` clause followed by a series of descriptors (like `as a`, `i want`, and `so that`) that can either be written with underscores or not.


For example, as demonstrated in [JavaWorld's "Behavior-driven development with easyb"](http://www.javaworld.com/javaworld/jw-09-2008/jw-09-easyb.html), a `narrative` has the following format:
```
narrative 'segment flown', {
     as_a 'frequent flyer'
     i_want 'to accrue rewards points for every segment I fly'
     so_that 'I can receive free flights for my dedication to the airline'
 }
```

note how underscores are leveraged (i.e. `as_a`); however, current versions of easyb (0.9.5+) now support the removal of underscores; consequently, the `narrative` above can be rewritten as:
```
narrative 'segment flown', {
     as a 'frequent flyer'
     i want 'to accrue rewards points for every segment I fly'
     so that 'I can receive free flights for my dedication to the airline'
 }
```

Narratives do not affect the behavior of a story, per say -- in fact, their use is purely for documentation purposes and they will be shown in any easyb report that is generated at the conclusion of a run.