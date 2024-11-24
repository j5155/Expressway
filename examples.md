## ConditionalAction

Java:
```java
public Action choose() {
    return new ConditionalAction(
            new SleepAction(1), // will be run if the conditional is true
            new SleepAction(2), // will be run if the conditional is false
            () -> Math.random() > 0.5); // lambda conditional function, returning either true or false;
    // this example picks which one to run randomly
}
```

Or in Kotlin:
```kt
fun choose(): Action {
    return ConditionalAction(
        SleepAction(1.0),  // will be run if the conditional is true
        SleepAction(2.0) // will be run if the conditional is false
    ) { Math.random() > 0.5 } // lambda conditional function, returning either true or false;
    // this example picks which one to run randomly
}
```