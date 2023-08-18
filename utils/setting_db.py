import sqlite3

conn = sqlite3.connect('database.db', check_same_thread=False)

# Create a cursor object to interact with the database
cursor = conn.cursor()

cursor.execute('''CREATE TABLE IF NOT EXISTS AIvector(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id TEXT NOT NULL,
    vector TEXT NOT NULL
);''')

conn.commit()

cursor.close()
conn.close()