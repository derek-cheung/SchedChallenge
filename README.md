# SchedChallenge

The app allows you to select from one of the 3 pre-packaged json files and import that data. The imported data is persisted to a local
database so it'll remain over app sessions. The database is split up in to 2 tables, one for the session data and one for the persons data.
For each role in a session, the associated person's id is stored.

When a dataset is imported, it is diffed against the data currently stored in the local database. New sessions are inserted, changed
sessions are updated and removed sessions are deleted. The list of persons is collated from all the sessions and the same diffing 
is applied.

### Considerations

Whilst the database design here works for this simple use case, it's not ideal for expanded use cases. For example, this wouldn't readily
support being able to query all sessions a user is a speaker at. If we did want to support those capabilities better in future, splitting
each of the roles in a session in to it's own session-to-person join table would be best.

### Running The App
If you have Android Studio set up, you can clone the project and open it up there. Then you can just run the app to your connected device.

In case you don't have AS set up, you can go to the apk folder in the root level of this project, copy the apk file over to your 
android device and install it directly to your device.

### Running Unit Tests
Open up the project in AS and navigate to app/src/test/java/com/derek/schedchallenge. Then right click that folder and select
'Run Tests in schedchallenge'. This will run all the unit tests in that folder. The run window with the test results should show up
at the bottom.

In case you can't set it up, an image of the test results is included in the images folder in the root level of this project.

### Dump Results
LogCat in AS has a limit on how many lines it can show. The dataset it far too large to display that in a readable manner. The dump
results are displayed in the app in a scrollable view so you can browse it that way. Normally we would view database data with an
integrated tool but that is a lot more work to set up on both sides.

### Profile Results
The result is included in the images folder in the root level of this project. The red dots at the top represent actions in the app
and correspond to these events:

1) Tap import -> import initial JSON
2) Tap edited JSON option
3) Tap import -> import edited JSON
4) Tap deleted JSON option
5) Tap import -> import deleted JSON

Results follow what you would expect to see. Slight spikes in CPU and memory usage during the import events. Largest CPU spike is the
first import event as it hits database for the first time. Memory usage slowly drops off after each import event as the GC decides
to pick off the unused resources from the imports.
