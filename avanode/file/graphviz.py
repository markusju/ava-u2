__author__ = 'markus'


import pydot
import random


class GraphViz:
    def __init__(self, filename, own_id):
        self.filename = filename
        self.own_id = own_id
        self.neighbors = set()
        #self.__read_from_file()
        self.__alt_read_from_file()

    def __read_from_file(self):
        """
        Parse GraphVIZ file
        WARNING! Takes about 200ms.
        Not very efficient
        :return:
        """
        graph = pydot.graph_from_dot_file(self.filename)
        (g,) = graph # type: pydot.Dot

        for f in g.get_edge_list(): # type: pydot.Edge
            a = int(f.get_source())
            b = int(f.get_destination())

            if a == self.own_id:
                self.neighbors.add(b)

            if b == self.own_id:
                self.neighbors.add(a)

    def __alt_read_from_file(self):
        """
        Parse GraphVIZ file more efficiently
        Takes about 2ms
        :return:
        """

        with open(self.filename) as f:
            stack = []
            for line in f.readlines():
                # Parsing: Nice and fuzzy...
                if "{" in line:
                    stack.append("LPAR")
                    continue

                if "}" in line:
                    stack.remove("LPAR")
                    continue

                # Check for closing semicolon
                p1 = line.split(";")
                if len(p1) != 2:
                    raise Exception("Syntax Error: Semicolon missing!")

                # Binary Edge operator
                p2 = p1[0].split(" -- ")

                if len(p2) != 2:
                    raise Exception("Syntax Error: Binary edge operator missing!")

                #edges:
                a = int(p2[0])
                b = int(p2[1])

                if a == self.own_id:
                    self.neighbors.add(b)

                if b == self.own_id:
                    self.neighbors.add(a)

            if len(stack) != 0:
                raise Exception("Syntax Error: Closing parenthesis missing!")

    def get_neighbors(self):
        """
        Returns all neighbors for the specified Node ID
        :return:
        """
        return self.neighbors

    @staticmethod
    def gen_graph_save_to_file(nodes, edges, filename = 'file.dot'):
        """
        Save randomized Graph to file
        :param nodes:
        :param edges:
        :param filename:
        :return:
        """
        graph = GraphViz.gen_graph(nodes, edges)
        string = graph.to_string()

        text_file = open(filename, "w")
        text_file.write(string)
        text_file.close()

    @staticmethod
    def gen_graph(nodes, edges):
        """
        Generates a random graph.
        :param nodes:
        :param edges:
        :return:
        """
        graph = pydot.Graph()
        graph.set_type("graph")

        n = nodes
        m = edges
        if not m>n:
            raise ValueError("The number of edges must be greater than the number of nodes.")
        if not m <= ((n*n)-n)/2:
            raise ValueError("The number of edges must be smaller or equal to two times the number of nodes.")

        stairs = {}

        # Generate Stairs
        # Dictionary containing a list of all valid endpoints for each node in the graph
        # Following code then picks randomly from each list...
        # Example for n = 4 ....
        # {1: [], 2: [1], 3: [1,2], 4: [1,2,3] }
        for i in range(1, n+1):
            if i == 1:
                stairs[1] = []
            elif i == 2:
                stairs[2] = [1]
            else:
                stairs[i] = list(range(1, i))

        # Generate Graph
        k = 1
        while k <= m:
            for i in list(stairs.keys()):
                # Outer condition does not hold anymore
                if not k <= m:
                    break
                # Do not do anything for i == 1
                if i == 1:
                    continue

                try:
                    j = random.choice(stairs[i])
                except IndexError:
                    # Continue on IndexError
                    continue
                # Remove element
                stairs[i].remove(j)
                # Add Edge
                graph.add_edge(pydot.Edge(i, j))
                k += 1
        return graph


