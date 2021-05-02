## Summary for Week 2

# Beltan

I continued the implementation of the map, namely the ToolBar and a setting that allows for a maximum distance to which
events are shown to be chosen. I ran into many issues for the ToolBar, apparently related to the Places API from Google.
I have not yet managed to resolve those issues.
The settings I have implemented without too many issues. A couple arose but I managed to resolve them.

I took a lot more time than expected on this toolbar, especially in trying to find the cause of the errors, which I have still
not been able to do. I name Google the cause for all of this

Except for that however my estimates were quite close to the time I ended up investing this week into the project.

# David

I added a the user interface for creating events. This includes name, description, an image, website, etc. There were no particular issues beside sanitizing the user input. The solution I settled on was to add a utility class that offers static methods that perform input verification. This can be expanded on and reused in other parts of th projects.
It took way more time to build the social creation UI, Keran created the tests and overall this task took 3 to 4 times the expected time for completion

# Eloi

I extended to Event class for Database Support : added method to load an Event from the Database, update a particular field et store an social on the database. Took more time than expected because of merging every Event class previously made. I also coded a first version of the "Saved a feature social" which took the expected time. It's very basic for now : press on a button to save social and the id of the button is added to a saved_event array list of the user.

# Emmanuelle

I first finished writing tests for the code that I wrote last week. Then, I merged my branch with the main branch and launched a pull request. Finally, I started working on a way to cache Event entities.

The merge took a lot longer than I expected since I ran into quite a few issues. Also, my estimate for the caching of social entities was off, and therefore I am not finished yet. For next week, I hope to estimate better.

# Jérémie

I implemented tests for the authentication UI (SignUp and SignIn activities) but faced issues with the previous implementation. I had to reformat the whole Firebase integration in order to moduralize in a clean way. I also merged the main branch into the branch I'm working on.

The estimates for my tasks were way off since I had to do more work than expected. I did not merge back to main since the sprint tasks for this week and the concerned user stories in the sprint backlog are not finished yet. Next week, I'll finish the testing part in order to match the required coverage. 

# Keran (scrum master)

I tested the creation of an social which was implemented by David. The testing was split into two tasks. First, the input validator for the fields was unit tested because this task was extracted in a class that can be used to validate any field. Then an instrumented test checks that a user can successfully create an social or see the appropriate errors in the form.

The challenges were the use of Expresso to fill in form, especially for date picker and spinner, and the refactor of the input validator to separate the logic part of the UI one.

# Overall team

Great progresses were made to have a common conception of the basic entities: events, public and private users such that everyone can work on a common basis. However, it is still a work in progress that must finalized next week. Another challenge tackled by the team is the implementation of the Firebase database and authentification. This work spans a few weeks to be fully working.

Another part of the team works on concrete feature that can be seen on the end product (e.g map features, social creation). Efforts need to be made to better estimate the work load and split the work into feasible task, but the learning curve is steep ! The organization get better every day thanks to the standup meeting and constant communication.
