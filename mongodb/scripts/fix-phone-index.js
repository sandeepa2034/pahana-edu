// MongoDB script to fix phone index issue
// Run this script in MongoDB to resolve the duplicate key error

// Switch to the database
use('pahana_edu_db');

// First, drop any existing phone index
try {
    db.users.dropIndex("phone_1");
    print("✅ Dropped existing phone index");
} catch (error) {
    print("ℹ️ Phone index doesn't exist or already dropped: " + error.message);
}

// Find and delete users with null phone numbers (these are old records)
const usersWithNullPhone = db.users.find({ phone: null }).toArray();
print("📊 Found " + usersWithNullPhone.length + " users with null phone numbers");

// Delete users with null phone
const deleteResult = db.users.deleteMany({ phone: null });
print("🗑️ Deleted " + deleteResult.deletedCount + " users with null phone numbers");

// Also delete users with empty string phone
const deleteResult2 = db.users.deleteMany({ phone: "" });
print("🗑️ Deleted " + deleteResult2.deletedCount + " users with empty phone numbers");

// Now create a unique index on phone (only for non-null values)
try {
    db.users.createIndex(
        { phone: 1 }, 
        { 
            unique: true, 
            partialFilterExpression: { phone: { $exists: true, $ne: null, $ne: "" } } 
        }
    );
    print("✅ Created unique partial index on phone field");
} catch (error) {
    print("❌ Failed to create phone index: " + error.message);
}

// Show current users count
const userCount = db.users.countDocuments();
print("📈 Total users remaining: " + userCount);

// Show all users
const allUsers = db.users.find({}).toArray();
print("👥 Current users:");
allUsers.forEach(user => {
    print("  - " + user.username + " (phone: " + user.phone + ", role: " + user.role + ")");
});

print("🎉 Database cleanup completed!");
