__author__ = 'markus'

from .init import INIT
from .shutdown import SHUTDOWN
from .shutdownall import SHUTDOWNALL
from .message import MESSAGE
from .rumor import RUMOR
from .status import STATUS

from .abstractcommand import AbstractCommand

from avanode.protocol.analysis import RequestAnalyzer


class CommandFactory:
    """
    Provides Instances of Commands
    """

    def __init__(self, requestanalyzer):
        if not isinstance(requestanalyzer, RequestAnalyzer):
            raise ValueError("Argument must be instance of RequestAnalyzer")
        self.requestanalyzer = requestanalyzer

        # Command dict containing all commands
        self.cmdmap = {
            "INIT": lambda x: INIT(x),
            "SHUTDOWN": lambda x: SHUTDOWN(x),
            "SHUTDOWNALL": lambda x: SHUTDOWNALL(x),
            "MESSAGE": lambda x: MESSAGE(x),
            "RUMOR": lambda x: RUMOR(x),
            "STATUS": lambda x: STATUS(x)
        }

    def get_command(self):
        """
        Returns an instance of the command
        :return AbstractCommand:
        """
        method = self.requestanalyzer.request_method

        # Command not found
        if method not in self.cmdmap:
            # Not one of the commands? Then it is a message m
            return self.cmdmap["MESSAGE"](self.requestanalyzer)

        cmd = self.cmdmap[method](self.requestanalyzer)

        if not isinstance(cmd, AbstractCommand):
            raise RuntimeError("Illegal Command Type")

        return cmd

