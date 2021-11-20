package com.coinbase.solution.consumer;


import java.util.*;

class Solver {
    static class Rate {
        String fromCurrency;
        String toCurrency;
        double amount;
        
        public Rate (String fromCurrency, String toCurrency, double amount) {
          this.fromCurrency = fromCurrency;
          this.toCurrency = toCurrency;
          this.amount = amount;
        }
    }

    private static class Edge implements Comparable<Edge> {
        String currency;
        String parent;
        double cost;

        public Edge(String parent, String currency, double cost) {
            this.currency = currency;
            this.parent = parent;
            this.cost = cost;
        }

        @Override
        public int compareTo(Edge o) {
            if (this.cost == o.cost) {
                return 1;
            } else {
                return Double.compare(this.cost, o.cost);
            }
        }
    }

    HashMap<String, HashMap<String, Double>> graph = new HashMap<>();

    public double solve(List<Rate> rates, String from, String to) {

        buildGraph(rates);

        if (!graph.containsKey(from)) {
            return -1;
        }

        Queue<Edge> queue = new PriorityQueue<>();

        queue.add(new Edge(null, from, 1.0));

        //Visited  Set. Contains currency (node) and the lowest cost seen so far
        Map<String, Double> vertexSet = new HashMap<>();

        Map<String, String> parent = new HashMap<>();
        //Parent will be used to keep the path lineage. Useful to show path
        parent.put(from, from);
        while (!queue.isEmpty()) {

            Edge edge = queue.poll();
            String top = edge.currency;

            if (vertexSet.containsKey(top) && vertexSet.get(top) < edge.cost) {
                continue;
            }

            //Update new lowest cost and parent
            vertexSet.put(top, edge.cost);
            parent.put(top, edge.parent);

            //Keep building edges
            graph.get(from).put(top, edge.cost);

            //We have found the shortest edge to destination currency, no need to continue
            if(top.equals(to))
                break;

            for (var dest : graph.get(top).entrySet()) {
                double rate = graph.get(from).get(top) * dest.getValue();

                if (vertexSet.containsKey(dest.getKey()) && vertexSet.get(dest.getKey()) >= rate || dest.getKey().equals(from)) {
                    continue;
                }
                queue.add(new Edge(top, dest.getKey(), rate));
            }
        }


        if (!graph.get(from).containsKey(to)) {
            return -1;
        }

        System.out.println(getPath(parent, from, to));
        return graph.get(from).get(to);
    }
//This code is added for debugging. Will just print the path
    private String getPath(Map<String, String> parent, String from, String to) {
        System.out.println(parent);
        String current = to;
        System.out.println(current);
        Stack<String> res = new Stack<>();
        res.add(to);
        while (!parent.get(current).equals(from)) {
            current = parent.get(current);
            res.add(current);
        }

        StringBuilder stringBuilder = new StringBuilder();
        res.add(from);

        while (res.size() != 0) {

            stringBuilder.append(res.pop());
            if (res.size() > 0) {
                stringBuilder.append(" -> ");
            }
        }
        return stringBuilder.toString();
    }

    private void buildGraph(List<Rate> rates) {
        //Put Rates as Given
        for (Rate rate : rates) {
            if (!graph.containsKey(rate.fromCurrency)) {
                graph.put(rate.fromCurrency, new HashMap<>());
            }

            graph.get(rate.fromCurrency).put(rate.fromCurrency, 1.0);

            graph.get(rate.fromCurrency).put(rate.toCurrency, rate.amount);
        }

        //Add Reverse Rates
        for (Rate rate : rates) {
            for (var val : graph.get(rate.fromCurrency).entrySet()) {
                if (!graph.containsKey(val.getKey())) {
                    graph.put(val.getKey(), new HashMap<>());
                }

                graph.get(val.getKey()).put(val.getKey(), 1.0);

                if (!graph.get(val.getKey()).containsKey(rate.fromCurrency)) {
                    graph.get(val.getKey()).put(rate.fromCurrency, 1.0 / val.getValue());
                }
            }
        }
    }
    
    public static void main(String[] args) {

        Solver solution = new Solver();
        //Move this to the impl
        List<Solver.Rate> rates = Arrays.asList(
                new Solver.Rate("USD", "JPY", 110),
                //I was doing just "1/1000" in the coding excercise without casting to double
                //Old:
                //new Solver.Rate("USD", "AUD", 1d/100d),
                //New:
                new Solver.Rate("USD", "AUD", 1d/100d),
                new Solver.Rate("USD", "XYZ", 1),
                new Solver.Rate("XYZ", "MOS", 2),
                new Solver.Rate("AUD", "MOS", 4),
                new Solver.Rate("MOS", "JPY", 2)
        );

        String from = "USD";
        String to = "MOS";

        double result = solution.solve(rates, from, to);

        System.out.println(result);
    }

}



    

