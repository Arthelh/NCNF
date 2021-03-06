# Summary for Week 9

## Beltan

This week I finished linking the Database to the Map and Event Feed. The link itself was already done in the previous but to finish the implementation at the UI level I had to refactor the SocialObjectActivity into a fragment. 
This itself went fine, but it took me quite some time to fix the way it would show on the map and that ended up taking me quite some time. I also added in a little extra in that now the back arrow key also returns to the previous fragment
and doesn't just close the app if you're on the main activity.

Overall it took me a bit longer than expected, mostly because of the map and interactions I didn't know would happen that took me quite some time to fix.

## David

This week I refactored the Organization option inside a user profile to its (temporary?) final form we settled on. With help from Keran we added a repository interface to communicate with the organizations in the db. 

## Eloi 
This week, I focused on the User Profile Interface : it needed a serious refactor as I wasn't a good UI at all. Took more time than expected as clean UI requries time and patience. I had a bit of trouble to understand layouts, etc.

## Emmanuelle

This week, I first finished the tests for the feature that I implemented last week. This was a bit difficult at first, and I ended up having to use a feature that we are planning to remove later, but it would've been impossible to test otherwise so I'll just refactor it when we remove the feature. I also had some other troubles with testing because most of my feature is background activity but I eneded up getting an acceptable coverage.

After merging that pull request, I redid the UI for both the event cards in the feed and the event page itself. This wasn't particularly difficult and I didn't run into any particular problems. My time estimates were pretty good this week.

## Jérémie (Scrum Master)

This week, I worked on the UI for the friends system and its link to the existing backend functions. I had trouble with the way I wanted to display each new feature and this took me some reflexion time but I'm happy with the result. I also worked on how we display the profile of my friends with "sensible information". Finally, I used Keran's work in order to load users profile pictures.

Those tasks took me some time but I think the expected time was pretty close to the actual one. One issue that I have to address is to avoid merging too big PRs but this will be my goal next week.

## Keran

I started the week by working with David on Organization. The goal was to finish the pending work such that we can address the code review concerns. So I wrote tests for the views that displays organization to the user and let him add a new one. I also implemented and tested am OrganizationRepository to help finish the feature more easily. On Jeremy's request, I added a default image when downloading an image from the FileStorage such we can have default picture in the application. I realized that I did not properly injected the FileStore in the activities and the tests, so I also fix that problem.

## Overall team

This week's sprint, we decided to keep the tasks and user stories we were working on in order to fully complete them. We achieved our goal and were able to complete 4 big user stories, including the Friends system, the public profile and the Map displaying nearby events. We worked also on a draft for the live position tracking system, which is one of the most important features, and on linking a user to an organization in order to publish events. The destabilisation srpint went pretty well since the extra user story is also complete with a working backend (no UI yet).

Teamwork clearly became easier since several weeks. The team has a clear vision of the goal to achieve each week and the next features to implement. Overall, our app is really starting to look great and we all look forward to finish it.
