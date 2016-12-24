__author__ = 'markus'


class FileEntry:

    def __init__(self, host_id, host, port):
        self.id = host_id
        self.host = host
        self.port = port
        self.already_contacted = False

    def get_id(self):
        """
        Returns the ID
        :return:
        """
        return self.id

    def get_host(self):
        """
        Returns the host name
        :return:
        """
        return self.host

    def get_port(self):
        """
        Returns the defined port
        :return:
        """
        return self.port

    def is_already_contacted(self):
        return self.already_contacted

    def set_already_contacted(self):
        self.already_contacted = True

    def __str__(self):
        return "<FileEntry Object:: id: "+str(self.id)+" host:"+self.host+" port:"+str(self.port)+">"
