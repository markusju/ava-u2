__author__ = 'markus'

from datetime import datetime
import sys


def write(message):
    """
    Write to stdout with newline
    :param message:
    :return:
    """
    sys.stdout.write(message+"\n")


def write_ip_port(message, ip, port):
    """
    Write log message
    :param message:
    :param ip:
    :param port:
    :return:
    """
    write("[" + ip + ":" + str(port) + "] " + message)


def write_ip_port_id(message, ip, port, id):
    """
    Write Log message
    :param message:
    :param ip:
    :param port:
    :param id:
    :return:
    """
    write_ip_port("[ID: "+str(id)+"] "+message, ip, port)


def write_ava(ownid, message, id=None):
    """
    Log message
    :param ownid:
    :param message:
    :param id:
    :return:
    """

    buffer = ""
    currtime = "[" + datetime.now().strftime('%Y-%m-%d %H:%M:%S.%f') + "]"
    ownid = "[" + str(ownid) + "]"
    if not id:
        id = ""
    else:
        id = "[ID: " + str(id) + "]"

    buffer += ownid + " " + currtime + " " + id + " " + message

    write(buffer)

