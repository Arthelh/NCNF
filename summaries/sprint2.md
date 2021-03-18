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

# Eloi

# Emmanuelle

# Jérémie

I implemented tests for the authentication UI (SignUp and SignIn activities) but faced issues with the previous implementation. I had to reformat the whole Firebase integration in order to moduralize in a clean way. I also merged the main branch into the branch I'm working on.

The estimates for my tasks were way off since I had to do more work than expected. I did not merge back to main since the sprint tasks for this week and the concerned user stories in the sprint backlog are not finished yet. Next week, I'll finish the testing part in order to match the required coverage. 

# Keran (scrum master)

I tested the creation of an event which was implemented by David. The testing was split into two tasks. First, the input validator for the fields was unit tested because this task was extracted in a class that can be used to validate any field. Then an instrumented test checks that a user can successfully create an event or see the appropriate errors in the form.

The challenges were the use of Expresso to fill in form, especially for date picker and spinner, and the refactor of the input validator to separate the logic part of the UI one.

# Overall team

Great progresses were made to have a common conception of the basic entities: events, public and private users such that everyone can work on a common basis. However, it is still a work in progress that must finalized next week. Another challenge tackled by the team is the implementation of the Firebase database and authentification. This work spans a few weeks to be fully working.

Another part of the team works on concrete feature that can be seen on the end product (e.g map features, event creation). Efforts need to be made to better estimate the work load and split the work into feasible task, but the learning curve is steep ! The organization get better every day thanks to the standup meeting and constant communication.
