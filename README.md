# Conduit - A Medium.com Clone.
#### This is an implementation of Conduit, a Medium.com clone. Medium is a famous blogging website.
---
### Database Schema Design (EER)
---
<p align="center">
  <img src="art/Updated%20Conduit%20EER.png" alt="EER Diagram">
</p>

---
### Frameworks & Libraries Used
---

|       Tools                |                                 Link                                 |
|:---------------------------|:--------------------------------------------------------------------:|
| 🤖  Language              |           [Java](https://www.java.com/en/)                           |
| 💚  Framework             |         [SpringBoot](https://spring.io/projects/spring-boot)         |
| 📁  DB Access             |            [jOOQ](https://www.jooq.org/)                             |
| 📼  Caching DB            |           [Redis](https://redis.io/)                                 |
| 📁  Main DB               |           [MySql](https://www.mysql.com/)                            |
| 🔍  Caching Library       |           [Redission Library](https://github.com/redisson/redisson)  |
| 🔍  Validation            |           [Hibernate Validator](https://hibernate.org/validator/)    |
| 🔍  Authorization         |           [JWT tokens](https://jwt.io/)                              |

  

---
### Features
---
- Username Password used for authentication, JWT tokens used for authorization.
- Create, update and delete articles.
- Faster access to the most popular articles, using a caching system with Redis.
- Get a global feed of articles, which can be filtered by author, tags, favourited or not.
- Mark articles as favourite.
- Comment on articles.
- Reply to comments.
- Personalize your user profile by adding a profile picture.
- Follow/Unfollow other users.
- Subscribe to a daily digest of the latest articles, sent to you by email.
- Receive congratulatory emails commemorating the achievement when your article breaks a favorite count milestone.

