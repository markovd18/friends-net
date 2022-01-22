## Friends Net Client

Client side of the application consists of two yarn packages:

- **[friends-net](#friends-net)** - the application itself
- **[friends-net-api](#friends-net-api)** - package with generated API interfaces for client-server communication

## Friends net API

API package contains just generated objects and axios interfaces allowing easy communication with server. This generation is possibly by Open API typescript generator. For convenience, this package contains custom `index.js` file exporting all the API classes, configured to communicate with correct server url. The entire package is exported as `@markovda/fn-api` for convenient usage.

## Friends Net

Friends Net client application is implemented with Next.js and packaged with Yarn.

Package itself is divided into several directories:

- **components** - reusable components used across the application
- **hooks** - reusable React hooks for convenient usage of shared component logic
- **pages** - individual Next.js application pages
- **public**
- **styles**
- **utils**

## Pages

Every page has one thing in common - navigation panel at the top. This panel is different for authenticated (also different for admin and regular user) and unauthenticated users.

Unauthenticated users are allowed to continue to login or registration page via navigation panel.

Authenticated users may log out, view their relationships or proceed to the home page.

### Landing Page

Index page is used only to greet unauthenticated user and redirect him to the login or registration page.

### Registration page

Registration page contains simple form - the registration form.

User is required to register with unique user-name in format of e-mail, password and nickname of choice. Once registered, user may sign in and proceed to home page.

### Login page

Just like registration page, it contains simple login form. It allows user to log in with his credentials. Once logged in, user is immediately redirected to home page.

Server uses JWT authentication. That means on login client receives his credentials and his unique authentication token that he needs to store and sent to server with every request in Authentication header. This application stored users identification data in cookies.

### Home page

Home page is available to authenticated users only. When unauthenticated, visitor is immediately redirected to landing page.

This is the main page of the application. It displays posts of all user's friends side by side with admin announcements, shows on-line users and allows user-to-user messaging.

Messaging is available through list of on-line friends. Clicking on one's name shows pop-up dialog where one can send messages to the other. It is limited only on one chat at a time. That means when one receives message and no chat is open, it is automatically opened and messages are shown. When chat is already active, second is not show but the inbound message is stored in memory. When user then opens chat with the user who sent him the message, it will be available there.

As of now, no chat message notifications are implemented. Therefore when one receives message and has already different chat open, he will not be notified that he got a new message.

### Friendships page

On page with friendships user may display his current friends, users that he blocked or his pending friend requests. He may also fill a text input and search for new users to be friends with.
