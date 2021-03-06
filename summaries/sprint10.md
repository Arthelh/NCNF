# Summary for Week 10

## Beltan (Scrum Master)

This week I finished the last links of the map to the database, namely showing the organizations on the map, now that the organizations view and entity has been created. I also finished the date filtering by implementing the actual filtering in addition to just the layout. Overall it went okay but because of weird issues with my Android Studio and other things it ended up taking me much more than it was supposed to.

## David

I started this week by helping Keran fix all the tests that had been broken because of last minute changes for the demo and incorrect Injections, along the way I started changing the Date to LocalDateTimes. I then patiently waited for the package hierarchy to be refactored and finally started working on Organization elements that were missing, namely event creation and owned events list. I didn't run into any difficulties so far as UI is fairly straightoforward.


## Eloi 

This week, I finished the User Profile UI : took my a bit more time as I decided to add several features to it afterwards. Once this was done, I started to add java documentation to the code because if we start this too late, we might not be able to finish it before the deadline. I have also added a few tests to increase the overall test coverage of the project which had dropped a bit after the last pull requests.


## Emmanuelle

This week, I worked on the UI for the group feature. For now, it is mostly foccused on functionality as opposed to aesthetics. Apart from some small testing dissifuclties which I managed to solve in the end, everything went as expected without any particular difficulties. However, some of the classes I wrote will need to be refactored once we create a GroupRepository (as of right now I'm still injecting a DatabaseService).

There are still some issues with the linking of the groups to the database. I hope to fix those next week, as well as making the UI a bit nicer to look at.

## Jérémie 

This week, I worked on refactoring the whole project addressing the Code Review. I had to wait for every team member to merge their PRs in order to avoid merging issues. I refactored the layout of packages and addressed the naming convention issues. I'm still working on the modularity issues by writing Repositories for each model to be managed in the database. 

This refactoring will be merged during next week since it takes more than one week to refactor the whole project.

## Keran

We needed to flush our work in progress to apply package refactoring. We realized that some tests were dependent on the database and failed. That problem prevented us from merging any new pull requests. So I worked in emergency to fix the tests which meant add injection to activities (e.g MapActivity) and mocking to the corresponding tests.

Furthermore, I changed the use of Date to LocalDateTime because of deprecation notice. This requires changes across the app, for conversion from LocalDateTime to Date for some API.

I could not work on all my tasks this week because of the refactoring. So the tasks will be finished by the end of week.

## Overall team

This week went very well. One of the big tasks we had to do was the folder restructuring which made a bottleneck where we all had to finish our tasks, do this refactor, and then only start working again, but it went well and it is now done. Everyone continued working on their respective tasks and we are coming ever closer to the final product.
