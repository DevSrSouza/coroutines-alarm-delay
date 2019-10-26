# coroutines-alarm-delay
Delay your task in android with alarmDelay that truly runs (using AlarmManager)

## Usage
```kotlin
scope.launch {
    doSomethingBefore()
    alarmDelay(context, 2*60*1000) // 2 minutes
    doSometingAfter()
}
```

[``alarmDelay`` extensions](https://github.com/DevSrSouza/coroutines-alarm-delay/blob/master/library/src/main/java/br/com/devsrsouza/coroutinesalarmdelay/AlarmDelay.kt#L10)

## Why this project (The problem)
A mouth ago I start to work in a project that uses AlarmManager to make repeating tasks that run 
even, when the Android is in [Doze Mode](https://developer.android.com/training/monitoring-device-state/doze-standby). 
The problem is that when the Alarm was triggered in the code we have a throttling using Coroutines delay with one minute and the code before the delay, basically never runs, 
I search around the internet about this and I find the **Deep Sleep**, a state that Device goes when you are not charging and the screen is off.

### Solutions
I talk with some peoples in kotlin slack, and they give me 2 solutions: **Wake Lock** or **AlarmManager**(that we already use), 
but do a wake lock just for some throttling is not a great idea. We have to pick AlarmManager just for a throttling.

### Coroutines meets AlarmManager
The problem with AlarmManager is that is kinda hard to work with, you need to have a broadcast receiver, and if we need to throttling others parts of 
the code we will need to create a new Alarm.

Coroutines has the [suspendCoroutine](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines.experimental/suspend-coroutine.html) that we 
can use to suspend and resume in the future, is this what this library does, suspend and resume and Alarm is triggered.