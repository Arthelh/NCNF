## Summary for Week 3

# Beltan

This week I finished the implementation of the AutoComplete part for the Map toolbar. It is now working correctly and 
when the user enters a location, the map is moved to it and the markers are updated accordingly.
I also worked on the look of the app and I think it turned out fairly well.

About my time estimates, this week I think they were fair.

# David

This week I started to look for a way to get a recommendation system for socials. After some research I settled on google analytics with bigQuery and Tenserflow. I had to subdivide the initial spring task as it turned out being much more complex than expected. My estimates were well below the actual time this is going to take, at least another week probably.


# Eloi

This week, I manage to create the Database service in order to properly separate the back-end and the front-end. It took me the expected time as I went from the model of Authentication Service Jérémie did last week and create mine the same way. What I didn't expect was that because of this service, I had modify the bookmark I've done last week which took me a lot of time.


# Emmanuelle

This week, I worked on filtering the social feed by tags and sorting it by date or by relevance, and on connecting the feed view which was still linked with placeholder social activities to the actual social activity. I didn't run into any major difficulties, and it took me the expected time. Next week, I will implement tests for this part and merge it with the main branch.

# Jérémie (scrum master)

I spent the whole week testing the Authentication module and refactoring the UI. The testing part was cumbersome to debug but the required coverage is achieved.

This was a big part of the project to merge and it took much more time than expected.

# Keran 

I initially planned to work on a notification service, but we finally decided that merging the database/authentification branch was a priority. I was assigned to work on testing the user and database package, which heavily rely on Firebase. The task was challenging and took a lot of time and effort, but we achieved the necessary coverage at the end. The key point is to isolate the communication with Firebase to then inject and mock in Unit/Instrumented tests.

# Overall team

This week, the main task was to test and merge the Authentication and Database modules in order to build the rest of the application on top of it. The integration of Firebase services is now properly modularized and the Database can be used for our basic entities (Users, Events). This completes two high priority user stories.

Another goal was to finish the map user story, specifically the navigation and the selection of the radius we want socials to be seen. This has been done and tested, completing another user story.

For the remaining user stories in the Sprint Backlog, the work is in progress but not done yet. These will require one more week to finish implementation and testing.

The time estimates were more accurate than last week but we still need time to ajust the user stories we put in the Sprint Backlog for them to be completable in one week.

