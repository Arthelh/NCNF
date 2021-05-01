## Summary for Week 7

# Beltan

This week I finished my tasks of modifying the map markers so that first, many events happening at the exact same place would create only one marker representing them all and second, when clicking on a marker's info window, you can access either the event page if it's the only one represented by the marker or a custom feed view if the marker represents multiple ones. Showing the info window took me more time than expected because it was surprisingly more difficult than I thought and the other task went pretty much as expected.

# David (scrum master)

This week I started working on the whole Organization interface. This required some restructuring of the user profile to incorporate organizations. The token system still needs to be 
determined and then implemented. Next week I might be able to request a PR with the Organization UI.


# Eloi 
With Jérémie, we work on refactoring/re-designing the database service. I spent a lot of time coding new tests for this in order to get a good coverage. Now it's much cleaner than before and easier to use. I'm glad we managed to finish it this week so we can move forward for the next following weeks and the others can fully implement features that require DB usage. Beside that, I also started refactoring the private/public event classes to apply changes we discussed about last week. There is still a lot to do for this task but it will be done next week for sure.

# Emmanuelle 

This week I continued working on the feature that shows friend group participants where everyone is located. This took a little longer than expected especially since I wasn't exactly sure how to link it to the database. My goal is to finish this by the weekend. 


# Jérémie

This week I continued the refactor of the DatabaseService and was able to merge those changes with the help of Eloi. It was a big PR and I spent a lot of time figuring out how to moduralize this service as clean as possible. Now we have clean functions and abstractions to communicate with Firebase. 
As expected, the time it took to refactor this part was underestimated but thanks to Eloi this was possible to finish this part in order to continue working on extra features next week.

# Keran

Following the upload of images on Firebase Storage last week, I implemented the download and display of images, specfically for the event view. On top of that, I added a cache such that when a file is downloaded, it is then stored in the local storage specifically designed for caching through the Android API.
I also review the refactoring of database service, which was quite a thing.


# Overall team

This week's workflow was much better than last's. Eloi and Jérémie spent a lot of time refactoring the whole database interface and testing it. Keran also completed a caching system that
hopefully will fulfill the requirements.  
Overall, The project appears to be back on track, however we must remain careful with our planning to avoid making the same mistakes again. We still have core features and requirements to 
complete and we will prioritize those above the rest. 

