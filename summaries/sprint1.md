## Summary for Week 1

# Beltan (Scrum Master)

I implemented the view of the interactive map that will show position and nearby socials.
My time estimate was fairly accurate, 4h estimated v 5h actual. 

My tasks are well laid out for next week and I have a good view of the next steps I'll be implementing in the interactive map.

# David

I implemented a feed to display socials, with the ability to click on an item of the feed to display it in more details.

Using the RecyclerView took some time to get right, UI testing remains minimal and must be made more robust.

# Eloi

This week : Deploy Cloud Firestore on the application to create the link between the app and the database, create User entity with integrated load/store methods to the database, integrate DB User creation when logging-in using Jeremie's module 
Next week : Finish User entity (Singleton version)

# Emmanuelle

I implemented the Event entity and a very basic UI to display it.

The entity part of my task went very smotthly (although it did take a little longer than expected), however when creating the UI I realized that it was not worth spending excessive time on making things look pretty before all of us agree on a design template. I also did not manage to write tests for the UI yet.

Next time, I will try to manage my time better in order to have finished tests before the end of the week.

# Jérémie

I implemented the sign in/sign up UI and the link with Firebase authentication system.

The authentication was supposed to use google authentication but I found the Firebase authentication which simplifies how we log a user.

Next time, I'll try to clear and test everything I wrote because I did not expected to spend the whole week on the implementation.

# Keran

The goal was to test the interactive map, which consists of a Google Map and markers loaded from a database. A button can be pressed to switch between venues and socials on the map.

The challenges of testing this feature was first the dependency to external data and the testing of UI elements.
To solve the first problem, I implemented the Hilt library will help inject dependencies in activities for example. The advantage of Hilt is that the dependency can be replaced in the test to have mock data. Mockito is used in our case.

To test UI elements, Expresso is recommended, but would not detect the markers on the map. On the other hand, another library, UIautomator could assert the presence of markers on the Google map.

Unfortunately, although the tests run well on a local machine, Cirrus CI failed to pass the tests, which prevented us to merge the branch in this state. This problem still needs to be resolved.

# Overall team

We implemented only part of the user stories we had assigned to this sprint as they will probably span over the course
of a few weeks. The start of the implementation for each of the features went seemingly very well and we seem on a good
track to kickstart this project. 

We also all have a good view of what our next individual tasks will be for sprint2

Our time estimates were not very accurate and will probably remain so for a couple weeks before we get better estimating
skills.

We had three standup meetings: on Monday, on Wednesday and on Friday, before the course meeting. This was a bit too much in
retrospect as for the first meeting most people did not start working because of other projects and courses taking the priority.
It did give however a strong sense of teamwork and I think everyone felt well settled into the team, in part thanks to them. 