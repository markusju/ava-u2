package de.htwsaar.kim.ava.avanode.file;

import de.htwsaar.kim.ava.avanode.dot.Dot;
import de.htwsaar.kim.ava.avanode.dot.Edge;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
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

    public FileConfig(int ownId, String fileNameConf, String fileNameDot) throws IOException {
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



    public static void genDotFile(int nodes, int edges) throws IOException {
        genDotFile(nodes, edges, "file.dot");
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


            try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                writer.print(dot);
            }

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



}
