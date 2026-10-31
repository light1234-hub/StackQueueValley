# REQ1 Plant Unit Checks

`game.terrain.Req1PlantUnitTests` is a dependency-free Java test runner because this project folder has no Maven/Gradle configuration or test framework.

Run the class's `main` method from the IDE after marking `test` as a test source root, or compile it with the project's `src` files on the same Java classpath. It prints one `PASS` line per case and throws an `AssertionError` when a check fails.

The cases cover strawberry offspring stage, same-turn newborn timing, initial plant age, scheduled produce, melon spreading, plantable-soil lookup, and immature-stage timing. Random success probabilities are not asserted so the checks stay deterministic.
