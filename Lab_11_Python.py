# ENSF 608
# Demo python program for connecting with databases

import mysql.connector

my_connection = mysql.connector.connect(user='Marasco', password='ensf409',
                              host='127.0.0.1',
                              database='competition')


proof = my_connection.get_server_info()  # Version of MySQL server

db_cursor = my_connection.cursor()
db_cursor.execute('SELECT DATABASE();')
current_database = db_cursor.fetchone()
db_cursor.execute('SELECT * FROM competitor')
query_result = db_cursor.fetchall()

print('Success!')
print(proof)
print(current_database)
print(query_result)
print(type(query_result[0]))

my_connection.close()

