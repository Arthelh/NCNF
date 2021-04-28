## Summary for Week 6

# Beltan

For this week I modified the markers on the map. I have three tasks to do: The first one is to make that when there are multiple markers
very close to each other, instead of having many markers overlapping we have a circle instead with a number showing how many markers are in
this area. This is already done and merged. The Second is to have it so that when there are multiple events at the exact same place it only creates
one marker with the information of all events it represents and the third is to have it so that when you click on a marker you can access the event
page of the event(s) it represents. Task 2 is still underway and I still have to work on task 3.

Overall I'm a bit behind on this week due to other workload and I'll have to catch up this weekend

# David
As I spent way too many hours last week on the project, I had a lot of work to catch up with, so I didn't make any progress. 

# Eloi 
This week, I had to refactor the Private/Public event classes. We change our event model to Groups/Events and dropped the abstract class we made. As for Jérémie, this change impacted a lot of files, especially the test files. For that reason, I won't have a PR before the meeting but hopefully this week-end. Also managed to help Jérémie with the Database Service.


# Emmanuelle (scrum master)

This week, I redid the UI for the event creation now that we know exactly what fields we want to put into it, and then I started working on the activity to track the location of your friends. I did not run into any major issues and I will be continuing the friend tracking activity next week. 


# Jérémie

This week, I work on the refactoring of the DatabaseService. This part impacted a lot of files in the project and I had to change many tests. Work still has to be done on this matter which explains why I do not have a PR this week. 

This took me a lot of time already and I not close to be done. But this is looking good.


# Keran

I mainly worked on file upload. We chose to use Firestore to store the images for our app (for example event banner or profile image). I implemented a class to interact with Firestore and upload an image. For now, this is used to upload the selected image when creating an event. I then display the image from the store using Glide. However, this will change as the next steps is to implement image caching, so the way images are fetched will be revisited.

I also helped Jeremie on the refactoring of our database service.

# Overall team

At the beginning of the week, we refactored the Scrum Board which helped us organize a little bit better. However, we still have issues with some people not knowing what to do because their planned tasks ended up depending on other people's tasks. For next week, we will try to change that and make sure that everyone always knows what to do next, especially since there are still quite a few things left to do.

