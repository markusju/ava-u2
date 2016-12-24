__author__ = 'markus'

import socket
from threading import Thread


class Client:

    def __init__(self, address, port, own_id):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        # self.socket.bind(('127.0.0.10', 0))
        self.socket.connect((address, port))
        self.sockfile = self.socket.makefile()
        # self.socket.settimeout(3)
        self.own_id = own_id
        self.currentLine = None

    def send_command(self, command):
        """
        Send a command over the socket.
        Includes the SRC parameter and the closing newline
        :param command:
        :return:
        """
        self.put_line(command+"\n"+"SRC: "+str(self.own_id)+"\n")

    def put_line(self, line):
        """
        Send string to socket.
        Please note that the socket is closed implicitly through destructor/garbage collector
        :param line:
        :return:
        """
        self.socket.sendall((line+"\n").encode("utf-8"))

    def put_line2(self, line):
        """
        Send a string with timeout set to 1 second and explicit close
        :param line:
        :return:
        """
        self.socket.settimeout(1)
        self.socket.send((line+"\n").encode("utf-8"))
        self.socket.close()

    def put_line_read_response(self, line):
        """
        Send a string and read the returned data from the remote node
        :param line:
        :return:
        """
        self.put_line(line)
        reply_stack = []
        while True:
            try:
                self.read_line()
                reply_stack.append(self.get_current_line())

                # Abbruch-Bedingung fuer Lesevorgang (1x New line)
                if len(reply_stack) > 1 and reply_stack[-1] == "\n":
                    break

            # Solange lesen bis nichts mehr verfuegbar ist
            except IOError:
                break
        return reply_stack

    def read_line(self):
        """
        Reads the next line
        Raises IOError when no more lines are available
        :return:
        """
        line = self.sockfile.readline().decode("utf-8")
        # Remove Newline from Content
        if not line:
            raise IOError("No more data")
        self.currentLine = line

    def get_current_line(self):
        """
        Returns the current line
        :return:
        """
        return self.currentLine




