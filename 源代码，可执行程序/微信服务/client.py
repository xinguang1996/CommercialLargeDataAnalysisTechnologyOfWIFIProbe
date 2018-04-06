import socket

def send(x):
	s = socket.socket()
	host = '192.168.1.205'
	port = 12345
	s.connect((host, port))
	s.send(x)
	print s.recv(1024)

send('1')
