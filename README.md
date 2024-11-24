# Welcome to ActionAdditions!

This library provides additions to the RoadRunner actions library. 

## Installation

Ensure that the following blocks are present in the "repositories" block of your TeamCode module build.gradle:
```groovy
    maven {
        url = 'https://repo.dairy.foundation/releases'
    }
    maven {
        url = "https://repo.dairy.foundation/snapshots"
    }
```

Add the following line to the "dependencies" block of that same build.gradle:
```groovy
implementation "page.j5155.roadrunner:actionadditions:0.0.1"
```

## Examples

Example repo coming soon (maybe); for now, see [the examples page](examples.md)