# altran-exercicio
Altran exercise

## Disclaimer
Although I have already used Spring Boot in my work, it had never used as Rest Application. Besides 
that, my knowledge of Angular was restricted to tutorials and videos. Therefore, to complete this 
project I had to search several sources to discover how to use Spring Boot, Angular and Mongo DB. 
I list them below. 

To develop both projects I used IntelliJ 2019.

## Usage
- Git: In console choose a directory and type:

`git clone https://github.com/rafonso/altran-exercicio.git`

It will create a `altran-exercicio` directory consisting by `carrinho-compras-backend` (Spring Boot 
backend application), `carrinho-compras-frontend` (Angular frontend application), 
`README.md` (this document) more IntelliJ and git configuration directories.
- Backend: Go to `carrinho-compras-backend` and type:

`mvn spring-boot:run`

- Frontend: Go to  `carrinho-compras-frontend` directory and type:

`ng serve`

It is possible that is shown a error message `Could not find module “@angular-devkit/build-angular”`.
In this case install this module typing:

`npm install --save @angular-devkit/build-angular`

## Backend Application
As requested, I used Spring Boot as REST application with Jetty as embedded server and MongoDB as 
database. I generate the skeleton of project from [Spring Initializr](https://start.spring.io/). Instead run a 
local instance of MongoDB, I used the [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) service to host database. 
Therefore it is not necessary run MongoDB locally. The exactly connection URL is configured in Spring Boot 
configuration file.

### Entities
The entities used here are using `@Document` annotation from Spring Data from MongoDB. Actually, due 
my lack of familiarity with NoSQL mindset, I modeled as it was a relational SQL database. Probably 
not the best way, I admit.
- `User`: It contains `id`, `email` and `name` properties. `email` is used as unique index. If 
I try insert a new User with a repeated email it will be thrown a 
`org.springframework.dao.DuplicateKeyException`.
- `Item`: It contains `id`, `name` and `value` properties. `value` must be a positive number, what 
is validated by `javax.validation.constraints.Positive` annotation.
- `CartItem`: It contains `id`, `item` and `quantity` properties. `item` is mapped using 
`org.springframework.data.mongodb.core.mapping.DBRef` annotation, working as `@ManyToOne` 
annotation from JPA. `quantity` must be a positive value. Also there is the `getItemValue()` method
that returns `item.value` * `quantity`. When the entity is serialized to JSON this getter is also 
serialized as `itemValue`. 
- `Cart`: It contains `id`, `user`, `cartItems` and `status` properties. `user`, `cartItems` are 
both using `DBRef`. `cartItems` can be null or empty. `status` can be `OPEN` or `CLOSED`. `CLOSED`
carts can not be changed. There is the `getCartValue()` method that returns the sum of 
`cartItems.itemValue`. It is serialized to JSON as `cartValue`.

### Controllers
To create the Controllers, I based myself on the article [Spring Boot + Angular + MongoDB Rest API 
Tutorial](https://www.callicoder.com/spring-boot-mongodb-angular-js-rest-api-tutorial/). The following 
Controllers were created:
- `UserController`: The methods (whose names and intent are repeated on the other controllers) used 
as are `getAll()`, `getById()`, `create()`, `update()` and `delete()`. `create()` and `update()`
can throw an exception if the email is repeated. `delete()` verifies if there is some Cart with
the User to be deleted. In positive case it is thrown a error message.
- `ItemController`: Contains the same methods cited above. `delete()` verifies if there is some 
Cart with the Item to be deleted, thrown a error message if found.
- `CartController`: In addition to the above methods, there is also the `close()` method, that 
changes the Cart status from `OPEN` to `CLOSED`. The `getAll()` returns all Carts sorted by their 
value, while their respective items are sorted according the items name. In `create()` the `cartItems` 
are inserted "manually" because Spring Data from MongoDB has no equivalent to JPA cascade 
annotations. In `update()` there is made a logic to remove `cartItem` or merge them if there are 
`cartItem`'s with the same item. In both `update()` and `close()` is done a validation if the Cart 
was already closed before, emitting an error message when needed. There is no special validation 
on `delete()`. 

### Unit tests
To execute the unit tests for the controllers, I based myself on these articles: 
- [Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing)
- [Using embedded MongoDB in Spring JUnit @WebMvcTest](https://stackoverflow.com/questions/49530149/using-embedded-mongodb-in-spring-junit-webmvctest)
- [TransactionRepositoryTest.java](https://github.com/zak905/mongo-spring-test-demo/blob/master/src/test/java/com/gwidgets/mongotest/TransactionRepositoryTest.java)

## Frontend Application
To create the Angular application, I used these articles:
- [Angular 8 CRUD Example | Angular 8 Tutorial For Beginners](https://appdividend.com/2019/06/04/angular-8-tutorial-with-example-learn-angular-8-crud-from-scratch/)
- [Angular 7 router.navigate with message after redirecting](https://stackoverflow.com/questions/58556569/angular-7-router-navigate-with-message-after-redirecting)
- [Send data through routing paths in Angular](https://stackoverflow.com/questions/44864303/send-data-through-routing-paths-in-angular)
- [Bootstrap - Alerts](https://getbootstrap.com/docs/4.0/components/alerts/)

The models from backend application were mirrored in frontend. 

### Users
The first page show all Users sorted by their email. If you try delete an User with Carts associated, a error message 
will be shown.

### Items
It works in a similar way as Users. The Items are listed according their name and a error message is shown if you try 
to delete a Item associated to some Cart. 

### Carts
This was more complex. I noticed that the pages takes longer to load, compared with Users and Items. 
Probably it happens due relationship among the Documents in MongoDB. 
- **List Carts**: the Carts are sorted according their value conform as requested. Both buttons 
_View_ (for closed Carts) and _Edit_ (for open Carts) redirect to Edit page, detailed below.
- **Create Cart**: It is shown a combo to choose the user owner of Cart. Once clicked the button 
to create, a it is created a new Cart on MongoDB with the selected User, no Items and status Open. 
Next, it is redirect to Edit page, detailed below.
- **Edit Cart** (**View Cart** when it is closed): Just like the List page, I felt a certain delay to
load the Cart data. It is shown the Cart ID, its User owner and status. I decided to not allow to 
change the user once the Cart is created. The _Add Item_ button redirect to Add Item page (below). The Items table 
shows the Item's name, its value, the requested quantity and its subtotal (i.e. value * quantity). Also, for each Item, 
there is the _Edit_ button - that redirects to Edit Item page (below) - and the _Delete_ button. At table footer there 
is the total value of the Cart. Finally There is the _Return to Carts_ button and the _Close Cart_. For closed Carts, 
just the Return button is shown.
- **Add Item**: There is a Combo with the available Items and a input field of type number to the quantity. To mount 
the combo of Items I retrieve all Items in database and remove those currently present in the Cart. This way I avoid to 
worry with repeated Items to the same Cart. If I remove a item from this Cart, it will be displayed in again in this 
combo.
- **Edit Item**: I can change only the quantity. Just like in Edit Cart page, I decided to not allow to change the Item.

## What it could be better

- I have the feeling that the way as I associated `User` and `CartItem`'s in the `Cart` Entity could be better. 
As well the `Item` inside `CartItem`. This would explain the delay I saw in `Carts` page. My lack of familiarity 
with NoSQL databases and relational database mindset affected me. I would needed more time and experience to do a 
better job.
- I executed the unit tests just for the controller tests. I confess that I did not execute the tests generated by 
Angular. Also I deleted the Unit test to the main class of Backend application. It was just testing the main method and 
was always failing. It is a shame, I know :(.
- When I try delete a User with related Carts, I could show the IDs of these Carts. The equivalent to Items. 
- A Confirmation dialog when I delete something or close a Cart.

## Conclusion
This project forced me to learn more about the learn more the technologies involved. Certainly, I will use the learnt 
lessons in my future jobs. Just for this, it was worthily.
