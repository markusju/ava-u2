package de.htwsaar.kim.ava.avanode.file;


import de.htwsaar.kim.ava.avanode.application.NodeCore;
import de.htwsaar.kim.ava.avanode.dot.Dot;
import de.htwsaar.kim.ava.avanode.dot.Edge;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by markus on 25.12.16.
 */
public class FileConfig {


    private int ownId;
    private String fileNameConf;
    private String fileNameDot;

    private Map<Integer, FileEntry> entries = new HashMap<>();
    private Set<FileEntry> neighbors = new HashSet<>();

    private NodeCore nodeCore;

    public FileConfig(NodeCore nodeCore, int ownId, String fileNameConf, String fileNameDot) throws IOException {
        this.nodeCore = nodeCore;
        this.ownId = ownId;
        this.fileNameConf = fileNameConf;
        this.fileNameDot = fileNameDot;
        this.readFromConf();
        this.readFromDot();

    }

    private void readFromConf() throws IOException {
        String line;
        try (
                InputStream fis = new FileInputStream(fileNameConf);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                entries.put(Integer.valueOf(parts[0]), new FileEntry(Integer.valueOf(parts[0]), parts[1], Integer.valueOf(parts[2])));
            }
        }
    }

    private void readFromDot() throws IOException{
        String line;
        try (
                InputStream fis = new FileInputStream(fileNameDot);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {

            Stack<String> stack = new Stack<>();

            while ((line = br.readLine()) != null) {
                if (line.contains("{")) {
                    stack.add("LPAR");
                    continue;
                }

                if (line.contains("}")) {
                    stack.pop();
                    continue;
                }

                String[] p1 = line.split(";");
                if (p1.length != 1 || !line.contains(";"))
                    throw new IOException("Syntax Error: Semicolon missing");

                String[] p2 = p1[0].split(" -- ");
                if (p2.length != 2 || !line.contains(" -- "))
                    throw new IOException("Syntax Error: Binary Edge Operator missing");

                int a = Integer.valueOf(p2[0]);
                int b = Integer.valueOf(p2[1]);

                if (a == ownId)
                    neighbors.add(entries.get(b));

                if (b == ownId)
                    neighbors.add(entries.get(a));
            }
            if (!stack.empty())
                throw new IOException("Syntax Error: Closing RPAR not found.");
        }
    }



    public int getPort() {
        return getOwnEntry().getPort();
    }

    public String getHost() {
        return getOwnEntry().getHost();
    }

    public int getOwnId() {
        return ownId;
    }

    public FileEntry getOwnEntry() {
        return getEntryById(ownId);
    }

    public FileEntry getEntryById(int id) {
        return entries.get(id);
    }

    public Set<FileEntry> getNeighbors() {
        return neighbors;
    }

    public Map<Integer, Integer> getVectorTimes() {
        HashMap<Integer, Integer> vecTimes = new HashMap<>();
        for(FileEntry entry: entries.values()) {
            vecTimes.put(entry.getId(), entry.getVectorTime());
        }
        return vecTimes;
    }

    public void processIncomingVectorTimes(Map<Integer, Integer> incoming) {
        for (Map.Entry<Integer, Integer> entry: incoming.entrySet()) {
            //Retrieve Entry...
            int currTime = getEntryById(entry.getKey()).getVectorTime();
            getEntryById(entry.getKey()).updateVectorTime(Integer.max(currTime, entry.getValue()));
        }
    }

    private static int generateRandomInt(int min, int max, ArrayList<Integer> excludeRows) {
        int random = ThreadLocalRandom.current().nextInt(min, max + 1);
        while(excludeRows.contains(random)) {
            random = ThreadLocalRandom.current().nextInt(min, max + 1);
        }
        return random;
    }

    public static void genElectionDotFile(int numOfParticpants, int numOfPartyFellows, int numOfFriends) throws FileNotFoundException, UnsupportedEncodingException {

        int cand1 = 1;
        int cand2 = 2;

        //Conditions
        boolean cond1 = 2*numOfPartyFellows > numOfParticpants;
        boolean cond2 = numOfPartyFellows > numOfParticpants;

        Dot dot = new Dot();

        //Party fellows
        for (int i = 3; i<3+numOfPartyFellows; i++) {
            dot.addEdge(new Edge(cand1, i));
            dot.addEdge(new Edge(cand2, i+numOfPartyFellows));
        }

        //Establish Friendships
       for (int i= 3; i<=numOfParticpants; i++) {
            //Fill with Random nodes
            while (dot.getNumOfAdjacentNodes(i) < numOfFriends) {
                ArrayList<Integer> excluded = new ArrayList<>();
                excluded.add(i);

                int randNode = generateRandomInt(3, numOfParticpants, excluded);

                if (dot.getNumOfAdjacentNodes(randNode) < numOfFriends) {
                    dot.addEdge(new Edge(i, randNode));
                }
            }

       }

        writeToFile("file.dot", dot);

    }

    public static void genDotFile(int nodes, int edges) throws IOException {
        genDotFile(nodes, edges, "file.dot");
    }

    private static void writeToFile(String fileName, Dot dot) throws FileNotFoundException, UnsupportedEncodingException {
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            writer.print(dot);
        }
    }

    public static void genDotFile(int nodes, int edges, String fileName) throws IOException {

        Dot dot = new Dot();

        int n = nodes;
        int m = edges;

        if (!(m>n))
            throw new IllegalArgumentException("The number of edges must be greater than the number of nodes!");
        if (!(m <= ((n*n)-n)/2))
            throw new IllegalArgumentException("The number of edges must be smaller or equal to two times the number of nodes.");


        Map<Integer, List<Integer>> stairs = new HashMap<>();


        for (int i = 1; i <= n ; i++) {
            if (i == 1)
                stairs.put(i, new LinkedList<>());
            else if (i == 2)
                stairs.put(i, new LinkedList<>(Collections.singletonList(1)));
            else
                //                             Fuck you, Java! :/
                stairs.put(i, new LinkedList<>(IntStream.range(1, i).boxed().collect(Collectors.toList())));
        }

        int k = 1;

        while (k <= m) {
            for (Integer i: stairs.keySet()) {
                List<Integer> step = stairs.get(i);

                if (!(k <= m))
                    break;

                if (i == 1)
                    continue;


                //Random pick

                //Check if this postion has any more choices
                if (step.size() < 1)
                    continue;


                int rand = (new Random()).nextInt(step.size());
                int j = step.get(rand);
                step.remove(rand);

                dot.addEdge(new Edge(i, j));
                k++;

                }

            }


            writeToFile(fileName, dot);

        }

    public static void genConfigFile(int maxId) throws IOException {
        genConfigFile(maxId, "file.txt");
    }

    public static void genConfigFile(int maxId, String fileName) throws IOException{
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            for (int i = 1; i <= maxId; i++)
                writer.println(Integer.toString(i) + " 127.0.0.1 " + Integer.toString(5000 + i));
        }

    }

    public static void main(String ...args) throws FileNotFoundException, UnsupportedEncodingException {
        genElectionDotFile(
                100,
                6,
                3
        );
    }



}
