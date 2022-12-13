package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class ParserOutput {
    private Parser parser;
    private List<String> productions;
    private Integer nodeNumber = 1;
    private List<Node> nodeList = new ArrayList<>();
    private Node root;
    private String outputFile;

    public ParserOutput(Parser parser, String outputFile) {
        this.parser = parser;
        this.productions = parser.getPi();
        this.outputFile = outputFile;
        generateTree();
    }

    public void generateTree() {
        Stack<Node> nodeStack = new Stack<>();
        var productionsIndex = 0;
        //root
        Node node = new Node();
        node.setParent(0);
        node.setSibling(0);
        node.setHasRight(false);
        node.setIndex(nodeNumber);
        nodeNumber++;
        node.setValue(parser.getGrammar().getStartingSymbol());
        nodeStack.push(node);
        nodeList.add(node);
        this.root = node;

        while(productionsIndex < productions.size() && !nodeStack.isEmpty()) {
            Node currentNode = nodeStack.peek(); //father
            if(parser.getGrammar().getSetOfTerminals().contains(currentNode.getValue()) || currentNode.getValue().contains("E")) {
                while(nodeStack.size() > 0 && !nodeStack.peek().getHasRight()) {
                    nodeStack.pop();
                }
                if(nodeStack.size() > 0)
                    nodeStack.pop();
                else
                    break;
                continue;
            }

            //children
            var production = parser.getProductionByOrderNumber(Integer.parseInt(productions.get(productionsIndex))).split(" ");
            nodeNumber += production.length-1;
            for(var i = production.length - 1; i >= 0; --i) {
                Node child = new Node();
                child.setParent(currentNode.getIndex());
                child.setValue(production[i]);
                child.setIndex(nodeNumber);
                if(i==0)
                    child.setSibling(0);
                else
                    child.setSibling(nodeNumber-1);
                child.setHasRight(i != production.length - 1);

                nodeNumber--;
                nodeStack.push(child);
                nodeList.add(child);
            }
            nodeNumber += production.length + 1;
            productionsIndex++;
        }
    }

    public void printTree(){
        try {
            nodeList.sort(Comparator.comparing(Node::getIndex));
            File file = new File(outputFile);
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("Index | Value | Parent | Sibling"+ "\n");
            System.out.println("Index | Value | Parent | Sibling");
            for (Node node : nodeList) {
                bufferedWriter.write(node.getIndex() + " | " + node.getValue() + " | " + node.getParent() + " | " + node.getSibling() + "\n");
                System.out.println(node.getIndex() + " | " + node.getValue() + " | " + node.getParent() + " | " + node.getSibling());
            }
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
