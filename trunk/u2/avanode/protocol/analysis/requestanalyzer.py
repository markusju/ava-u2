__author__ = 'markus'

from collections import OrderedDict
import avanode.cli


class RequestAnalyzer (object):

    def __init__(self, nodeprotocol):
        """
        Startet eine syntaktische Analyse des Requests
        :param serverprotocol:
        :return:
        """
        self.nodeprotocol = nodeprotocol
        self.request_stack = []

        self._request_method = ""
        self._request_method_arg = ""
        self._request_full = ""

        self._parameters = OrderedDict()

        self.__analyze_request()
        self.__cleanup_request_stack()
        self.__evaluate_request()


    @property
    def client_port(self):
        """
        Returns the client port
        :return:
        """
        return self.nodeprotocol.get_addr[1]

    @property
    def client_ip(self):
        """
        Returns the client's IP
        :return:
        """
        return self.nodeprotocol.get_addr[0]

    @property
    def request_method(self):
        """
        Returns the used request method
        :return:
        """
        return self._request_method

    @property
    def request_method_arg(self):
        """
        Returns the Method Argument
        :return:
        """
        return self._request_method_arg

    @property
    def request_full(self):
        """
        Returns the full request
        :return:
        """
        return self._request_full

    @property
    def parameters(self):
        """
        Returns the paramters
        :return:
        """
        return self._parameters

    @property
    def src_parameter(self):
        """
        Returns the SRC parameter
        None, if none found (avoids KeyErrors)
        :return:
        """
        if "SRC" in list(self._parameters.keys()):
            return self._parameters["SRC"]
        return None

    def __analyze_request(self):
        """
        Analyzes the incoming request from the client
        :return:
        """
        while True:
            try:
                self.nodeprotocol.read_line()
                self.request_stack.append(self.nodeprotocol.get_current_line())

                # Abbruch-Bedingung fuer Lesevorgang (1x New line)
                if len(self.request_stack) > 1 and self.request_stack[-1] == "\n":
                    break

            # Solange lesen bis nichts mehr verfuegbar ist
            except IOError:
                break
            except Exception as e:
                avanode.cli.write_ava(self.nodeprotocol.conf.get_own_id(), "An Error occured while processing a message received from a client: "+e.message, None)
            # Alle anderen durchreichen
#            except Exception as exc:
#                raise exc

    def __cleanup_request_stack(self):
        """
        Entfernt alle Eintraege die genau ein Newline-Zeichen sind und entfernt alle Newline-Zeichen, die sich am Ende eiens strings befinden.
        :return:
        """
        current_stack = self.request_stack
        new_stack = []

        for el in current_stack:
            if el[-1] == "\n" and len(el) > 1:
                new_stack.append(el[:-1])

        self.request_stack = new_stack

    def __evaluate_request(self):
        """
        Evaluates the previously recorded request
        :return:
        """
        if len(self.request_stack) < 1:
            raise Exception("Evaluation Error")

        current_stack = self.request_stack
        self._request_full = current_stack[0]

        method_parts = current_stack.pop(0).split(" ", 1)

        self._request_method = method_parts.pop(0)

        if len(method_parts) > 0:
            self._request_method_arg = method_parts.pop(0)

        for params in current_stack:
            # Split on first occurence
            parts = params.split(":", 1)

            # Parameters sind fehlerhaft
            if len(parts) != 2:
                raise Exception("Evaluation Error")

            key = parts[0].strip()
            value = parts[1].strip()

            if not key or not value:
                raise Exception("Evaluation Error")

            self.parameters[key] = value
