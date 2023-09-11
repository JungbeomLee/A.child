import sqlite3

conn = sqlite3.connect('./database.db', check_same_thread=False)

# Create a cursor object to interact with the database
cursor = conn.cursor()

cursor.execute('''SELECT session_id FROM AIvector''')
res = cursor.fetchall()
print(res)

#conn.commit()

cursor.close()
conn.close()