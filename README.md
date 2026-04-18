🏠 Auction House Platform

A full-stack real-time auction house application built with Spring Boot, PostgreSQL, WebSockets, and Angular.
Users can create auctions, place bids in real time, and receive live notifications.

🚀 Features
🔐 User local and oauth2 authentication & role-based access
🧾 Create and manage auction listings
⏱️ Real-time bidding system using WebSockets
📢 custom Live notifications for Auction updates and Winning alerts
📦 Auction item management (create, view, bid)

🛠️ Tech Stack
Backend
Java 17+
Spring Boot 4
Spring Web
Spring Security (JWT assumed)
Spring Data JPA (Hibernate)
WebSocket (STOMP protocol)
PostgreSQL
Frontend
Angular
Tailwind CSS

🧱 System Architecture
Frontend (Angular) communicates via REST + WebSocket
Backend (Spring Boot) handles:
Authentication (JWT-based)
Auction logic
Bid processing
WebSocket messaging
PostgreSQL stores users, auctions, and bids

Real-time flow:

User places a bid
Backend validates & saves it
WebSocket broadcasts update to all connected clients
UI updates instantly
🔐 Authentication & Roles
Users can register and login
Role-based access system:
USER → can create auctions & place bids
ADMIN → planned for future implementation
JWT-based authentication (assumed standard setup)
⚙️ Backend Setup
Prerequisites
Java 17+
Maven
PostgreSQL
Configuration

📌 Current Limitations / To Be Implemented
🛠 Admin dashboard (not implemented yet)
💳 Payment integration (future)
🖼 Image upload for auction items and user profile image and storing it with external service (future)
📦 Docker deployment setup (future)
🔔 Advanced notification system improvements (planned)
📈 Future Improvements
Admin panel for moderation
Chat between bidders/sellers
Email notifications
Dockerized deployment
