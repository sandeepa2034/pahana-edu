# MongoDB Scripts and Data

This directory contains MongoDB-related scripts and sample data for the Pahana Edu Bookshop application.

## Contents

- `sample-data/` - Sample data files for development and testing
- `scripts/` - MongoDB scripts for database initialization
- `indexes/` - Index creation scripts for performance optimization

## Usage

To initialize the database with sample data:

```bash
# Import sample books
mongoimport --db pahana_edu_db --collection books --file sample-data/books.json --jsonArray

# Import sample customers
mongoimport --db pahana_edu_db --collection customers --file sample-data/customers.json --jsonArray
```

## Database Collections

- `customers` - Customer information with phone number as primary key
- `books` - Book inventory with title, author, price, stock
- `bills` - Customer purchase records and billing information
- `users` - Admin users for system management
