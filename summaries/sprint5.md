## Summary for Week 5

# Beltan

This week I changed the source of the events for the map, a thing that I did in the first week, to have it use the real event class that is used throughout the project. I also modified the source of the events,
that was before a placeholder class, to have it be the same source as the feed. I still have work to do for next week as I have not finished all my tasks.

# David (scrum master)

This week I tried to create and run tests for user search, I was unable to find any documentation on how to make espresso type in a searchview so I wasted most of my time. I then tried to make a generic interface for Adapter and FirestoreRecyclerView, but unfortunately this doesn't appear to be possible, at least not without extreme complexity or reduced abstraction. 
I then turned to the feed view and refactored the fetching, filtering and displaying of events.


# Eloi 

This week, I have implemented pop-up alerts for the app and also changed the error format of the login/register screen. I didn't encounter any difficulties, this was relatively fast and easy to do. The only problem was that I had to change almost every test of this screen with new tests, which took a lot of time.


# Emmanuelle

This week, I started working on the user profile when we search for them. I did not have time to do a lot this week, so I will catch up next week by doing more work then. 

# Jérémie

This week, I helped Emmanuelle with the merge of her PR which was particularly cumbersome. This took me a lot of time but it was an important feature that we needed to continue the project. I also worked on the Friends system UI and worked with David in order to link the recycler views with the Firestore DB. We have now a nice interface to search users but we still have to implement the requests and link the UI to Keran's work.

It took me more time than expected but I expected it when helping Emmanuelle.

# Keran

We thought as a team that we needed to implement a friend system. I started working on the database design which would let us store the necessary data. An user can request to be a friend with another user, can see the other's user requests and approve/decline them and see his list of friends. After deciding on how the data would be store, I implemented a Friends class which exposes methods to interact with the database, such that the Activites can use invoke this class to accomplish the listed actions.

I reviewed 2 pull requests, I commented on a few details, but overall we are consistent with our code and implementation.

# Overall team

This week was difficult, as a long time pending PR had to be merged. Unclear tasks, free cloud service limitations and impossible-to-test features greatly hindered progress. Many classes and features lacked insight and worked with lists of events instead of using db queries. The goal is now to refactor, clean and update all the previous code that's become "legacy", then define clear and focused goals for the upcoming weeks. This will require a complete rebuild of the Sprint Backlog and tasks


