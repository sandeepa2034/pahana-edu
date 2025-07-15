// MongoDB Database Initialization Script for Pahana Edu Bookshop
// Run this script in MongoDB shell: mongo < init-db.js
// Or copy and paste in MongoDB Compass shell

// Switch to the application database
use("pahana_edu_db");

// Create indexes for better performance
db.customers.createIndex({ "phoneNumber": 1 }, { unique: true });
db.customers.createIndex({ "email": 1 });
db.customers.createIndex({ "accountNumber": 1 }, { unique: true });

db.books.createIndex({ "isbn": 1 }, { unique: true });
db.books.createIndex({ "title": 1 });
db.books.createIndex({ "author": 1 });
db.books.createIndex({ "category": 1 });
db.books.createIndex({ "price": 1 });

db.bills.createIndex({ "customerPhoneNumber": 1 });
db.bills.createIndex({ "billDate": 1 });
db.bills.createIndex({ "billNumber": 1 }, { unique: true });

db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });

print("Database indexes created successfully for Pahana Edu Bookshop!");
