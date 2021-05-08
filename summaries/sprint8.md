## Summary for Week 8

# Beltan

This week I worked on modifying the settings and linking the Map and Feed to the DataBase. The first task went well. I implemented it without much trouble and it only took a little more time than expected. The second task was much more harduous than expected because it required modification of many other classes and modifying them made the tests not pass anymore. The task itself is done, but I still need to do modifications so that tests pass and we can merge.

My time estimates this week were quite off. I ended up spending much more time than expected because of all the inter-dependencies the second task had.

# David

This week I kept working on the Organizations UI. I ran into some trouble with navigating between views with the FragmentStateManagers hierarchy and underlaying activity. I also had trouble perforing the query that would retrive and update organizations. I hope I'll be done by next week's sprint. 


# Eloi (Scrum Master)
This week, I finished my task of last week which was refactoring the event classes. Last week, I didn't do it as I did the DB refactoring.


# Emmanuelle

This week I continued working on the activity that tracks a user's friends. Since there are a lot of resources about this type of feature on the Internet, it was not that difficult. However, testing it was very difficult, and I still haven't been able to find a good way to test the UI features. I'll keep working on that next week. My time estimate was off, because I did not expect the testing to be that difficult.

# Jérémie

This week, I worked on the UI for a public profile and I refactored a bit the FriendsActivity in order to have a clean UI. I had issues with fragments and how they are displayed thus I spent more time than excpected on this particular task. I could no address the other one which was to clearup the events displayed and make cards out of them. This will be the fisrt task I'll do next week.

I wasn't able to PR this week since I had tests to refactor but this will be done this weekend.

# Keran

I reworked the FriendsRepository which is responsible for the queries related to friends management. I adatpted it to our new Database API. I then created and adapted existing views to actually use the FriendsRepository. In the end, the app displays the list of friends, let you search for user and accept or decline requests. I could not merge the PR because both Jérémie and I worked on the same view and we need to decide on a final design for the FriendsActivity.

# Overall team
Good job this week. Finishing both DB and Event refactoring means we can finally go forward and finish implementing core features. We also started changing the UI in order to not get to much work at the end of the semester.
