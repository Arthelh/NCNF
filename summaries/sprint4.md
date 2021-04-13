## Summary for Week 4

# Beltan

This week I implemented some tests on the Map ToolBar and did some more tests for the rest of the project to increase the coverage
to allow for more room for untestable/hard to test features. For both part we worked together with Jeremie to increase the coverage and 
figure out how to do it. I also implemented the gps into the map by adding a button that when clicked requests for permission if not yet granted and/or moves the map back
to the location of the user.

I also helped Eloï for testing his part and we worked together to discover and fix a bug that came from implementing a different way of opening the "Create Event"
activity from the main menu.

Surprisingly it took less time to do my tasks than what was expected for the testing tasks, but the gps took a bit longer than expected.

# David

This week I implemented the ability to chose an event picture from the library. I also cleaned up the EventAdapter for the event's feed. I've also started working on the feature to search other users in the database.

The first two tasks only took a couple of hourse, but the third one has already taken much more time than expected

# Eloi (scrum master)



# Emmanuelle



# Jérémie
This week, I worked on the layout of the application and changed activities to use fragments, allawing us to have a bottom navigation bar that looks way cleaner than a main activity with buttons. This took me some time since I had to refactor the existing activities but I think this is a nice layout on which we can build on top of. 

I encountered a problem with the map fragment since every time we change to another one, the whole fragment, including the map, is reloaded. This will be a little task to work on later in order to save the position and "freeze" the fragment and reload it to the exact same state using caching.

I also worked with Beltan to increase the test coverage.

My time estimates were pretty close to the actual time spent on this tasks.


# Keran

I worked on implementing a notification system in our app. The first step was to let the user subscribe/unsubscribe to notifications by using a setting in the profile. We store the preference and a token in the database. Thanks to Firebase, the work was straightforward. The whole feature could be tested at a high coverage, thanks to dependency injection.

The second part was to let an organizer publish an update and notify the user. Publishing is done, but notifying the users requires a backend, which is not free in Firebase. 

I also reviewed 2 pull requests.

# Overall team



