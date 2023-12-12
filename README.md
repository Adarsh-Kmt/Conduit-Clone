# Conduit - A Medium.com Clone.
#### This is an implementation of Conduit, a Medium.com clone. Medium is a famous blogging website.
---
### Database Schema Design (EER)
---
<p align="center">
  <img src="art/Conduit%20EER.png" alt="EER Diagram">
</p>

---
### Frameworks & Libraries Used
---
- Spring Boot framework
- MySQL as the main database
- Redis as the caching database
- Redisson Library
- JOOQ Library

---
### Features
---
- Create, update and delete articles.
- Faster access to the most popular articles, using a caching system with Redis.
- Get a global feed of articles, which can be filtered by author, tags, favourited or not.
- Mark articles as favourite.
- Comment on articles.
- Reply to Comments.
- Personalize user profile by adding a profile picture.
- Follow/Unfollow other users.

